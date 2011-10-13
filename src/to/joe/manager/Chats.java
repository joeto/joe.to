package to.joe.manager;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.util.Flag;
import to.joe.util.User;

/**
 * Manager for handling chatting
 * 
 */
public class Chats {
    private final String[] randomColorList;
    private final J2 j2;
    public boolean muteAll;

    public Chats(J2 j2p) {
        this.j2 = j2p;
        // colorslist, minus lightblue white and purple
        this.randomColorList = new String[11];
        this.randomColorList[0] = ChatColor.BLUE.toString();
        this.randomColorList[1] = ChatColor.DARK_PURPLE.toString();
        this.randomColorList[2] = ChatColor.GOLD.toString();
        this.randomColorList[3] = ChatColor.GRAY.toString();
        this.randomColorList[4] = ChatColor.GREEN.toString();
        this.randomColorList[5] = ChatColor.DARK_GRAY.toString();
        this.randomColorList[6] = ChatColor.DARK_GREEN.toString();
        this.randomColorList[7] = ChatColor.DARK_AQUA.toString();
        this.randomColorList[8] = ChatColor.DARK_RED.toString();
        this.randomColorList[9] = ChatColor.RED.toString();
        this.randomColorList[10] = ChatColor.DARK_BLUE.toString();
        this.restartManager();
    }

    /**
     * Restart manager. Sets muteall false.
     */
    public void restartManager() {
        this.muteAll = false;
    }

    /**
     * List of char widths in-game.
     */
    public static final int[] characterWidths = new int[] { 1, 9, 9, 8, 8, 8, 8, 7, 9, 8, 9, 9, 8, 9, 9, 9, 8, 8, 8, 8, 9, 9, 8, 9, 8, 8, 8, 8, 8, 9, 9, 9, 4, 2, 5, 6, 6, 6, 6, 3, 5, 5, 5, 6, 2, 6, 2, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 2, 2, 5, 6, 5, 6, 7, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6, 4, 6, 6, 3, 6, 6, 6, 6, 6, 5, 6, 6, 2, 6, 5, 3, 6, 6, 6, 6, 6, 6, 6, 4, 6, 6, 6, 6, 6, 6, 5, 2, 5, 7, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6, 3, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6, 6, 3, 6, 6, 6, 6, 6, 6, 6, 7, 6, 6, 6, 2, 6, 6, 8, 9, 9, 6, 6, 6, 8, 8, 6, 8, 8, 8, 8, 8, 6, 6, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 6, 9, 9, 9, 5, 9, 9, 8, 7, 7, 8, 7, 8, 8, 8, 7, 8, 8, 7, 9, 9, 6, 7, 7, 7, 7, 7, 9, 6,
            7, 8, 7, 6, 6, 9, 7, 6, 7, 1 };

    /**
     * @return List of acceptable colors for randomization
     */
    public String[] getRandomColorList() {
        return this.randomColorList;
    }

    /**
     * Send message only to players with named flag
     * 
     * @param flag
     * @param message
     */
    public void messageByFlag(Flag flag, String message) {
        for (final Player plr : this.j2.getServer().getOnlinePlayers()) {
            if ((plr != null) && this.j2.hasFlag(plr, flag)) {
                plr.sendMessage(message);
            }
        }
    }

    /**
     * Send message only to players WITHOUT named flag
     * 
     * @param flag
     * @param message
     */
    public void messageByFlagless(Flag flag, String message) {
        for (final Player plr : this.j2.getServer().getOnlinePlayers()) {
            if ((plr != null) && !this.j2.hasFlag(plr, flag)) {
                plr.sendMessage(message);
            }
        }
    }

    /**
     * Send message to all players
     * 
     * @param message
     */
    public void messageAll(String message) {
        for (final Player p : this.j2.getServer().getOnlinePlayers()) {
            if (p != null) {
                if (!this.j2.hasFlag(p, Flag.SHUT_OUT_WORLD)) {
                    p.sendMessage(message);
                }
            }
        }
    }

    /**
     * Admin-only chat.
     * 
     * @param name
     *            Sender
     * @param message
     */
    public void adminOnlyMessage(String name, String message) {
        final String msg = "<" + ChatColor.LIGHT_PURPLE + name + ChatColor.WHITE + "> " + message;
        this.j2.sendAdminPlusLog(msg);
        this.j2.serverLink.broadcastChat(msg);
    }

