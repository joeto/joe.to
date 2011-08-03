package to.joe.Commands.Admin;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class MuteCommand extends MasterCommand {

    public MuteCommand(J2 j2) {
        super(j2);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
        if (!isPlayer || this.j2.hasFlag(player, Flag.ADMIN)) {
            String messageBit = "";
            if (args.length < 1) {
                sender.sendMessage(ChatColor.RED + "Requires a name. /mute name");
                return;
            }
            final String targetString = args[0];
            final List<Player> matches = this.j2.getServer().matchPlayer(targetString);
            if ((matches == null) || (matches.size() == 0)) {
                sender.sendMessage(ChatColor.RED + "No matches for " + targetString);
                return;
            }
            if (matches.size() > 1) {
                sender.sendMessage(ChatColor.RED + String.valueOf(matches.size()) + " matches for " + targetString);
                return;
            }
            final Player target = matches.get(0);
            final String targetName = target.getName();
            final boolean muted = this.j2.hasFlag(targetName, Flag.MUTED);
            if (muted) {
                messageBit = "un";
                this.j2.users.dropFlagLocal(targetName, Flag.MUTED);
            } else {
                this.j2.users.addFlagLocal(targetName, Flag.MUTED);
            }
            target.sendMessage(ChatColor.YELLOW + "You have been " + messageBit.toUpperCase() + "MUTED");
            this.j2.sendAdminPlusLog(ChatColor.YELLOW + playerName + " has " + messageBit + "muted " + targetName);
        }
    }
}
