package to.joe.Commands.Admin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class TimeCommand extends MasterCommand{

	public TimeCommand(J2 j2) {
		super(j2);
	}

	@Override
	public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
		if(!isPlayer ||this.j2.hasFlag(player, Flag.ADMIN)){
			if(args.length!=1){
				sender.sendMessage(ChatColor.RED+"Usage: /time day|night");
				return;
			}
			long desired;
			if(args[0].equalsIgnoreCase("day")){
				desired=0;
			}
			else if(args[0].equalsIgnoreCase("night")){
				desired=13000;
			}
			else{
				sender.sendMessage(ChatColor.RED+"Usage: /time day|night");
				return;
			}
			long curTime=this.j2.getServer().getWorlds().get(0).getTime();
			long margin = (desired-curTime) % 24000;
			if (margin < 0) {
				margin += 24000;
			}
			this.j2.getServer().getWorlds().get(0).setTime(curTime+margin);
			this.j2.sendAdminPlusLog(ChatColor.DARK_AQUA+playerName+" changed time");
		}
	}
}
