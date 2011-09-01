package to.joe.util.Runnables.BanCooperative;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.json.JSONObject;

import to.joe.J2;
import to.joe.manager.BanCooperative;

public class CoopRunnerUnban extends CoopRunner {

    private final String admin;

    public CoopRunnerUnban(J2 j2, BanCooperative coop, String name, String admin) {
        super(j2, coop, name);
        this.admin = admin;
    }

    @Override
    public void run() {
        this.mcbans_unban();
        this.mcbouncer_unban();
    }

    private void mcbans_unban() {
        final HashMap<String, String> postVars = new HashMap<String, String>();
        postVars.put("player", this.name);
        postVars.put("exec", "unBan");
        postVars.put("admin", this.admin);
        final HashMap<String, String> result = this.JSONToHashMap(this.mcbans_api(postVars));
        if ((result.get("result")).equalsIgnoreCase("y")) {
            this.j2.log(ChatColor.RED + "[mcbans] Unbanned " + this.name);
        } else {
            this.j2.log(ChatColor.RED + "[mcbans] Failed to unban " + this.name);
        }
    }

    private void mcbouncer_unban(){
        final JSONObject result=this.mcbouncer_api("removeBan", this.name);
        if(result.optBoolean("success")){
            this.j2.log(ChatColor.RED+"[mcbouncer] Unbanned: "+this.name);
        }
        else{
            this.j2.log(ChatColor.RED+"[mcbouncer] Failed to unban "+this.name);
        }
    }

}
