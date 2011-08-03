package to.joe.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.util.Flag;

public class VoteCommand extends MasterCommand {

    public VoteCommand(J2 j2) {
        super(j2);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
        if (!isPlayer || this.j2.hasFlag(player, Flag.FUN)) {
            this.j2.voting.voteCommand(player, args);
        }
    }

}
