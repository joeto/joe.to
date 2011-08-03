package to.joe.Commands.Info;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;

public class BlacklistCommand extends MasterCommand {

    public BlacklistCommand(J2 j2) {
        super(j2);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
        if (isPlayer) {
            for (final String line : this.j2.blacklist) {
                player.sendMessage(line);
            }
        }
    }

}
