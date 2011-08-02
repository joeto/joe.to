package to.joe.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;

/**
 * Abstract class from which all j2 commands come
 */
public abstract class MasterCommand implements CommandExecutor{
	protected J2 j2;
	
	public MasterCommand(J2 j2){
		this.j2=j2;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String commandName = command.getName().toLowerCase();
		Player player=null;
		String playerName="Console";
		boolean isPlayer=(sender instanceof Player);
		if(isPlayer){
			player=(Player)sender;
			playerName=player.getName();
		}
		
		this.exec(sender, commandName, args, player, playerName, isPlayer);
		
		return true;
	}
	
	public abstract void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer);
	
}
