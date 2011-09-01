package to.joe.util.Runnables.BanCooperative;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.manager.BanCooperative;
import to.joe.util.Flag;
import to.joe.util.BanCooperative.BanCoopDossier;

public class CoopRunnerJoin extends CoopRunner {

    private final Player player;

    public CoopRunnerJoin(J2 j2, BanCooperative coop, String name, Player player) {
        super(j2, coop, name);
        this.player = player;
    }

    @Override
    public void run() {
        final String ip = this.player.getAddress().getAddress().getHostAddress();
        this.mcbans_user_connect(this.name, ip);
        this.mcbouncer_api("updateUser", this.name + "/" + ip);
        this.dox();
        final BanCoopDossier dox = this.coop.record.get(this.name);
        if (dox.totalBans() > 0) {
            this.j2.chat.messageByFlag(Flag.ADMIN, ChatColor.LIGHT_PURPLE + "Player " + ChatColor.WHITE + this.name + ChatColor.LIGHT_PURPLE + " has " + ChatColor.WHITE + dox.totalBans() + ChatColor.LIGHT_PURPLE + " bans. MCBans rep " + ChatColor.WHITE + dox.getMCBansRep() + ChatColor.LIGHT_PURPLE + "/10");
            this.j2.chat.messageByFlag(Flag.ADMIN, ChatColor.LIGHT_PURPLE + "To see the bans: /lookup " + ChatColor.WHITE + this.name);
            if (!this.j2.hasFlag(this.name, Flag.QUIET_IRC) && (dox.sigBans() > 0)) {
                this.j2.irc.messageAdmins("[BANS] " + this.name + ": Bans: " + dox.totalBans() + ". MCBans Rep " + dox.getMCBansRep() + "/10");
            }
        }
        this.j2.log(ChatColor.LIGHT_PURPLE + "[BANS] " + this.name + ": " + dox.totalBans() + " bans, mcbans rep " + dox.getMCBansRep() + "/10");
    }
}