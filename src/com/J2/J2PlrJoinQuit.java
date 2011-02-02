package com.J2;


import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;


public class J2PlrJoinQuit extends PlayerListener {
	
	private final J2Plugin j2;

	public J2PlrJoinQuit(J2Plugin instance) {
		j2 = instance;
	}
	
	@Override
	public void onPlayerJoin(PlayerEvent event) {
		if(!j2.ircEnable)
			return;
		j2.getIRC().ircMsg(event.getPlayer().getName()+" has logged in");
		j2.getIRC().adminChannel();
	}

	@Override
	public void onPlayerQuit(PlayerEvent event) {
		j2.getIRC().ircMsg(event.getPlayer().getName()+" has left the server");
	}

	@Override
	public void onPlayerLogin(PlayerLoginEvent event) {
		String reason=j2.getKickBan().checkBans(event.getPlayer().getName());
		
		if(reason!=null){
			event.setKickMessage(reason);
			event.disallow(PlayerLoginEvent.Result.KICK_BANNED, reason);
			return;
		}
		if(j2.maintenance && !j2.getPerm().isAtOrAbove(2,event.getPlayer())){
			reason="Server offline for maintenance";
		}
		if(reason!=null){
			event.setKickMessage(reason);
			event.disallow(PlayerLoginEvent.Result.KICK_OTHER, reason);
			return;
		}
		event.allow();
	}
}
