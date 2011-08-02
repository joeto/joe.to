package to.joe.Commands.Admin;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class SmiteCommand extends MasterCommand{

	public SmiteCommand(J2 j2) {
		super(j2);
	}

	@Override
	public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
		if(!isPlayer || this.j2.hasFlag(player, Flag.ADMIN)){
			if(args.length==0){
				sender.sendMessage(ChatColor.RED+"/smite player");
				return;
			}
			List<Player> results=this.j2.minitrue.matchPlayer(args[0],true);
			if(results.size()==1){
				Player target=results.get(0);
				boolean weather=target.getWorld().isThundering();
				this.j2.damage.danger(target.getName());
				this.j2.damage.addToTimer(target.getName());
				target.getWorld().strikeLightning(target.getLocation());
				//player.sendMessage(ChatColor.RED+"Judgment enacted");
				this.j2.sendAdminPlusLog( ChatColor.RED+playerName+" has zapped "+target.getName());
				target.sendMessage(ChatColor.RED+"You have been judged");
				//this.damage.processJoin(playerName);
				target.getWorld().setStorm(weather);
			}
			else if(results.size()>1){
				sender.sendMessage(ChatColor.RED+"Matches too many players");
			}
			else{
				sender.sendMessage(ChatColor.RED+"Matches no players");
			}
		}
	}
}
