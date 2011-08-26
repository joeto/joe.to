package to.joe.util.Runnables.BanCooperative;

import java.util.HashMap;

import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.manager.BanCooperative;

public class CoopRunnerMCBansHeartbeat extends CoopRunner {

    public final String mcbans_version = "3.0.1.3.3.7";

    public CoopRunnerMCBansHeartbeat(J2 j2, BanCooperative coop) {
        super(j2, coop, "");
    }

    @Override
    public void run() {
        final HashMap<String, String> postVars = new HashMap<String, String>();
        postVars.put("version", this.mcbans_version);
        postVars.put("maxPlayers", String.valueOf(this.j2.config.access_max_players));
        postVars.put("exec", "callBack");
        postVars.put("playerList", this.playerList());
        this.mcbans_api(postVars);
    }

    private String playerList() {
        final StringBuilder playerList = new StringBuilder();
        final Player[] players = this.j2.getServer().getOnlinePlayers();
        if (players == null) {
            return "";
        }
        for (final Player player : players) {
            if (!playerList.equals("")) {
                playerList.append(",");
            }
            playerList.append(player.getName());
        }
        return playerList.toString();
    }

}
