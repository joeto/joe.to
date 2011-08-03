package to.joe.Commands.Admin;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class WhereIsPlayerCommand extends MasterCommand {

    public WhereIsPlayerCommand(J2 j2) {
        super(j2);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
        if (!isPlayer || this.j2.hasFlag(player, Flag.ADMIN)) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "/whereis player");
            } else {
                List<Player> possible = this.j2.getServer().matchPlayer(args[0]);
                if (possible.size() == 1) {
                    Player who = possible.get(0);
                    Location loc = who.getLocation();
                    sender.sendMessage(ChatColor.RED + who.getName() + ": " + loc.getX() + " " + loc.getY() + " " + loc.getZ());
                } else {
                    sender.sendMessage(ChatColor.RED + args[0] + " does not work. Either 0 or 2+ matches.");
                }
            }
        }
    }
}
