package to.joe.Commands.Admin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class LookupCommand extends MasterCommand {

    public LookupCommand(J2 j2) {
        super(j2);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
        if (isPlayer && this.j2.hasFlag(player, Flag.ADMIN)) {
            if (args.length == 0) {
                player.sendMessage(ChatColor.LIGHT_PURPLE + "/lookup player");
                return;
            }
            final String name = args[0];
            this.j2.log(ChatColor.LIGHT_PURPLE + playerName + " looked up " + name);
            this.j2.banCoop.lookup(name, player);
        }
    }
}
