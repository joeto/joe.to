package to.joe.Commands.Admin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;
import to.joe.util.Warp;

public class SetWarpCommand extends MasterCommand{

	public SetWarpCommand(J2 j2) {
		super(j2);
	}

	@Override
	public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
		if(isPlayer && this.j2.hasFlag(player, Flag.ADMIN)){
			if(args.length==0){
				player.sendMessage(ChatColor.RED+"Usage: /setwarp warpname");
				player.sendMessage(ChatColor.RED+"optional: /setwarp warpname flag");
				player.sendMessage(ChatColor.RED+"Admin flag is a, trusted is t");
			}
			else{
				Flag flag=Flag.PLAYER_WARP_PUBLIC;
				if(args.length>1){
					flag=Flag.byChar(args[1].charAt(0));
				}
				Warp newWarp=new Warp(args[0], player.getName(), player.getLocation(), flag);
				this.j2.warps.addWarp(newWarp);
				player.sendMessage(ChatColor.RED+"Warp created");
			}
		}
	}
}
