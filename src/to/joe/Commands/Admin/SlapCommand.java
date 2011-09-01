package to.joe.Commands.Admin;

import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class SlapCommand extends MasterCommand {

    public SlapCommand(J2 j2) {
        super(j2);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
        if (!isPlayer || this.j2.hasFlag(player, Flag.ADMIN)) {
            float force = 0;
            if (args.length < 1) {
                player.sendMessage(ChatColor.RED + "Usage: /slap player force");
            } else {
                if (args.length == 1) {
                    force = 5;
                } else {
                    force = new Float(args[1]);
                }
                final List<Player> results = this.j2.minitrue.matchPlayer(args[0], true);
                if (results.size() == 1) {
                    final Random randomGen = new Random();
                    final Vector newVelocity = new Vector(((randomGen.nextFloat() * 1.5) - 0.75) * force, (randomGen.nextFloat() / 2.5) + (0.4 * force), ((randomGen.nextFloat() * 1.5) - 0.75) * force);
                    final Player target = results.get(0);
                    target.setVelocity(newVelocity);
                    this.j2.chat.messageByFlag(Flag.ADMIN, ChatColor.RED + player.getName() + " slapped " + target.getName());
                } else if (results.size() > 1) {
                    sender.sendMessage(ChatColor.RED + "Matches too many players");
                } else {
                    sender.sendMessage(ChatColor.RED + "Matches no players");
                }
            }
        }
    }
}
