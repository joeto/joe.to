package to.joe.listener;

import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerTeleportEvent;

import to.joe.J2Plugin;

public class PlayerTeleport  extends PlayerListener {
	private J2Plugin j2;
	public PlayerTeleport(J2Plugin j2){
		this.j2=j2;
	}
	
	@Override
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if(j2.damage.allWolf.containsKey(event.getPlayer().getName())){
			event.setCancelled(true);
		}
	}
}
