package to.joe.Commands.SeniorStaff;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class MaxPlayersCommand extends MasterCommand {

	public MaxPlayersCommand(J2 j2) {
		super(j2);
	}

	@Override
	public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
		if(!isPlayer ||this.j2.hasFlag(player, Flag.SRSTAFF) &&args.length>0){
			int newCount;
			try{
				newCount=Integer.parseInt(args[0]);
			}
			catch(NumberFormatException e){
				newCount=this.j2.playerLimit;
			}
			this.j2.playerLimit=newCount;
			this.j2.sendAdminPlusLog(ChatColor.RED+playerName+" set max players to "+this.j2.playerLimit);
		}
	}
}
