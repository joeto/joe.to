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
		if(j2.ircEnable && j2.getServer().getOnlinePlayers().length<10){
			j2.irc.ircMsg(player.getName()+" has logged in");
			j2.irc.adminChannel();
		}
		j2.warps.loadPlayer(player.getName());
		for(String line : j2.motd){
			player.sendMessage(line);
		}
		/*if(j2.hasFlag(player,Flag.JAILED)){
			player.teleportTo(j2.users.jail);
			player.sendMessage(ChatColor.RED+"You are in "+ChatColor.DARK_RED+"JAIL");
			player.sendMessage(ChatColor.RED+"To get out, talk to the jailer");
			player.sendMessage(ChatColor.RED+"You need to punch him");
		}*/
	}

	@Override
	public void onPlayerKick(PlayerKickEvent event){
		
	}
	
	@Override
	public void onPlayerQuit(PlayerEvent event) {
		Player player=event.getPlayer();
		if(j2.users.getUser(player)!=null){
			j2.users.delUser(player.getName());
			j2.warps.dropPlayer(player.getName());
			if(j2.ircEnable && j2.getServer().getOnlinePlayers().length<10){
				j2.irc.ircMsg(event.getPlayer().getName()+" has left the server");
			}
		}
	}

	@Override
	public void onPlayerLogin(PlayerLoginEvent event) {
		if(j2.debug)j2.log.info("Incoming player: "+event.getPlayer().getName());
		String reason=j2.mysql.checkBans(event.getPlayer().getName());
		Player player=event.getPlayer();
		String name=player.getName();
		User user=j2.mysql.getUser(name);
		boolean isAdmin=(user.getUserFlags().contains(Flag.ADMIN)||j2.users.groupHasFlag(user.getGroup(), Flag.ADMIN));
		boolean isDonor=(user.getUserFlags().contains(Flag.DONOR)||j2.users.groupHasFlag(user.getGroup(), Flag.DONOR));
		if(reason!=null){
			reason="Visit http://forums.joe.to for unban";
			event.setKickMessage(reason);
			event.disallow(PlayerLoginEvent.Result.KICK_BANNED, reason);
			return;
		}
		if(j2.maintenance && !isAdmin){
			reason=j2.maintmessage;
			event.setKickMessage(reason);
			event.disallow(PlayerLoginEvent.Result.KICK_OTHER, reason);
			j2.users.delUser(name);
			return;
		}
		if(j2.users.getUser(player)!=null){
			event.setKickMessage("Already logged in");
			event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Already logged in");
			//j2.kickbans.callKick(player.getName(), "CONSOLE", "Logged in on another Minecraft");
			return;
		}
		if(!isAdmin && !isDonor && j2.getServer().getOnlinePlayers().length >= j2.playerLimit){
			event.setKickMessage("Server Full");
			event.disallow(PlayerLoginEvent.Result.KICK_FULL, "Server full");
			j2.users.delUser(name);
			return;
		}
		j2.users.addUser(name);
		event.allow();
		if(j2.debug)j2.log.info("Player "+event.getPlayer().getName()+" allowed in");
	}
}
