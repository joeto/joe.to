package to.joe.Commands.Admin;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class SlayCommand extends MasterCommand{

	public SlayCommand(J2 j2) {
		super(j2);
	}

	@Override
	public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
		if(!isPlayer || this.j2.hasFlag(player, Flag.ADMIN)){
			if(args.length==0){
				sender.sendMessage(ChatColor.RED+"I can't kill anyone if you don't tell me whom");
				return;
			}
			List<Player> list=this.j2.minitrue.matchPlayer(args[0],true);
			if(list.size()==0){
				sender.sendMessage(ChatColor.RED+"That matches nobody, smart stuff");
				return;
			}
			if(list.size()>1){
				sender.sendMessage(ChatColor.RED+"That matches more than one, smart stuff");
				return;
			}
			Player target=list.get(0);
			if(target!=null){
				target.damage(9001);
				target.sendMessage(ChatColor.RED+"You have been slayed");
				this.j2.sendAdminPlusLog(ChatColor.RED+playerName+" slayed "+target.getName());
			}
		}
	}
}
