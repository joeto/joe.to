package to.joe.manager;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.util.Flag;
import to.joe.util.Vanish;

/**
 * Ministry of Truth
 * 
 * WAR IS PEACE FREEDOM IS SLAVERY IGNORANCE IS STRENGTH
 * 
 * Also, handles admins going invisible And join/quits
 * 
 * 
 */
public class Minitrue {
    public J2 j2;
    public Vanish vanish = new Vanish(this);

    public Minitrue(J2 j2) {
        this.j2 = j2;
    }

    /**
     * Function for restarting the manager. Currently empty.
     */
    public void restartManager() {

    }

    /**
     * Called when a player has joined the game.
     * 
     * @param player
     *            The player who is joining.
     */
    public void processJoin(Player player, boolean quiet) {
        if (!quiet) {
            this.announceJoin(player.getName(), false);
        } else if (this.j2.hasFlag(player, Flag.SILENT_JOIN) && !this.invisible(player)) {
            this.vanish.callVanish(player);
        }
    }

    /**
     * Called when a player leaves the game. If they're invisible, only tell
     * admins.
     * 
     * @param player
     *            The player who is leaving.
     */
    public void processLeave(Player player) {
        if (!this.invisible(player)) {
            this.announceLeave(player.getName(), false);
        } else {
            this.j2.chat.messageByFlag(Flag.ADMIN, ChatColor.YELLOW + player.getName() + " quit, only admins saw");
        }
        this.vanish.removeInvisibility(player);
    }

    /**
     * Toggle vanishing act (callVanish), Announce to players that user has
     * joined/left, Tell admins the truth.
     * 
     * @param player
     *            User who is changing visibility
     */
    public void vanish(Player player) {
        this.vanish.callVanish(player);
        if (this.invisible(player)) {// assume player is NOW invisible
            this.announceLeave(player.getName(), true);
            this.j2.chat.messageByFlag(Flag.ADMIN, ChatColor.YELLOW + player.getName() + " is now SUPER STEALTHILY INVISIBLE");
        } else {// now visible
            this.announceJoin(player.getName(), true);
            this.j2.chat.messageByFlag(Flag.ADMIN, ChatColor.YELLOW + player.getName() + " is now visible to all");
        }
    }

    /**
     * Is the user currently invisible?
     * 
     * @param player
     *            Player in question
     * @return if player is invisible
     */
    public boolean invisible(Player player) {
        return this.vanish.isInvisible(player);
    }

    /**
     * Player joins game. Optional sneakiness.
     * 
     * @param playerName
     *            Name of joiner
     * @param sneaky
     *            If true, only tell admins the player has joined.
     */
    public void announceJoin(String playerName, boolean sneaky) {
        final String message = ChatColor.YELLOW + "Joining: " + playerName;
        if (sneaky) {
            this.j2.chat.messageByFlagless(Flag.ADMIN, message);
        } else {
            this.j2.chat.messageAll(message);
        }
    }

    /**
     * Announcement of leaving. Optional sneakiness.
     * 
     * @param playerName
     *            Player leaving
     * @param sneaky
     *            If true, only tell admins.
     */
    public void announceLeave(String playerName, boolean sneaky) {
        final String message = ChatColor.YELLOW + "Leaving: " + playerName;
        if (sneaky) {
            this.j2.chat.messageByFlagless(Flag.ADMIN, message);
        } else {
            if (this.j2.users.isOnline(playerName)) {
                this.j2.chat.messageAll(message);
            }
        }
    }

    /**
     * A catch on player chat. All chat goes through here. If the player is
     * invisible, only messages admins.
     * 
     * @param player
     *            Player trying to chat
     * @param message
     *            Their message
     * @return true if handled by this system. false if it should be handled by
     *         regular chat.
     */
    public boolean chat(Player player, String message) {
        if (this.invisible(player)) {
            this.j2.chat.adminOnlyMessage(player.getName(), message);
            return true;
        }
        return false;
    }

    /**
     * @return How many people are invisible
     */
    public int invisibleCount() {
        return this.vanish.invisibleCount();
    }

