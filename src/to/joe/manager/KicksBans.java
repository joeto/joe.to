package to.joe.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.util.Ban;
import to.joe.util.Flag;

/**
 * Manager of kicking/banning
 * 
 */
public class KicksBans {
    private final J2 j2;
    public ArrayList<Ban> bans;
    private ArrayList<String> xrayers;
    private HashMap<String, Integer> sessionSpamKicks;

    public KicksBans(J2 j2p) {
        this.j2 = j2p;
        this.restartManager();
    }

    /**
     * Reset ban cache.
     */
    public void restartManager() {
        this.bans = new ArrayList<Ban>();
        this.xrayers = new ArrayList<String>();
        this.sessionSpamKicks = new HashMap<String, Integer>();
    }

    /**
     * Called for /ban
     * 
     * @param adminName
     * @param split
     *            chat split
     * @param location
     *            location of admin
     */
    public void callBan(String adminName, String[] split, Location location) {
        final List<Player> toBanCandidates = this.j2.getServer().matchPlayer(split[0]);
        if (toBanCandidates.size() != 1) {
            if (!adminName.equalsIgnoreCase("console")) {
                this.j2.getServer().getPlayer(adminName).sendMessage(ChatColor.RED + "Error:" + split[0] + " does not exist or fits multiple players");
            }
            return;
        }
        final Player toBan = toBanCandidates.get(0);
        String banReason = "";
        final long banTime = 0;
        banReason = this.j2.combineSplit(1, split, " ");
        if (toBan != null) {
            final String name = toBan.getName();
            toBan.getWorld().strikeLightningEffect(toBan.getLocation());
            this.j2.mysql.ban(name, banReason, banTime, adminName, location);
            // if (split.length > 1) {
            toBan.kickPlayer("Banned: " + banReason);
            this.j2.sendAdminPlusLog(ChatColor.RED + "Banning " + name + " by " + adminName + ": " + banReason);
            this.j2.chat.messageByFlagless(Flag.ADMIN, ChatColor.RED + name + " banned (" + banReason + ")");
            this.j2.irc.messageRelay(name + " banned (" + banReason + ")");
            /*
             * } else { toBan.kickPlayer("Banned."); j2.log.log(Level.INFO,
             * "Banning " + name + " by " + adminName);
             * j2.chat.msgByFlag(Flag.ADMIN,ChatColor.RED + "Banning " + name +
             * " by " + adminName);
             * j2.chat.msgByFlagless(Flag.ADMIN,ChatColor.RED + name +
             * " banned"); j2.irc.ircMsg(name + " banned"); }
             */
        } else {
            if (!adminName.equalsIgnoreCase("console")) {
                this.j2.getServer().getPlayer(adminName).sendMessage(ChatColor.RED + "Error:" + split[0] + " does not exist or fits multiple players");
            }
        }
    }

    /**
     * called on /addban
     * 
     * @param adminName
     * @param split
     *            command text split
     * @param location
     *            admin location
     */
    public void callAddBan(String adminName, String[] split, Location location) {
        String banReason = "";
        final long banTime = 0;
        banReason = this.j2.combineSplit(1, split, " ");
        final String name = split[0];
        this.j2.mysql.ban(name, banReason, banTime, adminName, location);
        this.forceKick(name, "Banned: " + banReason);
        // if (split.length > 1) {
        this.j2.sendAdminPlusLog(ChatColor.RED + "Banning " + name + " by " + adminName + ": " + banReason);
        /*
         * } else { j2.log.log(Level.INFO, "Banning " + name + " by " +
         * adminName); j2.chat.msgByFlag(Flag.ADMIN,ChatColor.RED + "Banning " +
         * name + " by " + adminName); }
         */
    }

    /**
     * Calling /kick
     * 
     * @param name
     * @param admin
     * @param reason
     */
    public void callKick(String name, String admin, String reason) {
        this.callKick(name, admin, reason, false);
    }

