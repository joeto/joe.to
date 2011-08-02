package to.joe.Commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;

public class GetLocationCommand extends MasterCommand {

	public GetLocationCommand(J2 j2) {
		super(j2);
	}

	@Override
	public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
		if(isPlayer){
			Location loc=player.getLocation();
			String x=""+ChatColor.GOLD+(int)loc.getX()+ChatColor.DARK_AQUA;
			String y=""+ChatColor.GOLD+(int)loc.getY()+ChatColor.DARK_AQUA;
			String z=""+ChatColor.GOLD+(int)loc.getZ();
			player.sendMessage(ChatColor.DARK_AQUA+"You are at X:"+x+" Y:"+y+" Z:"+z);
		}
	}		
}
