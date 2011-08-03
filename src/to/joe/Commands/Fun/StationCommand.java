package to.joe.Commands.Fun;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;
import to.joe.util.Warp;

public class StationCommand extends MasterCommand {

    public StationCommand(J2 j2) {
        super(j2);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
        if (isPlayer && this.j2.hasFlag(player, Flag.FUN) && (this.j2.servernumber == 2)) {
            final Warp target = this.j2.warps.getClosestWarp(player.getLocation());
            final String name = target.getName();
            if ((args.length == 1) && args[0].equalsIgnoreCase("go")) {
                this.j2.safePort(player, target.getLocation());
                player.sendMessage(ChatColor.AQUA + "You are now at " + ChatColor.DARK_AQUA + "Station " + name);
                player.sendMessage(ChatColor.AQUA + "You can return here by saying " + ChatColor.DARK_AQUA + "/warp " + name);
            } else {
                player.sendMessage(ChatColor.AQUA + "You are closest to " + ChatColor.DARK_AQUA + "Station " + name);
                player.sendMessage(ChatColor.AQUA + "You can always get there with " + ChatColor.DARK_AQUA + "/warp " + name);
                player.sendMessage(ChatColor.AQUA + "To travel to the closest station say " + ChatColor.DARK_AQUA + "/station go");
            }
        }
    }
}
