package to.joe.Commands.Admin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class MuteAllCommand extends MasterCommand{

	public MuteAllCommand(J2 j2) {
		super(j2);
	}

	@Override
	public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
		if(!isPlayer || this.j2.hasFlag(player, Flag.ADMIN)){
			String messageBit;
			if(this.j2.chat.muteAll){
				messageBit=" has unmuted all players";
			}
			else{
				messageBit=" has muted all players";
			}
			this.j2.sendAdminPlusLog(ChatColor.YELLOW+playerName+messageBit);
			this.j2.chat.messageByFlagless(Flag.ADMIN, ChatColor.YELLOW+"The ADMIN"+messageBit);
			this.j2.chat.muteAll=!this.j2.chat.muteAll;
		}
	}
}
