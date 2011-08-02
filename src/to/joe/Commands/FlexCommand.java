package to.joe.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class FlexCommand extends MasterCommand{

	public FlexCommand(J2 j2) {
		super(j2);
	}

	@Override
	public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
		if(!isPlayer||this.j2.hasFlag(player,Flag.SRSTAFF)||playerName.equalsIgnoreCase("MrBlip")){
			String message=""+ChatColor.GOLD;
			switch(this.j2.random.nextInt(5)){
			case 0:
				message+="All the ladies watch as "+playerName+" flexes";break;
			case 1:
				message+="Everybody stares as "+playerName+" flexes";break;
			case 2:
				message+="Sexy party! "+playerName+" flexes and the gods stare";break;
			case 3:
				message+=playerName+" is too sexy for this party";break;
			case 4: 
				message+=playerName+" knows how to flex";break;
			}
			if(playerName.equalsIgnoreCase("MrBlip")&&this.j2.random.nextBoolean()){
				if(this.j2.random.nextBoolean())
					message=ChatColor.GOLD+"MrBlip shows off his chin";
				else
					message=ChatColor.GOLD+"MrBlip shows off his hat";
			}
			this.j2.chat.messageAll(message);
			this.j2.log(ChatColor.GOLD+playerName+" flexed.");
		}		
	}
}
