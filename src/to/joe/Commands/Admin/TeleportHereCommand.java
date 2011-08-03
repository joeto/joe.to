package to.joe.Commands.Admin;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class TeleportHereCommand extends MasterCommand {

    public TeleportHereCommand(J2 j2) {
        super(j2);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
        if (isPlayer && this.j2.hasFlag(player, Flag.ADMIN)) {
            String targetName = args[0];
            Player target = this.j2.getServer().getPlayer(targetName);
            if (target == null) {
                List<Player> inquest = this.j2.getServer().matchPlayer(args[0]);
                int matches = inquest.size();
                if (matches == 1) {
                    target = inquest.get(0);
                } else if (matches > 1) {
                    player.sendMessage(ChatColor.RED + "Matches multiple");
                    return;
                }
                if (target == null) {
                    player.sendMessage(ChatColor.RED + "No such player");
                    return;
                }
            }
            if (target.getName().equalsIgnoreCase(player.getName())) {
                player.sendMessage(ChatColor.RED + "Can't teleport yourself to yourself. Derp.");
            } else {
                this.j2.safePort(target, player.getLocation());
                target.sendMessage("You've been teleported");
                player.sendMessage("Grabbing " + target.getName());
                this.j2.sendAdminPlusLog(ChatColor.AQUA + playerName + " pulled " + target.getName() + " to self");
            }
        }
    }
}
