package to.joe.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

import org.bukkit.Location;

import to.joe.J2;
import to.joe.util.Flag;
import to.joe.util.User;
import to.joe.util.IRCBot;

/**
 * Manager for the IRC relay
 * 
 */
public class IRC {
    private final J2 j2;
    private IRCBot bot;
    private final Object adminsLock = new Object();
    private HashMap<String, String> admins;// hostname->name
    private HashMap<String, Long> msgs;
    public LinkedBlockingQueue<String> chatQueue = new LinkedBlockingQueue<String>();

    // private boolean stop = false;
    // private HashMap<String,Long> recent;

    public IRC(J2 j2p) {
        this.j2 = j2p;
        this.cleanStartup();
    }

    public void reloadIRCAdmins() {
        this.admins = this.j2.mysql.getIRCAdmins();
    }

    /**
     * Restarts the bot.
     */
    public void restartManager() {
        this.cleanStartup();
        if (this.bot != null) {
            this.bot.quitServer("Restarting...");
        }
        this.connectAndAuth();
    }

    /**
     * Load IRC admins, clear message queue.
     */
    private void cleanStartup() {
        this.msgs = new HashMap<String, Long>();
        // this.recent=new HashMap<String,Long>();
    }

    /**
     * Checks if a string matches a previously submitted string from the last
     * hour If it's a repeat ban announce, dont' send.
     * 
     * @param string
     * @return
     */
    private boolean goodToGo(String string) {
        if (string.startsWith("[J2BANS]") || string.startsWith("[BANS]")) {
            final long rightNow = (new Date()).getTime();
            if (this.msgs.containsKey(string)) {
                if ((this.msgs.get(string).longValue() + 3600000L) < rightNow) {
                    this.msgs.remove(string);
                    this.msgs.put(string, rightNow);
                    return true;
                } else {
                    return false;
                }
            } else {
                this.msgs.put(string, rightNow);
                return true;
            }
        }
        return true;
    }

    /**
     * Announces player joining - currently disabled too spammy Also queries to
     * see if it's not in the admin channel
     * 
     * @param name
     */
    public void processJoin(String name) {
        if (this.j2.ircEnable && (this.j2.getServer().getOnlinePlayers().length < 2)) {
            // j2.irc.ircMsg(name+" has logged in");
            this.j2.irc.adminChannel();
        }
    }

    /**
     * Announces player leaving - currently disabled too spammy
     * 
     * @param name
     */
    public void processLeave(String name) {
        if (this.j2.ircEnable && (this.j2.getServer().getOnlinePlayers().length < 10)) {
            // j2.irc.ircMsg(name+" has left the server");
        }
    }

    /**
     * Connecting to IRC, authenticating, etc
     */
    public void connectAndAuth() {

        this.bot = new IRCBot(this.j2.ircName, this.j2.ircMsg, this);

        if (this.j2.ircDebug) {
            this.bot.setVerbose(true);
        }
        System.out.println("Connecting to " + this.j2.ircChannel + " on " + this.j2.ircHost + ":" + this.j2.ircPort + " as " + this.j2.ircName);
        try {
            this.bot.connect(this.j2.ircHost, this.j2.ircPort, this.j2.getServer().getIp());
        } catch (final Exception e) {
            e.printStackTrace();
        }
        if (this.j2.gsAuth != "") {
            this.bot.sendMessage("authserv@services.gamesurge.net", "auth " + this.j2.gsAuth + " " + this.j2.gsPass);
            this.bot.sendMessage("ChanServ", "inviteme " + this.j2.ircAdminChannel);
        }
        this.bot.joinChannel(this.j2.ircChannel);
        if (this.j2.ircOnJoin != "") {
            this.bot.sendMessage(this.j2.ircChannel, this.j2.ircOnJoin);
        }
        this.reloadIRCAdmins();
    }

    /**
     * Murder the bot
     */
    public void kill() {
        if (this.bot != null) {
            this.bot.disconnect();
        }
    }

    /**
     * Acquires the bot
     * 
     * @return the bot
     */
    public IRCBot getBot() {
        return this.bot;
    }

    /**
     * If not connected to admin channel, join it.
     */
    private void adminChannel() {
        if (this.j2.ircEnable && !(new ArrayList<String>(Arrays.asList((this.bot.getChannels()))).contains(this.j2.ircAdminChannel))) {
            if (this.j2.gsAuth != "") {
                this.bot.sendMessage("authserv@services.gamesurge.net", "auth " + this.j2.gsAuth + " " + this.j2.gsPass);
                this.bot.sendMessage("ChanServ", "inviteme " + this.j2.ircAdminChannel);
                this.bot.joinChannel(this.j2.ircAdminChannel);
            }
        }
    }

    /**
     * check if the hostmask is an admin
     * 
     * @param hostname
     * @return
     */
    private boolean isIRCAuth(String hostname) {
        synchronized (this.adminsLock) {
            return this.admins.containsKey(hostname);
        }
    }

    /**
     * Send a message to target
     * 
     * @param target
     * @param message
     */
    public void message(String target, String message) {
        if (this.j2.ircEnable) {
            this.bot.sendMessage(target, message);
        }
    }

