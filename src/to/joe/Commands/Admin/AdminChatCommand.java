package to.joe.Commands.Admin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class AdminChatCommand extends MasterCommand {

    public AdminChatCommand(J2 j2) {
        super(j2);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
        if (!isPlayer || this.j2.hasFlag(player, Flag.ADMIN)) {
            if (args.length < 1) {
                sender.sendMessage(ChatColor.RED + "Usage: /a Message");
                return;
            }
            final String message = this.j2.combineSplit(0, args, " ");
            this.j2.chat.adminOnlyMessage(playerName, message);
        }
    }
}
