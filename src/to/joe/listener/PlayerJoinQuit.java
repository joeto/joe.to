package to.joe.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.*;
import to.joe.J2Plugin;
import to.joe.util.Flag;
import to.joe.util.User;


public class PlayerJoinQuit extends PlayerListener {
	
	private final J2Plugin j2;
	
	//private ArrayList<String> theList;

	public PlayerJoinQuit(J2Plugin instance) {
		j2 = instance;
		//theList=new ArrayList<String>();
	}
	
	@Override
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player=event.getPlayer();
		String name=player.getName();
		j2.irc.processJoin(name);
		j2.ip.processJoin(name);
		j2.warps.processJoin(name);
		j2.damage.processJoin(name);
		try{
			j2.mcbans.processJoin(name);
		}
		catch (Exception e){
			
		}
		for(String line : j2.motd){
			player.sendMessage(line);
		}
		event.setJoinMessage(null);
		j2.minitrue.announceJoin(name);
		if(j2.hasFlag(player, Flag.CUSTOM_THOR)){
			player.sendMessage(ChatColor.GOLD+"You have mystical powers");
		}
		if(j2.hasFlag(player, Flag.TOOLS)){
			player.sendMessage(ChatColor.AQUA+"You have tool usage enabled. Be careful");
		}
		if(j2.hasFlag(player,Flag.JAILED)){
			player.teleport(j2.jailloc);
			player.sendMessage(ChatColor.RED+"You are in "+ChatColor.DARK_RED+"JAIL");
			player.sendMessage(ChatColor.RED+"To get out, talk to the jailer");
			player.sendMessage(ChatColor.RED+"You need to punch him");
		}
	}

	@Override
	public void onPlayerKick(PlayerKickEvent event){
		//if(theList.contains(event.getPlayer().getName())){
			//Player player=event.getPlayer();
			//j2.mysql.userIP(player.getName(), player.getAddress());
			//theList.remove(player.getName());
		//}
		j2.damage.arf(event.getPlayer().getName());
		event.setLeaveMessage(null);
	}
	
	@Override
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player=event.getPlayer();
		String name=player.getName();
		j2.minitrue.announceLeave(name);
		if(j2.users.getUser(player)!=null){
			j2.users.delUser(player.getName());
			j2.warps.dropPlayer(player.getName());
			j2.irc.processLeave(player.getName());
		}
		event.setQuitMessage(null);
		j2.damage.arf(name);
	}

	@Override
	public void onPlayerPreLogin(PlayerPreLoginEvent event) {
		String name=event.getName();
		String ip=event.getAddress().getHostAddress();
		//System.out.println("IP: \""+ip+"\"");
		if(j2.debug)j2.log.info("Incoming player: "+name+" on "+ip);
		String reason=j2.mysql.checkBans(name);
		//j2.mysql.userIP(name,player.getAddress().getHostName());
		//if(event.getResult().equals(Result.ALLOWED)){
			j2.ip.incoming(name,ip);
		//}
		if(j2.debug)System.out.println("IP: "+event.getKickMessage());
		User user=j2.mysql.getUser(name);
		boolean isAdmin=(user.getUserFlags().contains(Flag.ADMIN)||j2.users.groupHasFlag(user.getGroup(), Flag.ADMIN));
		boolean isDonor=(user.getUserFlags().contains(Flag.DONOR)||j2.users.groupHasFlag(user.getGroup(), Flag.DONOR));
		boolean isTrusted=(user.getUserFlags().contains(Flag.TRUSTED)||j2.users.groupHasFlag(user.getGroup(), Flag.TRUSTED));
		boolean incoming=true;
		if(reason!=null){
			reason="Visit http://forums.joe.to for unban";
			event.setKickMessage(reason);
			event.disallow(PlayerPreLoginEvent.Result.KICK_BANNED, reason);
			incoming=false;
		}
		if(j2.maintenance && !isAdmin){
			reason=j2.maintmessage;
			event.setKickMessage(reason);
			event.disallow(PlayerPreLoginEvent.Result.KICK_OTHER, reason);
			//j2.users.delUser(name);
			incoming=false;
		}
		if(j2.trustedonly && !isTrusted){
			reason="Trusted only. http://forums.joe.to";
			event.setKickMessage(reason);
			event.disallow(PlayerPreLoginEvent.Result.KICK_OTHER, reason);
			incoming=false;
		}
		if(j2.users.getUser(name)!=null){
			event.setKickMessage("Already logged in. If not, wait a minute and try again.");
			event.disallow(PlayerPreLoginEvent.Result.KICK_OTHER, "Already logged in.If not, wait a minute and try again.");
			//j2.kickbans.callKick(player.getName(), "CONSOLE", "Logged in on another Minecraft");
			incoming=false;
		}
		if(!isAdmin && !isDonor && j2.getServer().getOnlinePlayers().length >= j2.playerLimit){
			event.setKickMessage("Server Full");
			event.disallow(PlayerPreLoginEvent.Result.KICK_FULL, "Server full");
			//j2.users.delUser(name);
			incoming=false;
		}
		if(!incoming){
			return;
		}
		j2.users.addUser(name);
		event.allow();
		if(j2.debug)j2.log.info("Player "+name+" allowed in");
	}
}
