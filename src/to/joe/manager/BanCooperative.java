package to.joe.manager;

import java.util.HashMap;

import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.util.BanCooperative.BanCoopDossier;
import to.joe.util.Runnables.BanCooperative.*;

/**
 * System for interfacing with MCBans and MCBouncer Easy to incorporate
 * additional systems.
 * 
 */
public class BanCooperative {
    private final J2 j2;

    /**
     * Mapped dossiers by name
     */
    public HashMap<String, BanCoopDossier> record;

    public BanCooperative(J2 j2) {
        this.j2 = j2;
        this.record = new HashMap<String, BanCoopDossier>();
    }

    /**
     * Starts the MCBans callBack.
     */
    public void startCallback() {
        this.j2.getServer().getScheduler().scheduleAsyncRepeatingTask(this.j2, new CoopRunnerMCBansHeartbeat(this.j2, this), 60000, 60000);
    }

    /**
     * Sends join info to coop systems
     * 
     * @param player
     */
    public void processJoin(Player player) {
        final CoopRunnerJoin runner = new CoopRunnerJoin(this.j2, this, player.getName(), player);
        new Thread(runner).start();
    }

    /**
     * Sends disconnect info to coop systems
     * 
     * @param name
     */
    public void disconnect(String name) {
        final CoopRunnerDisconnect runner = new CoopRunnerDisconnect(this.j2, this, name);
        new Thread(runner).start();
    }

    /**
     * Sends a lookup query to systems, replies to the admin querying
     * 
     * @param name
     * @param admin
     */
    public void lookup(String name, Player admin) {
        final CoopRunnerLookup runner = new CoopRunnerLookup(this.j2, this, name, admin);
        new Thread(runner).start();
    }

    /**
     * Sends ban information to coop systems
     * 
     * @param name
     * @param admin
     * @param reason
     */
    public void processBan(String name, String admin, String reason) {
        final CoopRunnerBan runner = new CoopRunnerBan(this.j2, this, name, admin, reason);
        new Thread(runner).start();
    }

    /**
     * Sends unban query to coop systems
     * 
     * @param name
     * @param admin
     */
    public void processUnban(String name, String admin) {
        final CoopRunnerUnban runner = new CoopRunnerUnban(this.j2, this, name, admin);
        new Thread(runner).start();
    }

}
