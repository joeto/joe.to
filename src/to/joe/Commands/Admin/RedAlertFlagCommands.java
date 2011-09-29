package to.joe.Commands.Admin;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class RedAlertFlagCommands extends MasterCommand {

	public RedAlertFlagCommands(J2 j2) {
		super(j2);
	}

	@Override
	public void exec(CommandSender sender, String commandName, String[] args,
			Player player, String playerName, boolean isPlayer) {
	if (!isPlayer || this.j2.hasFlag(player, Flag.ADMIN)) {
		if(commandName == "safe"){
			if(this.j2.RedAlert == false){
				player.sendMessage("Red alert not active!");
			}
			else{
			this.j2.users.addFlag(playerName, Flag.SAFE);
			}
		}
		if(commandName == "flag"){
			if(this.j2.RedAlert == false){
				player.sendMessage("Red alert not active!");
			}
			else{
			this.j2.users.dropFlag(playerName, Flag.SAFE);
			this.j2.users.dropFlag(playerName, Flag.PERMSAFE);
			}
		}
		if(commandName == "permsafe"){
			this.j2.users.addFlag(playerName, Flag.PERMSAFE);
		}
	}
}
}
