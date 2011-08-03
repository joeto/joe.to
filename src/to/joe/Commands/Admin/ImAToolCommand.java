package to.joe.Commands.Admin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class ImAToolCommand extends MasterCommand {

    public ImAToolCommand(J2 j2) {
        super(j2);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
        if (isPlayer && this.j2.hasFlag(player, Flag.ADMIN)) {
            if (this.j2.hasFlag(player, Flag.TOOLS)) {
                player.sendMessage(ChatColor.AQUA + "Tool mode disabled.");
                this.j2.users.dropFlagLocal(playerName, Flag.TOOLS);
            } else {
                player.sendMessage(ChatColor.AQUA + "Tool mode enabled. Be careful.");
                this.j2.users.addFlagLocal(playerName, Flag.TOOLS);
            }
        }
    }
}