    /**
     * Processing of the /who command Will only show invisible players to admins
     * and console Adds special coloring for admin viewing.
     * 
     * @param sender
     */
    public void who(CommandSender sender) {
        final Player[] players = this.j2.getServer().getOnlinePlayers();
        final boolean isAdmin = this.qualified(sender);
        int curlen = 0;
        final int maxlen = 320;
        int playercount = players.length;
        if (!isAdmin) {
            playercount -= this.invisibleCount();
        }
        String msg = "Players (" + playercount + "/" + this.j2.config.access_max_players + "):";

        for (final char ch : msg.toCharArray()) {
            curlen += Chats.characterWidths[ch];
        } // now we have our base length

        for (final Player p : players) {
            if (!p.isOnline()) {
                continue;
            }
            final boolean invis = this.j2.minitrue.invisible(p);
            if (!invis || isAdmin) {
                final String name = p.getName();
                String cname = ChatColor.WHITE + name;
                try {
                    cname = this.j2.users.getUser(name).getColorName();
                } catch (final Exception e) {
                    this.j2.users.addUser(name);
                    this.j2.users.processJoin(p, true);
                    cname = ChatColor.GREEN + name;
                }
                if (isAdmin) {
                    if (this.j2.hasFlag(p, Flag.TRUSTED)) {
                        cname = ChatColor.DARK_GREEN + name;
                    }
                    if (this.j2.hasFlag(p, Flag.ADMIN)) {
                        if (invis) {
                            cname = ChatColor.AQUA + name;
                        } else {
                            cname = ChatColor.RED + name;
                        }
                    }
                    if (this.j2.hasFlag(p, Flag.MUTED)) {
                        cname = ChatColor.YELLOW + name;
                    }
                    if (this.j2.hasFlag(p, Flag.NSA)) {
                        cname += ChatColor.DARK_AQUA + "«»";
                    }
                    if (this.j2.hasFlag(p, Flag.THOR)) {
                        cname += ChatColor.WHITE + "/";
                    }
                    if (this.j2.hasFlag(p, Flag.GODMODE)) {
                        cname += ChatColor.DARK_RED + "⌂";
                    }
                    if (this.j2.hasFlag(p, Flag.TOOLS)) {
                        cname += ChatColor.AQUA + "¬";
                    }
                    if (this.j2.hasFlag(p, Flag.JAILED)) {
                        cname += ChatColor.GRAY + "[ø]";
                    }
                }
                cname += ChatColor.WHITE.toString();
                int thislen = 0;
                for (final char ch : name.toCharArray()) {
                    thislen += Chats.characterWidths[ch];
                }
                if ((thislen + 1 + curlen) > maxlen) {
                    this.send(sender, msg);
                    msg = cname;
                } else {
                    msg += " " + cname;
                }
            }
        }
        this.send(sender, msg);
    }

    /**
     * Sends a response to the commandsender Sends as player message if player
     * Sends as log if console
     * 
     * @param sender
     *            Target of the message
     * @param message
     *            Message to send
     */
    public void send(CommandSender sender, String message) {
        if (sender != null) {
            sender.sendMessage(message);
        } else {
            this.j2.log(message);
        }
    }

    /**
     * Query for determining if a CommandSender counts as "admin"
     * 
     * @param sender
     * @return if CommandSender has admin rights.
     */
    public boolean qualified(CommandSender sender) {
        if ((sender != null) && (sender instanceof Player)) {
            return this.j2.hasFlag((Player) sender, Flag.ADMIN);
        } else {
            return true;
        }
    }

    /**
     * Method for finding a player that takes invisibility into account Only
     * gives an invisible player to admins
     * 
     * @param name
     *            Name being searched
     * @param isAdmin
     *            If the searcher is an admin
     * @return a list of player matches, filtered for invisibility
     */
    public List<Player> matchPlayer(String name, boolean isAdmin) {
        final List<Player> players = this.j2.getServer().matchPlayer(name);
        if (!isAdmin) {
            final ArrayList<Player> toremove = new ArrayList<Player>();
            for (final Player p : players) {
                if ((p != null) && this.invisible(p)) {
                    toremove.add(p);
                }
            }
            players.removeAll(toremove);
        }
        return players;
    }

    /**
     * It's getPlayer with protection
     * 
     * @param name
     * @param isAdmin
     * @return
     */
    public Player getPlayer(String name, boolean isAdmin) {
        final Player target = this.j2.getServer().getPlayer(name);
        if ((target != null) && target.isOnline() && !this.invisible(target)) {
            return target;
        }
        return null;
    }

    /**
     * Invisibility filter for getOnlinePlayers
     * 
     * @return A filtered online list
     */
    public Player[] getOnlinePlayers() {
        final Player[] players = this.j2.getServer().getOnlinePlayers();
        final Player[] toreturn = new Player[players.length - this.invisibleCount()];
        int cur = 0;
        for (final Player p : players) {
            if (!this.invisible(p)) {
                toreturn[cur++] = p;
            }
        }
        return toreturn;
    }
}
