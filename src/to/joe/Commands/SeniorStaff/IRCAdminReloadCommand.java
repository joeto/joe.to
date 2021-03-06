package to.joe.Commands.SeniorStaff;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class IRCAdminReloadCommand extends MasterCommand {

    public IRCAdminReloadCommand(J2 j2) {
        super(j2);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
        if (!isPlayer || this.j2.hasFlag(player, Flag.SRSTAFF)) {
            this.j2.irc.reloadIRCAdmins();
            this.j2.chat.messageByFlag(Flag.SRSTAFF, ChatColor.RED + "IRC admins reloaded by " + playerName);
            this.j2.log(playerName + " reloaded irc admins");
        }
    }
}
