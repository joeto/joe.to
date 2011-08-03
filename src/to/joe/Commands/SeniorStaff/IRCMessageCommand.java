package to.joe.Commands.SeniorStaff;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class IRCMessageCommand extends MasterCommand {

    public IRCMessageCommand(J2 j2) {
        super(j2);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
        if (!isPlayer || this.j2.hasFlag(player, Flag.SRSTAFF)) {
            if (args.length < 2) {
                return;
            }
            this.j2.irc.getBot().sendMessage(args[0], this.j2.combineSplit(1, args, " "));
        }
    }
}
