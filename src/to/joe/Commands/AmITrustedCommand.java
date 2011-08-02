package to.joe.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.util.Flag;

public class AmITrustedCommand extends MasterCommand {

	public AmITrustedCommand(J2 j2) {
		super(j2);
	}

	@Override
	public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
		if (isPlayer)
		{
			if(this.j2.hasFlag(player, Flag.TRUSTED)){
				player.sendMessage(ChatColor.AQUA+"You are trusted! Yay!");
			}
			else{
				player.sendMessage(ChatColor.AQUA+"You are not trusted. Get it!");
				player.sendMessage(ChatColor.AQUA+"Visit http://forums.joe.to   Minecraft section");
			}
		}
	}
}
