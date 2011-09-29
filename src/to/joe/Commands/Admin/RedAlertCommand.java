package to.joe.Commands.Admin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class RedAlertCommand extends MasterCommand {

	public RedAlertCommand(J2 j2) {
		super(j2);
	}

	@Override
	public void exec(CommandSender sender, String commandName, String[] args,
			Player player, String playerName, boolean isPlayer) {
		if (commandName == "redalert") {
			if (!isPlayer || this.j2.hasFlag(player, Flag.ADMIN)) {
				if (this.j2.RedAlert == true) {
					player.sendMessage(ChatColor.RED
							+ "Red alert mode already active");
				} else {
					this.j2.RedAlert = true;
					player.sendMessage(ChatColor.AQUA
							+ "Red alert mode engaged, all users have been flagged");
					this.j2.irc
							.messageAdmins(player.getName()
									+ " has initiated red alert mode on server, admins to server ASAP");
					this.j2.users.RedAlertMarkSafePlayers();
				}
			}
		}
		if (commandName == "greenalert") {
			if (!isPlayer || this.j2.hasFlag(player, Flag.ADMIN)) {
				if (this.j2.RedAlert == false) {
					player.sendMessage(ChatColor.RED
							+ "Red alert mode not active");
				} else {
					this.j2.RedAlert = false;
					player.sendMessage(ChatColor.AQUA
							+ "Red alert mode deactivated");
					this.j2.irc.messageAdmins("Red alert mode deactivated.");
					this.j2.users.UnmarkUsers();
				}
			}
		}
	}

}
