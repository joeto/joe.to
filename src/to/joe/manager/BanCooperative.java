package to.joe.manager;

import java.util.HashMap;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.util.BanCooperative.BanCoopDossier;
import to.joe.util.BanCooperative.Runners.*;

/**
 * System for interfacing with MCBans and MCBouncer
 * Easy to incorporate additional systems.
 * @author matt
 *
 */
public class BanCooperative {
	private J2 j2;
	
	/**
	 * Mapped dossiers by name
	 */
	public HashMap<String,BanCoopDossier> record;
	
	public BanCooperative(J2 j2){
		this.j2=j2;
		this.record=new HashMap<String,BanCoopDossier>();
	}
	
	/**
	 * Starts the MCBans callBack. Currently disabled.
	 */
	public void startThumper(){
		CoopRunnerMCBansHeartbeat thumpThump=new CoopRunnerMCBansHeartbeat(j2,this);
		new Thread(thumpThump).start();
	}

	/**
	 * Sends join info to coop systems
	 * @param player
	 */
	public void processJoin(Player player){
		CoopRunnerJoin runner=new CoopRunnerJoin(j2, this, player.getName(), player);
		new Thread(runner).start();
	}
	
	/**
	 * Sends disconnect info to coop systems
	 * @param name
	 */
	public void disconnect(String name){
		CoopRunnerDisconnect runner= new CoopRunnerDisconnect(j2, this, name);
		new Thread(runner).start();
	}
	
	/**
	 * Sends a lookup query to systems, replies to the admin querying
	 * @param name
	 * @param admin
	 */
	public void lookup(String name,Player admin){
		CoopRunnerLookup runner=new CoopRunnerLookup(j2, this, name, admin);
		new Thread(runner).start();
	}
	
	/**
	 * Sends ban information to coop systems
	 * @param name
	 * @param admin
	 * @param reason
	 */
	public void processBan(String name, String admin, String reason){
		CoopRunnerBan runner=new CoopRunnerBan(j2, this, name, admin, reason);
		new Thread(runner).start();
	}
	
	/**
	 * Sends unban query to coop systems
	 * @param name
	 * @param admin
	 */
	public void processUnban(String name, String admin){
		CoopRunnerUnban runner=new CoopRunnerUnban(j2, this, name, admin);
		new Thread(runner).start();
	}
	
}