package to.joe.Commands.SeniorStaff;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class MobCommand extends MasterCommand {

    public MobCommand(J2 j2) {
        super(j2);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
        if (isPlayer && this.j2.hasFlag(player, Flag.SRSTAFF)) {
            if (args.length == 0) {
                player.sendMessage(ChatColor.RED + "/mob mobname");
            } else {
                CreatureType creat = CreatureType.fromName(args[0]);
                if (creat != null) {
                    Block block = player.getTargetBlock(null, 50);
                    if (block != null) {
                        Location bloc = block.getLocation();
                        if (bloc.getY() < 126) {
                            Location loc = new Location(bloc.getWorld(), bloc.getX(), bloc.getY() + 1, bloc.getZ());
                            player.getWorld().spawnCreature(loc, CreatureType.fromName(args[0]));
                        }
                    }
                }
            }
        }
    }
}
