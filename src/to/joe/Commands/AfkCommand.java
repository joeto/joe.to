package to.joe.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;

public class AfkCommand extends MasterCommand {

    public AfkCommand(J2 j2) {
        super(j2);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
        if (isPlayer) {
            this.j2.kickbans.callKick(player.getName(), "BobTheAFKer", "AFK", true);
            this.j2.minitrue.announceLeave(player.getName(), false);
        }
    }
}
