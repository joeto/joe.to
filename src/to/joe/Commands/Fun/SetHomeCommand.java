package to.joe.Commands.Fun;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;
import to.joe.util.Warp;

public class SetHomeCommand extends MasterCommand {

    public SetHomeCommand(J2 j2) {
        super(j2);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
        if (isPlayer && this.j2.hasFlag(player, Flag.FUN)) {
            if (args.length == 0) {
                player.sendMessage(ChatColor.RED + "Usage: /sethome name");
            } else {
                final Warp newWarp = new Warp(args[0], player.getName(), player.getLocation(), Flag.PLAYER_HOME);
                this.j2.warps.addWarp(newWarp);
                player.sendMessage(ChatColor.RED + "Home created");
            }
        }
    }
}
