package to.joe.Commands.Admin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class HatCommand extends MasterCommand{

	public HatCommand(J2 j2) {
		super(j2);
	}

	@Override
	public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
		if(isPlayer && this.j2.hasFlag(player, Flag.ADMIN)){
			ItemStack meow=player.getItemInHand();
			if(meow.getAmount()>0&&meow.getTypeId()<256){
				player.getInventory().setHelmet(new ItemStack(meow.getType(),1));
				meow.setAmount(meow.getAmount()-1);
				player.sendMessage(ChatColor.RED+"You pat your new helmet");
			}
			else{
				player.sendMessage(ChatColor.RED+"You pat your head");
			}
		}
	}
}
