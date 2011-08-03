package to.joe.manager;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.util.Flag;
import to.joe.util.Warp;

/**
 * Warp manager (homes are private warps)
 * 
 */
public class Warps {
    public Warps(J2 J2) {
        this.restartManager();
        this.j2 = J2;
    }

    /**
     * Wipe warps
     */
    public void restartManager() {
        this.warps = new ArrayList<Warp>();
        this.homes = new ArrayList<Warp>();
    }

    /**
     * Add a warp to the system
     * 
     * @param warp
     */
    public void addWarp(Warp warp) {
        this.j2.mysql.addWarp(warp);
        this.addWarpInternal(warp);
    }

    /**
     * Add warp to internal system only.
     * 
     * @param warp
     */
    public void addWarpInternal(Warp warp) {
        synchronized (this.lock) {
            if (warp.getFlag().equals(Flag.PLAYER_HOME)) {
                this.homes.add(warp);
            } else {
                this.warps.add(warp);
            }
        }
    }

    /**
     * Remove warp
     * 
     * @param warp
     */
    public void killWarp(Warp warp) {
        synchronized (this.lock) {
            if (warp.getFlag().equals(Flag.PLAYER_HOME)) {
                this.homes.remove(warp);
            } else {
                this.warps.remove(warp);
            }
            this.j2.mysql.removeWarp(warp);
        }
    }

    /**
     * Player joins
     * 
     * @param name
     */
    public void processJoin(String name) {
        this.loadPlayer(name);
    }

    /**
     * Load warps for player
     * 
     * @param playername
     */
    public void loadPlayer(String playername) {
        final ArrayList<Warp> playerhomes = this.j2.mysql.getHomes(playername);
        synchronized (this.lock) {
            if (playerhomes != null) {
                this.homes.addAll(playerhomes);
            }
        }
    }

    /**
     * Remove warps for player.
     * 
     * @param playername
     */
    public void dropPlayer(String playername) {
        synchronized (this.lock) {
            final ArrayList<Warp> toRemove = new ArrayList<Warp>();
            for (final Warp home : this.homes) {
                if (home.getPlayer().equalsIgnoreCase(playername)) {
                    toRemove.add(home);
                }
            }
            this.homes.removeAll(toRemove);
        }
    }

    /**
     * Get named warp for user.
     * 
     * @param playername
     * @param warpname
     * @return
     */
    public Warp getUserWarp(String playername, String warpname) {
        synchronized (this.lock) {
            for (final Warp warp : this.homes) {

                if (warp.getName().equalsIgnoreCase(warpname) && warp.getPlayer().equalsIgnoreCase(playername)) {
                    return warp;
                }
            }
            return null;
        }
    }

    /**
     * Get named public warp
     * 
     * @param warpname
     * @return
     */
    public Warp getPublicWarp(String warpname) {
        synchronized (this.lock) {
            for (final Warp warp : this.warps) {
                if (warp.getName().equalsIgnoreCase(warpname)) {
                    return warp;
                }
            }
            return null;
        }
    }

    /**
     * Get list of user's homes
     * 
     * @param playername
     * @return
     */
    public ArrayList<Warp> getUserWarps(String playername) {
        synchronized (this.lock) {
            final ArrayList<Warp> toReturn = new ArrayList<Warp>();

            for (final Warp home : this.homes) {
                if (home.getPlayer().equalsIgnoreCase(playername)) {
                    toReturn.add(home);
                }
            }
            return toReturn;
        }
    }

    /**
     * Get list of public warps
     * 
     * @return
     */
    public ArrayList<Warp> getPublicWarps() {
        synchronized (this.lock) {
            return new ArrayList<Warp>(this.warps);
        }
    }

    /**
     * Get list of player's homes in String format
     * 
     * @param playername
     * @return
     */
    public String listHomes(String playername) {
        synchronized (this.lock) {
            final ArrayList<Warp> homes_u = this.getUserWarps(playername);
            String homes_s = "";
            if (homes_u != null) {

                for (final Warp home : homes_u) {
                    if (home != null) {
                        homes_s += ", " + home.getName();
                    }
                }
                if (!homes_s.equalsIgnoreCase("")) {
                    homes_s = homes_s.substring(2);// remove the first
                                                   // comma/space
                }
            }
            return homes_s;
        }
    }

    /**
     * List warps a player can visit to the player
     * 
     * @param player
     * @return
     */
    public String listWarps(Player player) {
        synchronized (this.lock) {
            final ArrayList<Warp> warps_u = this.getPublicWarps();

            String warps_s = "";
            if (warps_u != null) {
                this.j2.debug("Found " + warps_u.size() + " warps");
                for (final Warp warp_i : warps_u) {
                    final Flag flag = warp_i.getFlag();
                    this.j2.debug(warp_i.getName() + " has flag " + warp_i.getFlag().getChar());
                    if ((warp_i != null) && (this.j2.hasFlag(player, flag) || flag.equals(Flag.PLAYER_WARP_PUBLIC))) {
                        warps_s += ", " + warp_i.getName();
                    }
                }
                if (!warps_s.equalsIgnoreCase("")) {
                    warps_s = warps_s.substring(2);// remove the first
                                                   // comma/space
                }
            }
            return warps_s;
        }
    }

    /**
     * Get closest warp to a player's location
     * 
     * @param location
     * @return
     */
    public Warp getClosestWarp(Location location) {
        final ArrayList<Warp> allWarps = this.getPublicWarps();
        final double xstart = location.getX();
        final double ystart = location.getY();
        final double zstart = location.getZ();
        int distance = 0;
        Warp solution = null;
        for (final Warp warp : allWarps) {
            if (warp != null) {
                if (solution != null) {
                    final Location locwarp = warp.getLocation();
                    final double xwarp = locwarp.getX();
                    final double ywarp = locwarp.getY();
                    final double zwarp = locwarp.getZ();
                    final int dist = (int) Math.pow((Math.pow(xwarp - xstart, 2) + Math.pow(ywarp - ystart, 2) + Math.pow(zwarp - zstart, 2)), 0.5);
                    if (dist < distance) {
                        distance = dist;
                        solution = warp;
                    }
                } else {
                    solution = warp;
                    final Location locwarp = warp.getLocation();
                    final double xwarp = locwarp.getX();
                    final double ywarp = locwarp.getY();
                    final double zwarp = locwarp.getZ();
                    distance = (int) Math.pow((Math.pow(xwarp - xstart, 2) + Math.pow(ywarp - ystart, 2) + Math.pow(zwarp - zstart, 2)), 0.5);
                }
            }
        }
        return solution;
    }

    private ArrayList<Warp> warps;
    private ArrayList<Warp> homes;
    private final J2 j2;
    private final Object lock = new Object();
}
