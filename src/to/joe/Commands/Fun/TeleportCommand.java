package to.joe.Commands.Fun;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class TeleportCommand extends MasterCommand {

	public TeleportCommand(J2 j2) {
		super(j2);
	}

	@Override
	public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
		if(isPlayer && this.j2.hasFlag(player, Flag.FUN)){
			if(args.length==0){
				player.sendMessage(ChatColor.RED+"Usage: /tp playername");
				return;
			}
			boolean admin=this.j2.hasFlag(player, Flag.ADMIN);
			String targetName=args[0];
			Player target=this.j2.minitrue.getPlayer(targetName,admin);
			if(target==null){
				List<Player> inquest = this.j2.minitrue.matchPlayer(args[0],admin);
				int matches=inquest.size();
				if(matches>1){
					player.sendMessage(ChatColor.RED+"That matches multiple names!");
					return;
				}
				if(matches==0){
					player.sendMessage(ChatColor.RED+"No such player");
					return;
				}
				target=inquest.get(0);
				if(target==null){
					player.sendMessage(ChatColor.RED+"Cannot teleport");
					return;
				}
			}
			if(!admin && (this.j2.hasFlag(target, Flag.TRUSTED)) && this.j2.tpProtect.getBoolean(target.getName().toLowerCase(), false)){
				player.sendMessage(ChatColor.RED + "Cannot teleport to protected player.");
			}
			else if(target.getName().equalsIgnoreCase(player.getName())){
				player.sendMessage(ChatColor.RED+"Can't teleport to yourself");
			}
			else {
				this.j2.safePort(player, target.getLocation());
				player.sendMessage("OH GOD I'M FLYING AAAAAAAAH");
				this.j2.log("Teleport: " + player.getName() + " teleported to "+target.getName());
			}
		}
	}		
}

