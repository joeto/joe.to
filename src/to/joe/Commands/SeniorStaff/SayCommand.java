package to.joe.Commands.SeniorStaff;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class SayCommand extends MasterCommand {

    public SayCommand(J2 j2) {
        super(j2);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
        if (!isPlayer || this.j2.hasFlag(player, Flag.SRSTAFF)) {
            if (args.length < 1) {
                sender.sendMessage(ChatColor.RED + "Dude, you gotta /say SOMETHING");
                return;
            }
            final String message = ChatColor.LIGHT_PURPLE + "[SERVER] " + this.j2.combineSplit(0, args, " ");
            this.j2.log(message);
            this.j2.chat.messageAll(message);
        }
    }
}
