package to.joe.Commands.Admin;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class BanCommand extends MasterCommand{

	public BanCommand(J2 j2) {
		super(j2);
	}

	@Override
	public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
		if(!isPlayer || this.j2.hasFlag(player, Flag.ADMIN)){
			if(args.length < 2){
				sender.sendMessage(ChatColor.RED+"Usage: /ban playername reason");
				sender.sendMessage(ChatColor.RED+"       reason can have spaces in it");
				return;
			}
			Location loc;
			if(!isPlayer){
				loc=new Location(this.j2.getServer().getWorlds().get(0),0,0,0);
			}
			else{
				loc=player.getLocation();
			}
			this.j2.kickbans.callBan(playerName,args,loc);
		}		
	}
}
