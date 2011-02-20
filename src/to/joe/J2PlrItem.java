
package to.joe;


import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerItemEvent;
import org.bukkit.event.player.PlayerListener;


public class J2PlrItem extends PlayerListener {
	private final J2Plugin j2;

	public J2PlrItem(J2Plugin instance) {
		j2 = instance;
	}
	@Override
	public void onPlayerItem(PlayerItemEvent event){
		Player player = event.getPlayer();
		int type=event.getMaterial().getId();

		if(j2.hasFlag(player, Flag.TRUSTED) && j2.isOnSuperBlacklist(type)){
			player.sendMessage(ChatColor.RED+"Even trusted have limits. Can't place that.");
			event.setCancelled(true);
			return;
		}
		if(!j2.hasFlag(player, Flag.TRUSTED) && (j2.isOnRegularBlacklist(type)||j2.isOnSuperBlacklist(type))){
			player.sendMessage(ChatColor.RED+"You need to be trusted or higher to place that.");
			event.setCancelled(true);
			return;
		}
	}
}
