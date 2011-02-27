
package to.joe;


import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerItemEvent;
import org.bukkit.event.player.PlayerListener;


public class listenPlrItem extends PlayerListener {
	private final J2Plugin j2;

	public listenPlrItem(J2Plugin instance) {
		j2 = instance;
	}
	@Override
	public void onPlayerItem(PlayerItemEvent event){
		Player player = event.getPlayer();
		int type=event.getMaterial().getId();
		if(!j2.hasFlag(player, Flag.MODWORLD)){
			player.sendMessage("You don't have permission to do that");
			event.setCancelled(true);
			return;
		}
		if(j2.hasFlag(player, Flag.TRUSTED) && !j2.hasFlag(player,Flag.ADMIN) && j2.isOnSuperBlacklist(type)){
			player.sendMessage(ChatColor.RED+"Even trusted have limits. Can't place that.");
			event.setCancelled(true);
			return;
		}
		if(!j2.hasFlag(player, Flag.TRUSTED) && (j2.isOnRegularBlacklist(type)||j2.isOnSuperBlacklist(type))){
			player.sendMessage(ChatColor.RED+"You need to be trusted or higher to place that.");
			event.setCancelled(true);
			return;
		}
		BlockRow changed;
		Block smacked = event.getBlockClicked().getFace(event.getBlockFace());
		changed = new BlockRow(player.getDisplayName(),smacked.getTypeId(),0,smacked.getX(),smacked.getY(),smacked.getZ(),(System.currentTimeMillis()/1000L));
		managerBlockLog.bqueue.offer(changed);
	}
}
