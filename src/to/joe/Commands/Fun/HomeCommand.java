package to.joe.Commands.Fun;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;
import to.joe.util.Warp;

public class HomeCommand extends MasterCommand {

    public HomeCommand(J2 j2) {
        super(j2);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
        if (isPlayer && this.j2.hasFlag(player, Flag.FUN)) {
            if (args.length == 0) {
                final String homes_s = this.j2.warps.listHomes(player.getName());
                if (!homes_s.equalsIgnoreCase("")) {
                    player.sendMessage(ChatColor.RED + "Homes: " + ChatColor.WHITE + homes_s);
                    player.sendMessage(ChatColor.RED + "To go to a home, say /home homename");
                } else {
                    player.sendMessage(ChatColor.RED + "You have no homes available.");
                    player.sendMessage(ChatColor.RED + "Use the command /sethome");
                }
            } else {
                final Warp home = this.j2.warps.getUserWarp(player.getName(), args[0]);
                if (home != null) {
                    player.sendMessage(ChatColor.RED + "Whoosh!");
                    this.j2.safePort(player, home.getLocation());
                } else {
                    player.sendMessage(ChatColor.RED + "That home does not exist. For a list, say /home");
                }
            }
        }
    }
}
