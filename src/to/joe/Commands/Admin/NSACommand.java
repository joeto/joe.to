package to.joe.Commands.Admin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class NSACommand extends MasterCommand {

    public NSACommand(J2 j2) {
        super(j2);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
        if (isPlayer && this.j2.hasFlag(player, Flag.ADMIN)) {
            String message;
            if (this.j2.hasFlag(player, Flag.NSA)) {
                message = ChatColor.DARK_AQUA + playerName + ChatColor.AQUA + " takes off headphones. That's enough chatter";
                this.j2.users.dropFlagLocal(playerName, Flag.NSA);
            } else {
                message = ChatColor.DARK_AQUA + playerName + ChatColor.AQUA + " puts on headphones. Intercepting...";
                this.j2.users.addFlagLocal(playerName, Flag.NSA);
            }
            this.j2.sendAdminPlusLog(message);
        }
    }
}
