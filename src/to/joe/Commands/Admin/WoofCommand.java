package to.joe.Commands.Admin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class WoofCommand extends MasterCommand {

    public WoofCommand(J2 j2) {
        super(j2);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
        if (!isPlayer || this.j2.hasFlag(player, Flag.ADMIN)) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "/woof player");
            } else {
                this.j2.damage.woof(args[0]);
                this.j2.sendAdminPlusLog(ChatColor.RED + playerName + " has targeted " + args[0] + " for some fluffy lovin");
            }
        }
    }
}
