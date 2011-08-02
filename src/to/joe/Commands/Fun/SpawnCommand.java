package to.joe.Commands.Fun;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class SpawnCommand extends MasterCommand {

	public SpawnCommand(J2 j2) {
		super(j2);
	}

	@Override
	public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
		if(!isPlayer ||this.j2.hasFlag(player, Flag.FUN)){
			if(isPlayer && (!this.j2.hasFlag(player, Flag.ADMIN)|| args.length<1)){
				player.sendMessage(ChatColor.RED+"WHEEEEEEEEEEEEEEE");
				this.j2.safePort(player, player.getWorld().getSpawnLocation());
			}
			else if (args.length ==1){
				List<Player> inquest = this.j2.getServer().matchPlayer(args[0]);
				if(inquest.size()==1){
					Player inquestion=inquest.get(0);
					this.j2.safePort(inquestion, inquestion.getWorld().getSpawnLocation());
					inquestion.sendMessage(ChatColor.RED+"OH GOD I'M BEING PULLED TO SPAWN OH GOD");
					this.j2.sendAdminPlusLog(ChatColor.RED+playerName+" pulled "+inquestion.getName()+" to spawn");
				}
				else {
					sender.sendMessage(ChatColor.RED+"No such player, or matches multiple");
				}
			}
			else{
				sender.sendMessage(ChatColor.RED+"Usage: /spawn playername");
			}
		}		
	}

}
