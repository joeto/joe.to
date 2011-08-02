package to.joe.Commands.SeniorStaff;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class MaintenanceCommand extends MasterCommand {

	public MaintenanceCommand(J2 j2) {
		super(j2);
	}

	@Override
	public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
		if(!isPlayer ||this.j2.hasFlag(player, Flag.SRSTAFF)){
			if(!this.j2.maintenance){
				this.j2.sendAdminPlusLog(ChatColor.AQUA+playerName+" has turned on maintenance mode");
				this.j2.maintenance=true;
				for (Player p : this.j2.getServer().getOnlinePlayers()) {
					if (p != null && !this.j2.hasFlag(p, Flag.ADMIN)) {
						p.sendMessage(ChatColor.AQUA+"Server entering maintenance mode");
						p.kickPlayer("Server entering maintenance mode");
					}
				}
			}
			else{
				this.j2.sendAdminPlusLog(ChatColor.AQUA+playerName+" has turned off maintenance mode");
				this.j2.maintenance=false;
			}

		}
	}
}
