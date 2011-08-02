package to.joe.Commands.Admin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class GodmodeCommand extends MasterCommand{

	public GodmodeCommand(J2 j2) {
		super(j2);
	}

	@Override
	public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
		if(isPlayer && this.j2.hasFlag(player, Flag.ADMIN)){
			if(commandName.equals("kibbles")){
				this.j2.sendAdminPlusLog( ChatColor.RED+playerName+" enabled GODMODE");
				if(args.length>0&&args[0].equalsIgnoreCase("a"))
					this.j2.chat.messageAll(ChatColor.RED+"    "+playerName+" is an admin. Pay attention to "+playerName);
				this.j2.users.getUser(playerName).tempSetColor(ChatColor.RED);
				this.j2.damage.protect(playerName);
				player.getInventory().setHelmet(new ItemStack(51));
				this.j2.users.addFlagLocal(playerName, Flag.GODMODE);
			}
			else if(commandName.equals("bits")){
				String name=player.getName();
				player.sendMessage(ChatColor.RED+"You fizzle out");
				this.j2.sendAdminPlusLog( ChatColor.RED+playerName+" disabled GODMODE");
				this.j2.users.getUser(name).restoreColor();
				player.getInventory().clear(39);
				if(!this.j2.safemode){
					this.j2.damage.danger(playerName);
					player.sendMessage(ChatColor.RED+"You are no longer safe");
				}
				this.j2.users.dropFlagLocal(playerName, Flag.GODMODE);
			}
		}
	}
}
