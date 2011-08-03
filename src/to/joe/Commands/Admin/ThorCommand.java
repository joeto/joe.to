package to.joe.Commands.Admin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class ThorCommand extends MasterCommand {

    public ThorCommand(J2 j2) {
        super(j2);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
        if (isPlayer && this.j2.hasFlag(player, Flag.ADMIN)) {
            if (this.j2.hasFlag(player, Flag.THOR)) {
                player.sendMessage(ChatColor.GOLD + "You lose your mystical powers");
                this.j2.users.dropFlagLocal(playerName, Flag.THOR);
            } else {
                player.sendMessage(ChatColor.GOLD + "You gain mystical powers");
                this.j2.users.addFlagLocal(playerName, Flag.THOR);
            }
        }
    }
}
