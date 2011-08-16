package to.joe.Commands.Admin;

import java.util.List;

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
                final List<Player> list = this.j2.getServer().matchPlayer(args[0]);
                if (list.size() != 1) {

                } else {
                    final Player target = list.get(0);
                    this.j2.damage.woof(target);
                    this.j2.sendAdminPlusLog(ChatColor.RED + playerName + " has targeted " + target.getName() + " for some fluffy lovin");
                }
            }
        }
    }
}
