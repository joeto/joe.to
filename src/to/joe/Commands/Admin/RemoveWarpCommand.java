package to.joe.Commands.Admin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class RemoveWarpCommand extends MasterCommand {

    public RemoveWarpCommand(J2 j2) {
        super(j2);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
        if (isPlayer && this.j2.hasFlag(player, Flag.ADMIN)) {
            if (args.length == 0) {
                player.sendMessage(ChatColor.RED + "Usage: /removewarp warpname");
            } else {
                String toRemove = args[0];
                player.sendMessage(ChatColor.RED + "Removing warp " + toRemove);
                this.j2.warps.killWarp(this.j2.warps.getPublicWarp(toRemove));
            }
        }
    }
}