    /**
     * Process attempted command
     * 
     * @param hostname
     * @param nick
     * @param command
     * @return
     */
    public boolean ircCommand(String hostname, String nick, String[] command) {
        if (!this.j2.ircEnable) {
            return false;
        }
        @SuppressWarnings("unused")
        int lvl = 0;
        final String commands = this.j2.combineSplit(0, command, " ");
        final String[] args = new String[command.length - 1];
        System.arraycopy(command, 1, args, 0, command.length - 1);
        String adminName = "";
        if (!this.isIRCAuth(hostname)) {
            this.j2.log("Failed IRC command: " + hostname + " tried: " + commands);
            return false;
        } else {
            adminName = this.admins.get(hostname);
        }
        final User user = this.j2.mysql.getUser(adminName);
        final String group = user.getGroup();
        final ArrayList<Flag> flags = this.j2.users.getGroupFlags(group);
        flags.addAll(user.getUserFlags());
        if (flags.contains(Flag.SRSTAFF)) {
            lvl = 2;
        } else if (flags.contains(Flag.ADMIN)) {
            lvl = 1;
        } else {
            this.j2.log("Failed IRC command: " + nick + "(" + hostname + ") tried: " + commands);
            return false;
        }
        if (command[0].charAt(0) == '.') {
            command[0] = command[0].substring(1);
        }
        final String com = command[0].toLowerCase();
        boolean done = false;
        if (com.equals("kick") && (command.length > 2)) {
            this.j2.kickbans.callKick(command[1], adminName, this.j2.combineSplit(2, command, " "));
            // j2.kickbans.forceKick(command[1],
            // j2.combineSplit(2,command," "));
            done = true;
        }
        if (com.equals("ban") && (command.length > 2)) {
            this.j2.kickbans.callBan(adminName, args, new Location(this.j2.getServer().getWorlds().get(0), 0, 0, 0, 0, 0));
            done = true;
        }
        if (com.equals("g") && (command.length > 1)) {
            this.j2.chat.globalAdminMessage(adminName, this.j2.combineSplit(1, command, " "));
            done = true;
        }
        if (com.equals("a") && (command.length > 1)) {
            this.j2.chat.adminOnlyMessage(adminName, this.j2.combineSplit(1, command, " "));
            done = true;
        }
        if (com.equals("addban") && (command.length > 2)) {
            this.j2.kickbans.callAddBan(adminName, args, new Location(this.j2.getServer().getWorlds().get(0), 0, 0, 0, 0, 0));
            done = true;
        }
        if (com.equals("unban") && (command.length > 1)) {
            this.j2.kickbans.unban(adminName, command[1]);
            done = true;
        }
        if (com.equals("q")) {
            this.j2.users.addFlag(adminName, Flag.SILENT_JOIN);
            done = true;
        }
        if (done) {
            this.j2.log("IRC admin " + adminName + "(" + nick + "@" + hostname + ") used command: " + commands);
        } else {
            this.j2.log("IRC admin " + adminName + "(" + nick + "@" + hostname + ") tried: " + commands);
        }

        return done;
    }

    /**
     * Attempted shutdown
     * 
     * @param hostname
     */
    public void cough(String hostname) {
        if (this.isIRCAuth(hostname)) {
            this.j2.madagascar(hostname);
        }
    }

    /**
     * Reload IRC admins list
     */
    /*
     * public void loadIRCAdmins(){ String location="ircAdmins.txt"; if (!new
     * File(location).exists()) { FileWriter writer = null; try { writer = new
     * FileWriter(location); writer.write("#Add IRC admins here.\r\n");
     * writer.write("#The format is:\r\n");
     * writer.write("#NAME:HOSTNAME:ACCESSLEVEL\r\n");
     * writer.write("#Access levels: 2=kick,ban 3=everything");
     * writer.write("#Example:\r\n");
     * writer.write("#notch:pancakes.lolcakes:3\r\n"); } catch (Exception e) {
     * j2.logWarn("Exception while creating " + location); } finally { try { if
     * (writer != null) { writer.close(); } } catch (IOException e) {
     * j2.logWarn("Exception while closing writer for " + location); } } }
     * synchronized (adminsLock) { admins = new ArrayList<ircAdmin>(); try {
     * Scanner scanner = new Scanner(new File(location)); while
     * (scanner.hasNextLine()) { String line = scanner.nextLine(); if
     * (line.startsWith("#") || line.equals("") || line.startsWith("﻿")) {
     * continue; } String[] split = line.split(":"); if(split.length!=3)
     * continue; ircAdmin admin=new
     * ircAdmin(split[0],split[1],Integer.parseInt(split[2]));
     * admins.add(admin); } scanner.close(); } catch (Exception e) { j2.log(
     * "Exception while reading " + location +
     * " (Are you sure you formatted it correctly?)"); } } }
     */

    /**
     * Send message to regular channel
     * 
     * @param message
     */
    public void messageRelay(String message) {
        if (this.j2.ircEnable) {
            this.bot.sendMessage(this.j2.ircChannel, message);
        }
    }

    /**
     * Send message to admin channel
     * 
     * @param message
     */
    public void messageAdmins(String message) {
        if (this.j2.ircEnable && this.goodToGo(message)) {
            this.bot.sendMessage(this.j2.ircAdminChannel, message);
        }
    }

    /**
     * @return the manager's J2
     */
    public J2 getJ2() {
        return this.j2;
    }

    private boolean stop;
    public boolean restart = false;

    /**
     * Start 10 second checks to see if online, if in channels, etc.
     */
    public void startIRCTimer() {
        this.stop = false;
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (IRC.this.stop) {
                    timer.cancel();
                    return;
                }
                IRC.this.checkStatus();
            }
        }, 1000, 10000);
    }

    private void checkStatus() {
        if (!this.j2.ircEnable && this.restart) {
            this.connectAndAuth();
            this.restart = false;
            this.j2.ircEnable = true;
        } else {
            this.adminChannel();
        }
    }

}