    /**
     * Calling /kick, with option of being quiet about it
     * 
     * @param pname
     * @param admin
     * @param reason
     * @param quiet
     */
    public void callKick(String pname, String admin, String reason, boolean quiet) {
        final List<Player> toKickCandidates = this.j2.getServer().matchPlayer(pname);
        if (toKickCandidates.size() != 1) {
            if (!admin.equalsIgnoreCase("console") && !quiet) {
                this.j2.getServer().getPlayer(admin).sendMessage(ChatColor.RED + "Error:" + pname + " does not exist or fits multiple players");
            }
            return;
        }
        final Player toKick = toKickCandidates.get(0);
        if (toKick != null) {
            toKick.getWorld().strikeLightningEffect(toKick.getLocation());
            final String name = toKick.getName();
            if (reason != "") {
                toKick.kickPlayer("Kicked: " + reason);
                this.j2.sendAdminPlusLog(ChatColor.RED + "Kicking " + name + " by " + admin + ": " + reason);
                this.j2.chat.messageByFlagless(Flag.ADMIN, ChatColor.RED + name + " kicked (" + reason + ")");
                this.j2.irc.messageRelay(name + " kicked (" + reason + ")");
            } else {
                toKick.kickPlayer("Kicked.");
                this.j2.sendAdminPlusLog(ChatColor.RED + "Kicking " + name + " by " + admin);
                this.j2.chat.messageByFlagless(Flag.ADMIN, ChatColor.RED + name + " kicked");
                this.j2.irc.messageRelay(name + " kicked");
            }
        } else {
            if (!admin.equalsIgnoreCase("console") && !quiet) {
                this.j2.getServer().getPlayer(admin).sendMessage(ChatColor.RED + "Error:" + pname + " does not exist or fits multiple players");
            }
        }
    }

    /**
     * Remove all instances of an exact playername Used for /addban
     * 
     * @param name
     * @param reason
     */
    public void forceKick(String name, String reason) {
        boolean msged = false;
        for (final Player p : this.j2.getServer().getOnlinePlayers()) {
            if ((p != null) && p.getName().equalsIgnoreCase(name)) {
                p.getWorld().strikeLightningEffect(p.getLocation());
                p.kickPlayer(reason);
                if (!msged) {
                    if (reason != "") {
                        this.j2.irc.messageRelay(name + " kicked");
                    } else {
                        this.j2.irc.messageRelay(name + " kicked (" + reason + ")");
                    }
                    this.j2.sendAdminPlusLog(ChatColor.RED + "Knocked " + name + " out of the server");
                    msged = !msged;
                }
            }
        }
    }

    /**
     * Kick a player for spamming
     * 
     * @param player
     * @param spammingWhat
     */
    public void spamKick(Player player) {
        final String name = player.getName();
        int count = 0;
        if (this.sessionSpamKicks.containsKey(name)) {
            count = this.sessionSpamKicks.get(name);
        }
        count++;
        this.sessionSpamKicks.put(name, count);
        String reason = null;
        switch (count) {
            case 2:
                reason = "Last warning to stop spamming";
                break;
            case 3:
                this.callAddBan("BobTheSpamMonitor", null, new Location(player.getWorld(), 0, 0, 0));
                this.sessionSpamKicks.remove(name);
                break;
            default:
                reason = "Stop spamming";
        }
        if (count < 3) {
            player.kickPlayer(reason);
            this.j2.sendAdminPlusLog(name + " kicked for spam");
        }
    }

    /**
     * Kick all players
     * 
     * @param reason
     */
    public void kickAll(String reason) {
        this.j2.log(ChatColor.RED + "Kicking all players: " + reason);
        if (reason.equalsIgnoreCase("")) {
            reason = "Count to 30 and try again.";
        }
        for (final Player p : this.j2.getServer().getOnlinePlayers()) {
            if (p != null) {
                p.kickPlayer(reason);
            }
        }
    }

    /**
     * Unban a player
     * 
     * @param adminName
     * @param name
     */
    public void unban(String adminName, String name) {
        this.j2.mysql.unban(name);
        this.j2.sendAdminPlusLog(ChatColor.RED + "Unbanning " + name + " by " + adminName);
        this.j2.banCoop.processUnban(name, adminName);
    }

    /**
     * Player attempting to use an old style xray hack Defunct
     * 
     * @param name
     * @param commandName
     */
    public synchronized void ixrai(String name, String commandName) {
        if (this.xrayers.contains(name)) {
            this.callBan("BobTheVigilant", (name + " xray hacking").split(" "), new Location(this.j2.getServer().getWorld("world"), 0, 0, 0));
            this.j2.log(ChatColor.AQUA + "[BOB] Detected /" + commandName + " from " + name + " and bant");
            this.xrayers.remove(name);
        } else {
            this.xrayers.add(name);
            this.callKick(name, "BobTheVigilant", "Remove your hacks, then rejoin :)");
            this.j2.log(ChatColor.AQUA + "[BOB] Detected /" + commandName + " from " + name + " and kicked");
        }
    }
}
