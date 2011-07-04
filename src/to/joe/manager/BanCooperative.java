package to.joe.manager;

import java.util.HashMap;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.util.BanCooperative.BanCoopDossier;
import to.joe.util.BanCooperative.BanRunnerBan;
import to.joe.util.BanCooperative.BanRunnerJoin;
import to.joe.util.BanCooperative.BanRunnerLookup;
import to.joe.util.BanCooperative.BanRunnerUnban;

public class BanCooperative {
	private J2 j2;
	
	public HashMap<String,BanCoopDossier> record;
	public BanCooperative(J2 j2){
		this.j2=j2;
		this.record=new HashMap<String,BanCoopDossier>();
	}

	public void processJoin(Player player){
		BanRunnerJoin br=new BanRunnerJoin(j2, this, player.getName(), player);
		new Thread(br).start();
	}
	
	public void lookup(String name,Player admin){
		BanRunnerLookup br=new BanRunnerLookup(j2, this, name, admin);
		new Thread(br).start();
	}
	
	public void processBan(String name, String admin, String reason){
		BanRunnerBan br=new BanRunnerBan(j2, this, name, admin, reason);
		new Thread(br).start();
	}
	
	public void processUnban(String name){
		BanRunnerUnban br=new BanRunnerUnban(j2, this, name);
		new Thread(br).start();
	}
	
}
