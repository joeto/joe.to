package to.joe.Commands.Admin;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class GetFlagsCommand extends MasterCommand {

    public GetFlagsCommand(J2 j2) {
        super(j2);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
        if (!isPlayer || this.j2.hasFlag(player, Flag.ADMIN)) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "/getflags playername");
                return;
            }
            final List<Player> match = this.j2.minitrue.matchPlayer(args[0], true);
            if ((match.size() != 1) || (match.get(0) == null)) {
                sender.sendMessage(ChatColor.RED + "Player not found");
                return;
            }
            final Player who = match.get(0);
            String message = "Player " + match.get(0).getName() + ": ";
            for (final Flag f : this.j2.users.getAllFlags(who)) {
                message += f.getDescription() + ", ";
            }
            sender.sendMessage(ChatColor.RED + message);
            this.j2.log(playerName + " looked up " + who.getName());
        }
    }
}
