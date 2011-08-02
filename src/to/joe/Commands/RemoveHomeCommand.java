package to.joe.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.util.Flag;

public class RemoveHomeCommand extends MasterCommand {

	public RemoveHomeCommand(J2 j2) {
		super(j2);
	}

	@Override
	public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
		if(isPlayer && this.j2.hasFlag(player, Flag.FUN)){
			if(args.length==0){
				player.sendMessage(ChatColor.RED+"Usage: /removehome homename");
				if(this.j2.hasFlag(player, Flag.ADMIN)){
					player.sendMessage(ChatColor.RED+"Or: /removehome homename playername");
				}
			}
			if(args.length==1){
				String toRemove=args[0];
				player.sendMessage(ChatColor.RED+"Removing home "+toRemove);
				this.j2.warps.killWarp(this.j2.warps.getUserWarp(player.getName(), toRemove));
			}
			if(args.length==2 && this.j2.hasFlag(player, Flag.ADMIN)){
				String toRemove=args[0];
				String plr=args[1];
				player.sendMessage(ChatColor.RED+"Removing home "+toRemove+" of player "+plr);
				this.j2.warps.killWarp(this.j2.warps.getUserWarp(plr, toRemove));
			}
		}
	}		
}
