package to.joe.Commands.Admin;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class CoordinateTeleportCommand extends MasterCommand {

    public CoordinateTeleportCommand(J2 j2) {
        super(j2);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
        if (isPlayer && this.j2.hasFlag(player, Flag.ADMIN)) {
            if (args.length < 3) {
                player.sendMessage(ChatColor.RED + "You did not specify an X, Y, and Z");
            } else {
                this.j2.safePort(player, new Location(player.getWorld(), Double.valueOf(args[0]), Double.valueOf(args[1]), Double.valueOf(args[2]), 0, 0));
                player.sendMessage(ChatColor.RED + "WHEEEEE I HOPE THIS ISN'T UNDERGROUND");
            }
        }
    }
}
