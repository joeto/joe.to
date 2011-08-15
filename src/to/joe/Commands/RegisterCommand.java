package to.joe.Commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RegisterCommand extends MasterCommand {

    public RegisterCommand(J2 j2) {
        super(j2);
    }

    @Override
	public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
		if (isPlayer){
		    if(args.length!=1 || args[0].length()!=5){
		        player.sendMessage(ChatColor.RED + "Usage: /register key");
		        player.sendMessage(ChatColor.RED + "Key is your 5 digit code found on http://joe.to/trusted");
		    }
		    else{
		        if(this.j2.mysql.isRegistered(playerName, args[0])){
		            player.sendMessage(ChatColor.RED + "Your account is already linked!");
		        }
		        if(!this.j2.mysql.authCorrect(playerName, args[0])){
		            player.sendMessage(ChatColor.RED + "Your key is incorrect!")
		        }
		        else{
		            this.j2.mysql.addLink(playerName, args[0]);
		        }
		    }
		}
	}
}