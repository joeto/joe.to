package to.joe.util.Runnables.BanCooperative;

import java.util.HashMap;

import org.bukkit.ChatColor;

import to.joe.J2;
import to.joe.manager.BanCooperative;

public class CoopRunnerUnban extends CoopRunner {

    private String admin;

    public CoopRunnerUnban(J2 j2, BanCooperative coop, String name, String admin) {
        super(j2, coop, name);
        this.admin = admin;
    }

    @Override
    public void run() {
        this.mcbans_unban();
    }

    private void mcbans_unban() {
        HashMap<String, String> postVars = new HashMap<String, String>();
        postVars.put("player", name);
        postVars.put("exec", "unBan");
        postVars.put("admin", admin);
        HashMap<String, String> result = JSONToHashMap(this.mcbans_api(postVars));
        if ((result.get("result")).equalsIgnoreCase("y")) {
            j2.log(ChatColor.RED + "[mcbans] Unbanned " + name);
        } else {
            j2.log(ChatColor.RED + "[mcbans] Failed to unban " + name);
        }
    }

}
