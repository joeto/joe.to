package to.joe.util.BanCooperative;

import org.bukkit.ChatColor;

public class BanCoopBanMCBouncer extends BanCoopBan {

    public BanCoopBanMCBouncer(String server, String reason) {
        super(server, reason);
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return ChatColor.AQUA + "[MCBouncer] " + ChatColor.GREEN + "<" + this.getServer() + "> " + ChatColor.AQUA + this.getReason();
    }

}
