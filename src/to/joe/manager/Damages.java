package to.joe.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.PlayerInventory;

import to.joe.J2;

/**
 * Damage interpreter Also handles the fun wolf attack ability
 * 
 */
public class Damages {
    public ArrayList<String> PvPsafe, PvEsafe;
    public HashMap<String, ArrayList<Wolf>> allWolf;
    J2 j2;

    public Damages(J2 j2) {
        this.j2 = j2;
        this.startDamageTimer();
        this.restartManager();
    }

    /**
     * Clear system
     */
    public void restartManager() {
        this.clear();
        this.allWolf = new HashMap<String, ArrayList<Wolf>>();
        this.timer1 = new ArrayList<String>();
        this.timer2 = new ArrayList<String>();
    }

    /**
     * Protect named player if safe mod is on
     * 
     * @param name
     */
    public void processJoin(String name) {
        if (this.j2.config.world_safemode) {
            this.protect(name);
        }
    }

    /**
     * Protect named player from PvP and PvE
     * 
     * @param name
     */
    public void protect(String name) {
        this.protectPvP(name);
        this.protectPvE(name);
    }

    /**
     * Protect named player from PvP
     * 
     * @param name
     */
    public void protectPvP(String name) {
        if (!this.PvPsafe.contains(name)) {
            this.PvPsafe.add(name);
        }
    }

    /**
     * Protect named player from PvE
     * 
     * @param name
     */
    public void protectPvE(String name) {
        if (!this.PvEsafe.contains(name)) {
            this.PvEsafe.add(name);
        }
    }

    /**
     * Remove named player's protection from PvE and PvP
     * 
     * @param name
     */
    public void endanger(String name) {
        this.endangerPvP(name);
        this.endangerPvE(name);
    }

    /**
     * Remove named player's protection from PvP
     * 
     * @param name
     */
    public void endangerPvP(String name) {
        this.PvPsafe.remove(name);
    }

    /**
     * Remove named player's protection from PvE
     * 
     * @param name
     */
    public void endangerPvE(String name) {
        this.PvEsafe.remove(name);
    }

    /**
     * Wipes the PvP and PvE protection lists
     */
    public void clear() {
        this.PvPsafe = new ArrayList<String>();
        this.PvEsafe = new ArrayList<String>();
    }

    /**
     * Attack target player with wolves Strips player inventory for easy killing
     * 
     * @param target
     * @return
     */
    public boolean woof(Player player) {
        this.endanger(player.getName());
        final ArrayList<Wolf> wlist = new ArrayList<Wolf>();
        final boolean hated = this.j2.config.world_disable_wolves;
        final PlayerInventory targetInventory = player.getInventory();
        targetInventory.clear(36);
        targetInventory.clear(37);
        targetInventory.clear(38);
        targetInventory.clear(39);
        targetInventory.clear();
        this.j2.config.world_disable_wolves = false;
        for (int x = 0; x < 10; x++) {
            final Wolf wolf = (Wolf) player.getWorld().spawnCreature(player.getLocation(), CreatureType.WOLF);
            wlist.add(wolf);
            wolf.setAngry(true);
            wolf.setTarget(player);
        }
        this.j2.config.world_disable_wolves = hated;
        this.allWolf.put(player.getName(), wlist);
        return true;
    }

    /**
     * Remove any wolves attacking target player
     * 
     * @param target
     */
    public void arf(String target) {
        if (this.allWolf.containsKey(target)) {
            for (final Wolf wolf : this.allWolf.get(target)) {
                wolf.damage(100);
            }
            this.allWolf.remove(target);
            if (this.j2.config.world_safemode) {
                this.protect(target);
            }
        }
    }

    private boolean stop;
    private ArrayList<String> timer1;
    private ArrayList<String> timer2;

    /**
     * Add named player to short-span timer timer will auto-reset player's
     * damage protection Designed for smiting players in damage-less servers
     * 
     * @param name
     */
    public void addToTimer(String name) {
        synchronized (this.sync) {
            this.timer1.add(name);
        }
    }

    private void startDamageTimer() {
        this.stop = false;
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (Damages.this.stop) {
                    timer.cancel();
                    return;
                }
                Damages.this.update();
            }
        }, 1000, 1000);
    }

    private void update() {
        synchronized (this.sync) {
            for (final String n : this.timer2) {
                this.processJoin(n);
            }
            this.timer2 = new ArrayList<String>(this.timer1);
            this.timer1 = new ArrayList<String>();
        }
    }

    private final Object sync = new Object();
}
