package to.joe.Commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import to.joe.J2;
import to.joe.util.Flag;

public class ItemCommand extends MasterCommand {

	public ItemCommand(J2 j2) {
		super(j2);
	}

	@Override
	public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
		if(isPlayer && this.j2.hasFlag(player, Flag.FUN)){
			if (args.length < 1) {
				player.sendMessage(ChatColor.RED+"Correct usage is: /i [item](:damage) (amount)");
				return;
			}

			Player playerFor = null;
			Material material = null;

			int count = 1;
			String[] gData = null;
			Byte bytedata = null;
			if (args.length >= 1) {
				gData = args[0].split(":");
				if(gData[0].equals("0")){
					gData[0]="1";
				}
				material = Material.matchMaterial(gData[0]);
				if (gData.length == 2) {
					try{
						bytedata = Byte.valueOf(gData[1]);
					}
					catch(NumberFormatException e){
						player.sendMessage("No such damage value. Giving you damage=0");
					}
				}
			}
			if (args.length >= 2) {
				try {
					count = Integer.parseInt(args[1]);
				} catch (NumberFormatException ex) {
					player.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a number!");
					return;
				}
			}
			if (args.length == 3 && this.j2.hasFlag(playerName, Flag.ADMIN)) {
				playerFor = this.j2.getServer().getPlayer(args[2]);
				if (playerFor == null) {
					player.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a valid player!");
					return;
				}
			} else{
				playerFor=player;
			}
			if (material == null) {
				player.sendMessage(ChatColor.RED + "Unknown item");
				return ;
			}
			if(!this.j2.hasFlag(player,Flag.ADMIN)&& this.j2.isOnSummonlist(material.getId())){
				player.sendMessage(ChatColor.RED+"Can't give that to you right now");
				return;
			}
			if (bytedata != null) {
				playerFor.getInventory().addItem(new ItemStack(material, count, (short) 0, bytedata));
			} else {
				playerFor.getInventory().addItem(new ItemStack(material, count));
			}
			player.sendMessage("Given " + playerFor.getDisplayName() + " " + count + " " + material.toString());
			this.j2.log("Giving "+playerName+" "+count+" "+material.toString());
			if((this.j2.isOnWatchlist(material.getId()))&&(count>10||count<1)){
				this.j2.irc.messageAdmins("Detecting summon of "+count+" "+material.toString()+" by "+playerName);
				this.j2.sendAdminPlusLog(ChatColor.LIGHT_PURPLE+"Detecting summon of "+ChatColor.WHITE+count+" "+ChatColor.LIGHT_PURPLE+material.toString()+" by "+ChatColor.WHITE+playerName);
			}
			return;
		}
	}
}
