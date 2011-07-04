package to.joe.util.BanCooperative;

import java.util.HashMap;

import org.bukkit.ChatColor;

import to.joe.J2;
import to.joe.manager.BanCooperative;

public class BanRunnerBan extends BanRunner{

	private String admin;
	private String reason;
	
	public BanRunnerBan(J2 j2, BanCooperative coop, String name, String admin, String reason) {
		super(j2, coop, name);
		this.admin=admin;
		this.reason=reason;
	}

	@Override
	public void run() {
		this.mcbans_ban();
	}
	
	private void mcbans_ban(){
		String banType="ban_local_user";
		String reason_lower=reason.toLowerCase();
		if(reason_lower.contains("grief")
				&&!(reason_lower.contains("fuck")||reason_lower.contains("shit")||reason_lower.contains("bitch"))){
			banType="ban_user";
		}
		if(admin.toLowerCase().contains("bob")){
			admin="mbaxter";
			banType="ban_local_user";
		}
		HashMap<String, String> postVars = new HashMap<String, String>();
		postVars.put("player", name);
		postVars.put("admin", admin);
		postVars.put("reason", reason);
		String ip = j2.mysql.IPGetLast(name);
		postVars.put("playerip", ip);
		postVars.put("duration", "0");
		postVars.put("exec", banType);
		HashMap<String, String> result = JSONToHashMap(this.mcbans_api(postVars));
		if (result.get("result").equalsIgnoreCase("y")) {
			j2.log(ChatColor.RED+"[mcbans] Added "+name);
		}
		else if (result.get("result").equalsIgnoreCase("a")) {
			j2.log(ChatColor.RED+"[mcbans] Player "+name+" already on list");
		} else if (result.get("result").equalsIgnoreCase("n")) {
			j2.log(ChatColor.RED+"[mcbans] Could not add "+name);
		}
	}
	
	@SuppressWarnings("unused")
	private void mcbouncer_ban(){
		
	}

}
