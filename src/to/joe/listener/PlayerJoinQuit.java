package to.joe.listener;

/*import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import com.sk89q.jinglenote.MidiJingleSequencer;
*/


import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.player.*;

import to.joe.J2;
import to.joe.util.Flag;
import to.joe.util.User;


public class PlayerJoinQuit extends PlayerListener {
	
	private final J2 j2;
	
	//private ArrayList<String> theList;

	public PlayerJoinQuit(J2 instance) {
		j2 = instance;
		//theList=new ArrayList<String>();
		
	}
	
	@Override
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);
		Player player=event.getPlayer();
		this.j2.users.processJoin(player);
		
		
		/*if(j2.hasFlag(player,Flag.JAILED)){
			player.teleportTo(j2.users.jail);
			player.sendMessage(ChatColor.RED+"You are in "+ChatColor.DARK_RED+"JAIL");
			player.sendMessage(ChatColor.RED+"To get out, talk to the jailer");
			player.sendMessage(ChatColor.RED+"You need to punch him");
		}*/
		this.j2.minitrue.vanish.updateInvisible(player);
	}
	ArrayList<String> kicked=new ArrayList<String>();
	
	@Override
	public void onPlayerKick(PlayerKickEvent event){
		//if(theList.contains(event.getPlayer().getName())){
			//Player player=event.getPlayer();
			//j2.mysql.userIP(player.getName(), player.getAddress());
			//theList.remove(player.getName());
		//}
		String name=event.getPlayer().getName();
		kicked.add(name);
		j2.damage.arf(event.getPlayer().getName());
		event.setLeaveMessage(null);
	}
	
	@Override
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player=event.getPlayer();
		String name=player.getName();
		
		if(!kicked.contains(name)){
			j2.minitrue.processLeave(player);
		}
		else{
			kicked.remove(name);
		}
		if(j2.users.getUser(player)!=null){
			j2.users.delUser(name);
			j2.warps.dropPlayer(name);
			j2.irc.processLeave(name);
		}
		event.setQuitMessage(null);
		j2.damage.arf(name);
		j2.users.playerReset(name);
		this.j2.minitrue.vanish.invisible.remove(player);
		this.j2.banCoop.disconnect(name);
	}

	@Override
	public void onPlayerPreLogin(PlayerPreLoginEvent event) {
		String name=event.getName();
		String ip=event.getAddress().getHostAddress();
		//System.out.println("IP: \""+ip+"\"");
		j2.debug("Incoming player: "+name+" on "+ip);
		String reason=null;
		try{
			reason=j2.mysql.checkBans(name);
		}
		catch (Exception e){
			reason="Try again. Ban system didn't like you.";
		}
		//j2.mysql.userIP(name,player.getAddress().getHostName());
		//if(event.getResult().equals(Result.ALLOWED)){
			j2.ip.incoming(name,ip);
		//}
		User user=j2.mysql.getUser(name);
		boolean isAdmin=(user.getUserFlags().contains(Flag.ADMIN)||j2.users.groupHasFlag(user.getGroup(), Flag.ADMIN));
		boolean isDonor=(user.getUserFlags().contains(Flag.DONOR)||j2.users.groupHasFlag(user.getGroup(), Flag.DONOR));
		boolean isContributor=(user.getUserFlags().contains(Flag.CONTRIBUTOR)||j2.users.groupHasFlag(user.getGroup(), Flag.CONTRIBUTOR));
		boolean isTrusted=(user.getUserFlags().contains(Flag.TRUSTED)||j2.users.groupHasFlag(user.getGroup(), Flag.TRUSTED));
		boolean isPrivBlocked=user.getUserFlags().contains(Flag.NEVER_AGAIN);
		boolean incoming=true;
		if(reason!=null){
			if(!reason.equals("Try again. Ban system hiccup."))
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
		if(j2.trustedonly && (!isTrusted || isPrivBlocked) ){
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
		if(!isAdmin && !isDonor &&!isContributor && j2.getServer().getOnlinePlayers().length >= j2.playerLimit){
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
		j2.debug("Player "+name+" allowed in");
	}
}
