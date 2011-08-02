package to.joe.Commands.Admin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class ShushCommand extends MasterCommand{

	public ShushCommand(J2 j2) {
		super(j2);
	}

	@Override
	public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
		if(isPlayer && this.j2.hasFlag(player, Flag.ADMIN)){
			if(this.j2.hasFlag(playerName,Flag.SHUT_OUT_WORLD)){
				this.j2.users.dropFlagLocal(playerName, Flag.SHUT_OUT_WORLD);
				this.j2.sendAdminPlusLog(ChatColor.DARK_AQUA+playerName+" can now hear you again");
			}
			else{
				this.j2.users.addFlagLocal(playerName, Flag.SHUT_OUT_WORLD);
				this.j2.sendAdminPlusLog(ChatColor.DARK_AQUA+playerName+" has fingers to ears and is singing");
			}
		}
	}
}
