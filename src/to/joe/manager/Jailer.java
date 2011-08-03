package to.joe.manager;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.util.Flag;

/**
 * Unimplemented Jailing system
 * 
 */
public class Jailer {
    private final J2 j2;
    private Location jailLocation;

    public Jailer(J2 j2) {
        this.j2 = j2;
    }

    /**
     * Set jail location from config
     * 
     * @param jail
     */
    public void jailSet(String[] jail) {
        this.jailLocation = new Location(this.j2.getServer().getWorld("world"), Double.valueOf(jail[0]).doubleValue(), Double.valueOf(jail[1]).doubleValue(), Double.valueOf(jail[2]).doubleValue(), Float.valueOf(jail[3]).floatValue(), Float.valueOf(jail[4]).floatValue());
    }

    /**
     * See if should send to jail
     * 
     * @param player
     */
    public void processJoin(Player player) {
        if (!this.isJailed(player)) {
            return;
        }
        this.j2.safePort(player, this.jailLocation);
    }

    /**
     * Message the user that they're in jail
     * 
     * @param player
     */
    public void jailMsg(Player player) {
        player.sendMessage(ChatColor.RED + "You are in JAIL.");
        // more message here
    }

    /**
     * Query if the player is on jail list
     * 
     * @param player
     * @return
     */
    public boolean isJailed(Player player) {
        return this.isJailed(player.getName());
    }

    /**
     * Query if the named player is on the jail list
     * 
     * @param player
     * @return
     */
    public boolean isJailed(String player) {
        return this.j2.hasFlag(player, Flag.JAILED);
    }

    /**
     * See if the player is allowed to do anything
     * 
     * @param player
     * @return true if blocked by jail system
     */
    public boolean processAction(Player player) {
        if (!this.isJailed(player)) {
            return false;
        }
        this.jailMsg(player);
        return true;
    }
}
