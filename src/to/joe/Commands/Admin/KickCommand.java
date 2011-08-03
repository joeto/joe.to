package to.joe.Commands.Admin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class KickCommand extends MasterCommand {

    public KickCommand(J2 j2) {
        super(j2);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
        if (!isPlayer || this.j2.hasFlag(player, Flag.ADMIN)) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /kick playername reason");
                return;
            }
            this.j2.kickbans.callKick(args[0], playerName, this.j2.combineSplit(1, args, " "));
        }
    }
}
