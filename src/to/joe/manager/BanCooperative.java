package to.joe.manager;

import java.util.HashMap;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.util.BanCooperative.BanCoopDossier;
import to.joe.util.BanCooperative.Runners.*;

public class BanCooperative {
	private J2 j2;
	
	public HashMap<String,BanCoopDossier> record;
	
	public BanCooperative(J2 j2){
		this.j2=j2;
		this.record=new HashMap<String,BanCoopDossier>();
	}
	
	public void startThumper(){
		CoopRunnerMCBansHeartbeat thumpThump=new CoopRunnerMCBansHeartbeat(j2,this);
		new Thread(thumpThump).start();
	}

	public void processJoin(Player player){
		CoopRunnerJoin runner=new CoopRunnerJoin(j2, this, player.getName(), player);
		new Thread(runner).start();
	}
	
	public void disconnect(String name){
		CoopRunnerDisconnect runner= new CoopRunnerDisconnect(j2, this, name);
		new Thread(runner).start();
	}
	
	public void lookup(String name,Player admin){
		CoopRunnerLookup runner=new CoopRunnerLookup(j2, this, name, admin);
		new Thread(runner).start();
	}
	
	public void processBan(String name, String admin, String reason){
		CoopRunnerBan runner=new CoopRunnerBan(j2, this, name, admin, reason);
		new Thread(runner).start();
	}
	
	public void processUnban(String name, String admin){
		CoopRunnerUnban runner=new CoopRunnerUnban(j2, this, name, admin);
		new Thread(runner).start();
	}
	
}
