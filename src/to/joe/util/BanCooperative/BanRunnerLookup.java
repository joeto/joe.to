package to.joe.util.BanCooperative;

import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.manager.BanCooperative;

public class BanRunnerLookup extends BanRunner {

	private Player player;
	
	public BanRunnerLookup(J2 j2, BanCooperative coop, String name, Player player) {
		super(j2, coop, name);
		this.player=player;
	}
	
	@Override
	public void run() {
		if(!this.coop.record.containsKey(name)){
			this.dox(name);
		}
		BanCoopDossier dossier= this.coop.record.get(name);
		for(String line:dossier.full()){
			player.sendMessage(line);
		}
	}
	
}

