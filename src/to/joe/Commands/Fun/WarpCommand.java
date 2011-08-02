package to.joe.Commands.Fun;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;
import to.joe.util.Warp;

public class WarpCommand extends MasterCommand {

	public WarpCommand(J2 j2) {
		super(j2);
	}

	@Override
	public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
		if(isPlayer && this.j2.hasFlag(player, Flag.FUN)){
			if(args.length==0){
				String warps_s=this.j2.warps.listWarps(player);
				if(!warps_s.equalsIgnoreCase("")){
					player.sendMessage(ChatColor.RED+"Warp locations: "+ChatColor.WHITE+warps_s);
					player.sendMessage(ChatColor.RED+"To go to a warp, say /warp warpname");
				}else{
					player.sendMessage("There are no warps available.");
				}
			}
			else{
				String target=args[0];
				Warp warp=this.j2.warps.getPublicWarp(target);
				if(warp!=null && (this.j2.hasFlag(player, warp.getFlag())||warp.getFlag().equals(Flag.PLAYER_WARP_PUBLIC))){
					player.sendMessage(ChatColor.RED+"Welcome to: "+ChatColor.LIGHT_PURPLE+target);
					this.j2.log(ChatColor.AQUA+"Player "+playerName+" went to warp "+target);
					this.j2.safePort(player, warp.getLocation());
				}
				else {
					player.sendMessage(ChatColor.RED+"Warp does not exist. For a list, say /warp");
				}
			}
		}
	}		
}
