package to.joe.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.util.Flag;

public class TrustedRequestCommand extends MasterCommand {

	public TrustedRequestCommand(J2 j2) {
		super(j2);
	}

	@Override
	public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
		if(isPlayer){
			if(this.j2.hasFlag(player, Flag.TRUSTED)){
				if(this.j2.hasFlag(player, Flag.TRUSTREQ)){
					this.j2.users.dropFlag(playerName, Flag.TRUSTREQ);
					player.sendMessage(ChatColor.AQUA + "No longer recieving trusted requests.");
				}
				else{
					this.j2.users.addFlag(playerName, Flag.TRUSTREQ);
					player.sendMessage(ChatColor.AQUA + "You are now recieving trusted requests.");
				}
			}
			else{
				if(args.length==0){
					player.sendMessage(ChatColor.RED + "Usage: /trustreq");
					player.sendMessage(ChatColor.RED + "Usage: Request trusted assistance.");
					player.sendMessage(ChatColor.RED + "Ex: /trustreq need some water please");
				}
				else{
					String message=this.j2.combineSplit(0, args, " ");
					this.j2.chat.messageByFlag(Flag.TRUSTREQ, ChatColor.AQUA + "[TRUSTED REQUEST] <"+ChatColor.DARK_AQUA + playerName + ChatColor.AQUA+"> "+message);
					player.sendMessage(ChatColor.AQUA + "Request sent. Be patient :)");
				}
			}
		}
	}		
}
