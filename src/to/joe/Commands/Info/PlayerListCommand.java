package to.joe.Commands.Info;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;

public class PlayerListCommand extends MasterCommand {

    public PlayerListCommand(J2 j2) {
        super(j2);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
        this.j2.minitrue.who(sender);
    }

}