    /**
     * Message from admin to all players. Sender appears as ADMIN except to
     * admins.
     * 
     * @param name
     *            Sender
     * @param message
     */
    public void globalAdminMessage(String name, String message) {
        final String amessage = "<" + name + "> " + ChatColor.LIGHT_PURPLE + message;
        final String pmessage = "<ADMIN> " + ChatColor.LIGHT_PURPLE + message;
        final String imessage = "<ADMIN> " + message;
        this.messageByFlagless(Flag.ADMIN, pmessage);
        this.j2.sendAdminPlusLog(amessage);
        this.j2.irc.messageRelay(imessage);
    }

    private String formatNamelyArea(String name, ChatColor color, boolean me) {
        String colorName = "";
        if (color != null) {
            colorName = color + name;
        } else {
            final String[] colorlist = this.j2.chat.getRandomColorList();
            final int size = colorlist.length;
            final int rand = this.j2.random.nextInt(size);
            if (rand < size) {
                colorName = colorlist[rand] + name;
            } else {
                for (int x = 0; x < name.length(); x++) {
                    colorName += colorlist[this.j2.random.nextInt(size)] + name.charAt(x);
                }
            }
        }
        if (me) {
            return "* " + colorName + " ";
        } else {
            return "<" + colorName + ChatColor.WHITE + "> ";
        }
    }

    /**
     * Handle a message sent. Includes anti-spam measures And bigotry detection
     * 
     * @param player
     * @param chat
     *            Message from the player
     * @param me
     *            Is the message a /me message
     */
    public void handleChat(Player player, String chat, boolean me) {

        if (this.j2.minitrue.chat(player, chat)) {
            return;
        }
        final String name = player.getName();
        final String chatlc = chat.toLowerCase();

        if (chatlc.contains("nigg") || chatlc.contains("fag")) {
            final String msg = ChatColor.RED + "Watch " + ChatColor.LIGHT_PURPLE + name + ChatColor.RED + " for language: "+chat;
            this.j2.sendAdminPlusLog(msg);
            this.j2.irc.messageAdmins(ChatColor.stripColor(msg));
        }

        if (((this.muteAll && !this.j2.hasFlag(player, Flag.ADMIN)) || this.j2.hasFlag(player, Flag.MUTED))) {
            player.sendMessage(ChatColor.RED + "You are currently muted");
            final String message = this.formatNamelyArea(name, ChatColor.YELLOW, me) + chat;
            this.messageByFlag(Flag.ADMIN, message);
            this.j2.log(message);
            return;
        }

        ChatColor color = null;
        if (!this.j2.config.general_random_namecolor) {
            color = this.j2.users.getUser(player).getColor();
        }
        final String message = this.formatNamelyArea(name, color, me) + chat;

        if (me) {
            this.j2.irc.messageRelay("* " + name + " " + chat);
        } else {
            this.j2.irc.messageRelay("<" + name + "> " + chat);
        }
        // j2.irc.chatQueue.offer("<"+name+"> "+chat);
        this.j2.log(message);
        this.messageAll(message);

    }

    /**
     * Handles a message coming from IRC. Does not send if all players muted.
     * 
     * @param name
     *            Sender of the message
     * @param message
     * @param me
     *            Is the message a /me message
     * @param channel
     *            Channel message was sent from
     */
    public void handleIRCChat(String name, String message, boolean me, String channel) {
        if (this.muteAll) {
            this.j2.irc.getBot().sendMessage(channel, "All players currently muted. Message will not go through.");
            return;
        }
        String combined;
        if (me) {
            combined = "* " + this.j2.config.irc_ingame_color + name + ChatColor.WHITE + " "+ message;
        } else {
            combined = this.j2.config.irc_ingame_separator[0] + this.j2.config.irc_ingame_color + name + ChatColor.WHITE + this.j2.config.irc_ingame_separator[1] + " " + message;
        }

        if (combined.length() > this.j2.config.irc_char_limit) {
            this.j2.irc.getBot().sendMessage(channel, name + ": Your message was too long. The limit's " + this.j2.config.irc_char_limit + " characters");
        } else {
            this.j2.log("IRC:" + combined);
            this.messageAll(combined);
            if (this.j2.config.irc_echo_messages) {
                if (me) {
                    this.j2.irc.getBot().sendMessage(channel, "[IRC] *" + name + " " + message);
                } else {
                    this.j2.irc.getBot().sendMessage(channel, "[IRC] <" + name + "> " + message);
                }
            }
        }
    }

