package to.joe.Commands.Admin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;
import to.joe.util.Warp;

public class HomeInvasionCommand extends MasterCommand{

	public HomeInvasionCommand(J2 j2) {
		super(j2);
	}

	@Override
	public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
		if(isPlayer && this.j2.hasFlag(player, Flag.ADMIN)){
			if(args.length==0){
				player.sendMessage(ChatColor.RED+"Usage: /homeinvasion player");
				player.sendMessage(ChatColor.RED+"      to get a list");
				player.sendMessage(ChatColor.RED+"       /homeinvasion player homename");
				player.sendMessage(ChatColor.RED+"      to visit a specific home");
			}
			if(args.length==1){
				String target=args[0];
				boolean isOnline=this.j2.users.isOnline(target);
				if(!isOnline){
					this.j2.warps.loadPlayer(target);
				}
				player.sendMessage(ChatColor.RED+target+" warps: "+ChatColor.WHITE+this.j2.warps.listHomes(target));
				if(!isOnline){
					this.j2.warps.dropPlayer(target);
				}
			}
			if(args.length==2){
				String target=args[0];
				boolean isOnline=this.j2.users.isOnline(target);
				if(!isOnline){
					this.j2.warps.loadPlayer(target);
				}
				Warp warptarget=this.j2.warps.getUserWarp(target, args[1]);
				if(warptarget!=null){
					player.sendMessage(ChatColor.RED+"Whooooosh!  *crash*");
					this.j2.safePort(player, warptarget.getLocation());
				}
				else {
					player.sendMessage(ChatColor.RED+"No such home");
				}
				if(!isOnline){
					this.j2.warps.dropPlayer(target);
				}
			}
		}
	}
}
