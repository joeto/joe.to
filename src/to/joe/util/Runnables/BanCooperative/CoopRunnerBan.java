package to.joe.util.Runnables.BanCooperative;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.json.JSONObject;

import to.joe.J2;
import to.joe.manager.BanCooperative;

public class CoopRunnerBan extends CoopRunner {

    private String admin;
    private final String reason;

    private final String local = "localBan";
    private final String global = "globalBan";

    public CoopRunnerBan(J2 j2, BanCooperative coop, String name, String admin, String reason) {
        super(j2, coop, name);
        this.admin = admin;
        this.reason = reason;
    }

    @Override
    public void run() {
        //this.mcbans_ban();
        this.mcbouncer_ban();
    }
    
    private void mcbans_ban() {
        String banType = this.local;
        final String reason_lower = this.reason.toLowerCase();
        /*if (reason_lower.contains("grief") && !(reason_lower.contains("fuck") || reason_lower.contains("shit") || reason_lower.contains("bitch"))) {
            banType = this.global;
        }
        if (this.admin.toLowerCase().contains("bob")) {
            this.admin = "mbaxter";
            banType = this.local;
        }*/
        final HashMap<String, String> postVars = new HashMap<String, String>();
        postVars.put("player", this.name);
        postVars.put("admin", this.admin);
        postVars.put("reason", this.reason);
        final String ip = this.j2.mysql.IPGetLast(this.name);
        postVars.put("playerip", ip);
        postVars.put("exec", banType);
        final HashMap<String, String> result = this.JSONToHashMap(this.mcbans_api(postVars));
        if (result.get("result").equalsIgnoreCase("y")) {
            this.j2.log(ChatColor.RED + "[mcbans] Added " + this.name);
        } else if (result.get("result").equalsIgnoreCase("a")) {
            this.j2.log(ChatColor.RED + "[mcbans] Player " + this.name + " already on list");
        } else if (result.get("result").equalsIgnoreCase("n")) {
            this.j2.log(ChatColor.RED + "[mcbans] Could not add " + this.name);
        }
    }

    private void mcbouncer_ban() {
        final JSONObject result=this.mcbouncer_api("addBan", this.admin+"/"+this.name+"/"+this.reason);
        if(result!=null && result.optBoolean("success")){
            this.j2.log(ChatColor.RED+"[mcbouncer] Added ban: "+this.name);
        }
        else{
            this.j2.log(ChatColor.RED+"[mcbouncer] Failed to ban "+this.name);
        }
    }

}
