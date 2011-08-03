package to.joe.util;

import org.bukkit.Location;

/**
 * Warps and homes
 * 
 */
public class Warp {
    public Warp(String Name, String Player, Location Location, Flag Flag) {
        this.location = Location;
        this.name = Name;
        this.player = Player;
        this.flag = Flag;
    }

    /**
     * @return the warp's name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return the owner of the warp
     */
    public String getPlayer() {
        return this.player;
    }

    /**
     * @return the warp location
     */
    public Location getLocation() {
        return this.location;
    }

    /**
     * @return flag of the warp
     */
    public Flag getFlag() {
        return this.flag;
    }

    private Location location;
    private String name, player;
    private Flag flag;
}
