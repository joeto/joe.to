package to.joe.Commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.util.Flag;

public class MessageCommand extends MasterCommand {

    public MessageCommand(J2 j2) {
        super(j2);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
        if (isPlayer) {
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Correct usage: /msg player message");
                return;
            }
            String targetName = args[0];
            boolean admin = this.j2.hasFlag(player, Flag.ADMIN);
            Player target = this.j2.minitrue.getPlayer(targetName, admin);
            if (target == null) {
                List<Player> inquest = this.j2.minitrue.matchPlayer(args[0], admin);
                int matches = inquest.size();
                if (matches > 1) {
                    player.sendMessage(ChatColor.RED + "That matches multiple names!");
                    return;
                }
                if (matches == 0) {
                    player.sendMessage(ChatColor.RED + "No such player");
                    return;
                }
                target = inquest.get(0);
                if (target == null) {
                    player.sendMessage(ChatColor.RED + "Cannot find player");
                    return;
                }
            }
            this.j2.chat.handlePrivateMessage(player, target, this.j2.combineSplit(1, args, " "));
        }
    }

}
