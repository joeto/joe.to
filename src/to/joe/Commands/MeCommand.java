package to.joe.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;

public class MeCommand extends MasterCommand {

	public MeCommand(J2 j2) {
		super(j2);
	}

	@Override
	public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
		if (isPlayer && commandName.equals("me") && args.length>0)
		{
			this.j2.chat.handleChat(player, this.j2.combineSplit(0, args, " "), true);
		}
	}
}
