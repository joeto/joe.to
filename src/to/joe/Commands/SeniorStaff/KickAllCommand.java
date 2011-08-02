package to.joe.Commands.SeniorStaff;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class KickAllCommand extends MasterCommand{

	public KickAllCommand(J2 j2) {
		super(j2);
	}

	@Override
	public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
		if(!isPlayer||j2.hasFlag(player,Flag.SRSTAFF)){
			String reason="";
			if(args.length>0){
				reason=this.j2.combineSplit(0, args, " ");
			}
			this.j2.kickbans.kickAll(reason);
			this.j2.log(playerName+" kicked all: "+reason);
		}
	}

}
