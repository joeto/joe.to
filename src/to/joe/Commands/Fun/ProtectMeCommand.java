package to.joe.Commands.Fun;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class ProtectMeCommand extends MasterCommand {

    public ProtectMeCommand(J2 j2) {
        super(j2);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
        if (isPlayer && this.j2.hasFlag(playerName, Flag.TRUSTED)) {
            final String playersName = player.getName().toLowerCase();
            if (this.j2.tpProtect.getBoolean(playersName, false)) {
                this.j2.tpProtect.setBoolean(playersName, false);
                player.sendMessage(ChatColor.RED + "You are now no longer protected from teleportation");
            } else {
                this.j2.tpProtect.setBoolean(playersName, true);
                player.sendMessage(ChatColor.RED + "You are protected from teleportation");
            }
        }
    }
}