    /**
     * Takes a admin broadcast from the admin irc channel
     * 
     * @param from
     *            Sender of the message
     * @param message
     */
    public void handleBroadcastFromIRC(String from, String message) {
        this.j2.sendAdminPlusLog(ChatColor.AQUA + "Server-wide message from " + from);
        this.adminOnlyMessage("irc-" + from, message);
    }

    /**
     * Handle a /msg, secretly sends to any listening admins
     * 
     * @param from
     *            Sender
     * @param to
     *            Receiver
     * @param message
     */
    public void handlePrivateMessage(Player from, Player to, String message) {
        if (to.equals(from)) {
            to.sendMessage(ChatColor.RED + "I think you're lonely.");
            return;
        }
        final User userTo = this.j2.users.getUser(to);
        final User userFrom = this.j2.users.getUser(from);
        final String colorTo = userTo.getColorName();
        final String colorFrom = userFrom.getColorName();
        final String complete = ChatColor.WHITE + "<" + colorFrom + "->" + colorTo + "> " + message;
        if (this.j2.hasFlag(from, Flag.MUTED)) {
            from.sendMessage(ChatColor.RED + "You are muted");
        } else {
            to.sendMessage(complete);
            from.sendMessage(complete);
        }
        final String nsaified = this.nsaify(complete);
        for (final Player p : this.j2.getServer().getOnlinePlayers()) {
            if ((p != null) && p.isOnline() && !p.equals(from) && !p.equals(to) && this.j2.hasFlag(p, Flag.NSA)) {
                p.sendMessage(nsaified);
            }
        }
        this.j2.log(complete);
    }

    public boolean isSpam(Player player, String text) {
        final User user = this.j2.users.getUser(player);
        final String name = player.getName();
        final int spamCount = user.spamCheck(text);
        if (spamCount > 0) {
            switch (spamCount) {
                case 3:
                    player.sendMessage(ChatColor.RED + "You will be kicked if you continue");
                    this.j2.sendAdminPlusLog(ChatColor.LIGHT_PURPLE + "Warned " + name + " for spam. Kicking if continues.");
                    this.j2.debug("User " + name + " warned for spam");
                    break;
                case 5:
                    this.j2.kickbans.spamKick(player);
                    break;
                default:
                    player.sendMessage(ChatColor.RED + "Repeat message or too fast, blocked.");
                    this.j2.debug("User " + name + " is spamming - " + spamCount);
                    break;
            }
            return true;
        }
        return false;
    }

    /*
     * private HashMap<Integer,ChatChannel> channels; public void
     * addChannel(ChatChannel chan){ //j2.mysql.chanAdd(chan);
     * channels.put(chan.getID(),chan); } public void dropChannel(int id){
     * //j2.mysql.chanDrop(id); if(channels.containsKey(id)){
     * channels.remove(id); } } public ChatChannel getChannel(String name){
     * return channels.get(name); } public void loadChannel(ChatChannel chan){
     * channels.put(chan.getID(), chan); }
     */

    private String nsaify(String string) {
        return string.replace(ChatColor.WHITE.toString(), ChatColor.DARK_AQUA.toString());
    }

    /*
     * public void logChat(String name, String message) { this is a terrible,
     * horrible idea. Never do it again.
     * 
     * 
     * Connection conn = null; PreparedStatement ps = null; try { conn =
     * getConnection(); Date curTime=new Date(); ps =
     * conn.prepareStatement("INSERT INTO " + chatTable +
     * " (time, name, message) VALUES (?,?,?)"); ps.setLong(1,
     * curTime.getTime()); ps.setString(2, name); ps.setString(3, message);
     * ps.executeUpdate(); } catch (SQLException ex) {
     * 
     * } finally { try { if (ps != null) { ps.close(); } if (conn != null) {
     * conn.close(); } } catch (SQLException ex) { } } }
     */
}
