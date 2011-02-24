package to.joe;



import org.bukkit.entity.Player;
import org.bukkit.event.player.*;


public class listenPlrJoinQuit extends PlayerListener {
	
	private final J2Plugin j2;

	public listenPlrJoinQuit(J2Plugin instance) {
		j2 = instance;
	}
	
	@Override
	public void onPlayerJoin(PlayerEvent event) {
		Player player=event.getPlayer();
		if(j2.ircEnable){
			j2.irc.ircMsg(player.getName()+" has logged in");
			j2.irc.adminChannel();
		}
		j2.warps.loadPlayer(player.getName());
	}

	@Override
	public void onPlayerKick(PlayerKickEvent event){
		
	}
	
	@Override
	public void onPlayerQuit(PlayerEvent event) {
		Player player=event.getPlayer();
		if(j2.users.getUser(player)!=null){
			j2.users.delUser(player);
			j2.warps.dropPlayer(player.getName());
			if(j2.ircEnable){
				j2.irc.ircMsg(event.getPlayer().getName()+" has left the server");
			}
		}
	}

	@Override
	public void onPlayerLogin(PlayerLoginEvent event) {
		if(j2.debug)j2.log.info("Incoming player: "+event.getPlayer().getName());
		String reason=j2.mysql.checkBans(event.getPlayer().getName());
		Player player=event.getPlayer();
		boolean isAdmin=j2.hasFlag(player, Flag.ADMIN);
		if(reason!=null){
			event.setKickMessage(reason);
			event.disallow(PlayerLoginEvent.Result.KICK_BANNED, reason);
			return;
		}
		if(j2.maintenance && !isAdmin){
			reason="Server offline for maintenance";
			event.setKickMessage(reason);
			event.disallow(PlayerLoginEvent.Result.KICK_OTHER, reason);
			return;
		}
		if(j2.users.getUser(player)!=null){
			//event.setKickMessage("Already logged in");
			//event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Already logged in");
			j2.kickbans.callKick(player.getName(), "CONSOLE", "Logged in on another Minecraft");
			return;
		}
		if(!isAdmin && !j2.hasFlag(player, Flag.DONOR) && j2.getServer().getOnlinePlayers().length >= j2.playerLimit){
			event.setKickMessage("Server Full");
			event.disallow(PlayerLoginEvent.Result.KICK_FULL, "Server full");
			return;
		}
		j2.users.addUser(event.getPlayer().getName());
		event.allow();
		if(j2.debug)j2.log.info("Player "+event.getPlayer().getName()+" allowed in");
	}
}
