
package to.joe;


import org.bukkit.ChatColor;
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
			player.sendMessage(ChatColor.RED+"Even trusted have limits. Can't do that.");
			event.setCancelled(true);
			return;
		}
		if(!j2.hasFlag(player, Flag.TRUSTED) && (j2.isOnRegularBlacklist(type)||j2.isOnSuperBlacklist(type))){
			player.sendMessage(ChatColor.RED+"You need to be trusted or higher to do that.");
			event.setCancelled(true);
			return;
		}
		/*if(type==259||type==291||type==292||type==293||type==294||type==295||type==321||type==324||type==325
				||type==326||type==327||type==330||type==331||type==354||type==355||type==356){
			BlockRow changed;
			Block clicked = event.getBlockClicked();
			BlockFace face=event.getBlockFace();
			int x=clicked.getX()+face.getModX();
			int y=clicked.getY()+face.getModY();
			int z=clicked.getZ()+face.getModZ();
			changed = new BlockRow(player.getDisplayName(),0,type,x,y,z,(System.currentTimeMillis()/1000L),null);
			managerBlockLog.bqueue.offer(changed);
		}*/
	}
}
