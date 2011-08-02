package to.joe.Commands.SeniorStaff;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class FlagsCommand extends MasterCommand {

	public FlagsCommand(J2 j2) {
		super(j2);
	}

	@Override
	public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
		if(!isPlayer ||this.j2.hasFlag(player, Flag.SRSTAFF)){
			if(args.length<3){
				sender.sendMessage(ChatColor.RED+"Usage: /flags player add/drop flag");
				return;
			}
			String action=args[1];
			if(!(action.equalsIgnoreCase("add") || action.equalsIgnoreCase("drop"))){
				sender.sendMessage(ChatColor.RED+"Usage: /flags player add/drop flag");
				return;
			}
			String name=args[0];
			char flag=args[2].charAt(0);
			if(action.equalsIgnoreCase("add")){
				this.j2.users.addFlag(name,Flag.byChar(flag));
			}
			else {
				this.j2.users.dropFlag(name,Flag.byChar(flag));
			}
			String tolog=ChatColor.RED+playerName+" changed flags: "+name + " "+ action +" flag "+ Flag.byChar(flag).getDescription();
			this.j2.chat.messageByFlag(Flag.ADMIN, tolog);
			this.j2.log(tolog);
		}
	}
}
