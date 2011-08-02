package to.joe.Commands.Admin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class HarassCommand extends MasterCommand{

	public HarassCommand(J2 j2) {
		super(j2);
	}

	@Override
	public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
		if(!isPlayer || this.j2.hasFlag(player, Flag.ADMIN)){
			if(args.length!=1){
				sender.sendMessage(ChatColor.AQUA+"Missing a name!");
				return;
			}
			Player target= this.j2.getServer().getPlayer(args[0]);
			if(target==null||!target.isOnline()){
				sender.sendMessage(ChatColor.AQUA+"Fail. No such user \""+args[0]+"\"");
				return;
			}
			if(! this.j2.panda.panda(target)){
				this.j2.panda.harass(target.getName());
				this.j2.sendAdminPlusLog(ChatColor.AQUA+"[HARASS] Target Acquired: "+ChatColor.DARK_AQUA+target.getName()+ChatColor.AQUA+". Thanks, "+playerName+"!");
				this.j2.irc.messageAdmins("[HARASS] Target Acquired: "+target.getName()+". Thanks, "+playerName+"!");
			}
			else{
				this.j2.panda.remove(target.getName());
				this.j2.sendAdminPlusLog(ChatColor.AQUA+"[HARASS] Target Removed: "+ChatColor.DARK_AQUA+target.getName()+ChatColor.AQUA+". Thanks, "+playerName+"!");
				this.j2.irc.messageAdmins("[HARASS] Target Removed: "+target.getName()+". Thanks, "+playerName+"!");
			}
		}
	}
}
