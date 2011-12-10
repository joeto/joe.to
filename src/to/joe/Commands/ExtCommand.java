package to.joe.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;

public class ExtCommand extends MasterCommand {

    public ExtCommand(J2 j2) {
        super(j2);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
        if (isPlayer && j2.config.general_server_number==2) {
            player.setFireTicks(600);
        }
    }
}
