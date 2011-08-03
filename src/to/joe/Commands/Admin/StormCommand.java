package to.joe.Commands.Admin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class StormCommand extends MasterCommand {

    public StormCommand(J2 j2) {
        super(j2);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
        if (!isPlayer || this.j2.hasFlag(player, Flag.ADMIN)) {
            if (args.length == 0) {
                player.sendMessage(ChatColor.RED + "/storm start/stop");
                return;
            }
            if (args[0].equalsIgnoreCase("start")) {
                player.getWorld().setStorm(true);
                this.j2.sendAdminPlusLog(ChatColor.RED + playerName + " starts up a storm");
                this.j2.chat.messageByFlagless(Flag.ADMIN, ChatColor.RED + "Somebody has started a storm!");
            }
            if (args[0].equalsIgnoreCase("stop")) {
                player.getWorld().setStorm(false);
                this.j2.sendAdminPlusLog(ChatColor.RED + playerName + " stops the storm");
                this.j2.chat.messageByFlagless(Flag.ADMIN, ChatColor.RED + "Somebody has prevented a storm!");
            }
        }
    }
}
