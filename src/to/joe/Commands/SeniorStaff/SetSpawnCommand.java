package to.joe.Commands.SeniorStaff;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class SetSpawnCommand extends MasterCommand {

    public SetSpawnCommand(J2 j2) {
        super(j2);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
        if (isPlayer && this.j2.hasFlag(player, Flag.SRSTAFF)) {
            if (args.length < 3) {
                player.sendMessage(ChatColor.RED + "/setspawn x y z");
                return;
            }
            player.getWorld().setSpawnLocation(Integer.valueOf(args[0]), Integer.valueOf(args[1]), Integer.valueOf(args[2]));
            this.j2.sendAdminPlusLog(ChatColor.RED + "Spawn set to " + args[0] + " " + args[1] + " " + args[2] + " by " + playerName);
        }
    }
}
