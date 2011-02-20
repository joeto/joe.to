package to.joe;



import org.bukkit.entity.Player;
import org.bukkit.event.player.*;


public class J2PlrJoinQuit extends PlayerListener {
	
	private final J2Plugin j2;

	public J2PlrJoinQuit(J2Plugin instance) {
		j2 = instance;
	}
	
	@Override
	public void onPlayerJoin(PlayerEvent event) {
		if(j2.ircEnable){
			j2.getIRC().ircMsg(event.getPlayer().getName()+" has logged in");
			j2.getIRC().adminChannel();
		}
	}

	@Override
	public void onPlayerKick(PlayerKickEvent event){
		j2.users.delUser(event.getPlayer());
		if(j2.ircEnable){
			j2.getIRC().ircMsg(event.getPlayer().getName()+" has logged in");
			j2.getIRC().adminChannel();
		}
	}
	
	@Override
	public void onPlayerQuit(PlayerEvent event) {
		if(j2.ircEnable){
			j2.getIRC().ircMsg(event.getPlayer().getName()+" has left the server");
		}
	}

	@Override
	public void onPlayerLogin(PlayerLoginEvent event) {
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
		if(j2.users.getOnlineUser(player)!=null){
			event.setKickMessage("Already logged in");
			event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Already logged in");
			return;
		}
		if(!isAdmin && !j2.hasFlag(player, Flag.DONOR) && j2.getServer().getOnlinePlayers().length >= j2.playerLimit){
			event.setKickMessage("Server Full");
			event.disallow(PlayerLoginEvent.Result.KICK_FULL, "Server full");
			return;
		}
		j2.users.addUser(event.getPlayer().getName());
		event.allow();
	}
}
