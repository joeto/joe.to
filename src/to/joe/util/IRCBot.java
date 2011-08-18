package to.joe.util;

import org.bukkit.entity.Player;
import org.jibble.pircbot.PircBot;

import to.joe.manager.IRC;

public class IRCBot extends PircBot {

    private final IRC manager;

    private final boolean useMsgCmd;

    public IRCBot(String name, boolean useMsgCmd, IRC irc) {
        this.setName(name);
        this.setAutoNickChange(true);
        this.useMsgCmd = useMsgCmd;
        this.manager = irc;
        this.setMessageDelay(1100);
    }

    @Override
    public void onDisconnect() {
        if (this.manager.getJ2().ircEnable) {
            this.manager.restart = true;
            this.manager.getJ2().ircEnable = false;
            this.dispose();
        }
    }

    @Override
    public void onInvite(String targetNick, String sourceNick, String sourceLogin, String sourceHostname, String channel) {
        if (targetNick.equalsIgnoreCase(this.getNick()) && channel.equalsIgnoreCase(this.manager.getJ2().ircAdminChannel)) {
            this.joinChannel(channel);
        }
    }

    @Override
    public void onMessage(String channel, String sender, String login, String hostname, String message) {
        if (message.charAt(0) == '!') {
            final String[] parts = message.split(" ");
            if (parts[0].toLowerCase().equals("!help")) {
                this.sendNotice(sender, "!msg - Send a message to players on the server | !players - Get current playercount | !playerlist - List players online");
            } else if (message.equalsIgnoreCase("!players") || message.equalsIgnoreCase("!playerlist")) {
                String playerList = "";
                int playerCount = 0;
                Player[] players;
                if (channel.equalsIgnoreCase(this.manager.getJ2().ircAdminChannel)) {
                    players = this.manager.getJ2().getServer().getOnlinePlayers();
                } else {
                    players = this.manager.getJ2().minitrue.getOnlinePlayers();
                }
                for (final Player player : players) {
                    if (player != null) {
                        if (playerList.equals("")) {
                            playerList += player.getName();
                        } else {
                            playerList += ", " + player.getName();
                        }
                        playerCount++;
                    }
                }
                if (playerList == "") {
                    this.sendMessage(channel, "No players online.");
                } else {
                    if (message.equalsIgnoreCase("!players")) {
                        this.sendMessage(channel, "Currently " + playerCount + " of " + this.manager.getJ2().playerLimit + " on the server");
                    } else {
                        this.sendMessage(channel, "Players (" + playerCount + " of " + this.manager.getJ2().playerLimit + "): " + playerList);
                    }
                }
            } else if (message.equalsIgnoreCase("!admins")) {
                String adminList = "Admins: ";
                for (final Player player : this.manager.getJ2().getServer().getOnlinePlayers()) {
                    if ((player != null) && (this.manager.getJ2().hasFlag(player, Flag.ADMIN))) {
                        if (adminList == "Admins: ") {
                            adminList += player.getName();
                        } else {
                            adminList += ", " + player.getName();
                        }
                    }
                }
                final boolean adminsOnline = !adminList.equals("Admins: ");
                if (channel.equalsIgnoreCase(this.manager.getJ2().ircAdminChannel)) {
                    if (!adminsOnline) {
                        this.sendMessage(channel, "No admins online.");
                    } else {
                        this.sendMessage(channel, adminList);
                    }
                } else {
                    if (!adminsOnline) {
                        this.sendMessage(channel, "No admins online. Find one on #joe.to or #minecraft");
                    } else {
                        this.sendMessage(channel, "There are admins online!");
                    }
                }
            } else if (this.useMsgCmd && parts[0].equalsIgnoreCase("!msg")) {
                if (channel.equalsIgnoreCase(this.manager.getJ2().ircAdminChannel)) {
                    this.sendMessage(channel, "Try that in the other channel.");
                } else {
                    this.handleMessage(channel, sender, this.manager.getJ2().combineSplit(1, parts, " "));
                }
            } else if (parts[0].equalsIgnoreCase("!broadcast")) {
                if (!channel.equalsIgnoreCase(this.manager.getJ2().ircAdminChannel)) {
                    this.sendMessage(channel, "Try that in the other channel.");
                } else {
                    this.manager.getJ2().chat.handleBroadcastFromIRC(sender, this.manager.getJ2().combineSplit(1, parts, " "));
                }
            } else if (parts[0].equalsIgnoreCase("!reports")) {
                if (!channel.equalsIgnoreCase(this.manager.getJ2().ircAdminChannel)) {
                    this.sendMessage(channel, "Try that in the other channel.");
                } else {
                    String response = "";
                    final int size = this.manager.getJ2().reports.getReports().size();
                    response = "There are currently " + size + " reports open. ";
                    switch (size) {
                        case 0:
                            response += "\\o/";
                            break;
                        case 1:
                            response += ":|";
                            break;
                        case 2:
                            response += ":(";
                            break;
                        case 3:
                            response += ":'(";
                            break;
                        case 4:
                            response += "D:";
                            break;
                        default:
                            response += "Seriously guys? Start cleaning up.";
                            break;
                    }
                    this.sendMessage(channel, response);
                }
            } else if (this.useMsgCmd && parts[0].equalsIgnoreCase("!me")) {
                if (channel.equalsIgnoreCase(this.manager.getJ2().ircAdminChannel)) {
                    this.sendMessage(channel, "Try that in the other channel.");
                }
                this.handleMeMessage(channel, sender, this.manager.getJ2().combineSplit(1, parts, " "));
            } else if (parts[0].equalsIgnoreCase("!has")) {
                if (!channel.equalsIgnoreCase(this.manager.getJ2().ircAdminChannel)) {
                    this.sendMessage(channel, "Try that in the other channel.");
                }
                String playerName = null;
                for (final Player player : this.manager.getJ2().getServer().getOnlinePlayers()) {
                    final String testName = player.getName();
                    if (testName.equalsIgnoreCase(parts[1])) {
                        playerName = testName;
                    }
                }
                if (playerName != null) {
                    this.sendMessage(channel, "I has " + playerName + "!");
                }
            }
            return;
        }
        if ((message.charAt(0) == '.') && channel.equalsIgnoreCase(this.manager.getJ2().ircChannel)) {
            final String[] parts = message.split(" ");
            if (this.manager.ircCommand(hostname, sender, parts)) {
                this.sendRawLine("NOTICE " + sender + " :Done");
            } else if (!this.useMsgCmd) {
                this.handleMessage(channel, sender, " " + message);
            }
            return;
        }
        if (message.equals("A MAN IN BRAZIL IS COUGHING")) {
            this.manager.cough(hostname);
        }
        if (!this.useMsgCmd) {
            this.handleMessage(channel, sender, " " + message);
        }
    }

    private void handleMessage(String channel, String sender, String message) {
        this.manager.getJ2().chat.handleIRCChat(sender, message, false, channel);
    }

    private void handleMeMessage(String channel, String sender, String message) {
        this.manager.getJ2().chat.handleIRCChat(sender, message, true, channel);
    }

    @Override
    protected void onPrivateMessage(String sender, String login, String hostname, String message) {
        if (this.manager.ircCommand(hostname, sender, message.split(" "))) {
            this.sendRawLine("NOTICE " + sender + " :Done");
        } else {
            this.sendRawLine("NOTICE " + sender + " :No access to that command");
        }
    }

}
