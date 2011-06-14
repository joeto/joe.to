package to.joe.manager;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.util.Ban;
import to.joe.util.Flag;

public class KicksBans {
	private J2 j2;
	public ArrayList<Ban> bans;
	public ArrayList<String> xrayers;

	public KicksBans(J2 j2p){
		this.j2=j2p;
		this.restartManager();
	}
	
	public void restartManager(){
		this.bans = new ArrayList<Ban>();
		this.xrayers=new ArrayList<String>();
	}

	public void callBan(String adminName, String[] split, Location location)
	{
		List<Player> toBanCandidates = j2.getServer().matchPlayer(split[0]);
		if(toBanCandidates.size()!=1){
			if(!adminName.equalsIgnoreCase("console")){
				j2.getServer().getPlayer(adminName).sendMessage(ChatColor.RED+"Error:"+split[0]+" does not exist or fits multiple players");
			}
			return;
		}
		Player toBan=toBanCandidates.get(0);
		String banReason="";
		long banTime=0;
		banReason=j2.combineSplit(1, split, " ");
		if (toBan != null) {
			String name = toBan.getName();
			toBan.getWorld().strikeLightningEffect(toBan.getLocation());
			j2.mysql.ban(name,banReason,banTime,adminName,location);
			//if (split.length > 1) {
			toBan.kickPlayer("Banned: " + banReason);
			j2.sendAdminPlusLog(ChatColor.RED + "Banning " + name + " by " + adminName + ": " + banReason);
			j2.chat.msgByFlagless(Flag.ADMIN,ChatColor.RED + name + " banned (" + banReason+")");
			j2.irc.ircMsg(name + " banned (" + banReason+")");
			/*} else {
				toBan.kickPlayer("Banned.");
				j2.log.log(Level.INFO, "Banning " + name + " by " + adminName);
				j2.chat.msgByFlag(Flag.ADMIN,ChatColor.RED + "Banning " + name + " by " + adminName);
				j2.chat.msgByFlagless(Flag.ADMIN,ChatColor.RED + name + " banned");
				j2.irc.ircMsg(name + " banned");
			}*/
		} else {
			if(!adminName.equalsIgnoreCase("console")){
				j2.getServer().getPlayer(adminName).sendMessage(ChatColor.RED+"Error:"+split[0]+" does not exist or fits multiple players");
			}
		}
	}
	public void callAddBan(String adminName, String[] split,Location location)
	{
		String banReason="";
		long banTime=0;
		banReason=j2.combineSplit(1, split, " ");
		String name=split[0];
		j2.mysql.ban(name,banReason,banTime,adminName,location);
		forceKick(name,"Banned: "+banReason);
		//if (split.length > 1) {
		j2.sendAdminPlusLog(ChatColor.RED + "Banning " + name + " by " + adminName + ": " + banReason);
		/*} else {
			j2.log.log(Level.INFO, "Banning " + name + " by " + adminName);
			j2.chat.msgByFlag(Flag.ADMIN,ChatColor.RED + "Banning " + name + " by " + adminName);
		}*/
	}

	public void callKick(String pname,String admin,String reason){
		this.callKick(pname, admin, reason, false);
	}

	public void callKick(String pname,String admin,String reason, boolean quiet){
		List<Player> toKickCandidates = j2.getServer().matchPlayer(pname);
		if(toKickCandidates.size()!=1 ){
			if(!admin.equalsIgnoreCase("console")&&!quiet){
				j2.getServer().getPlayer(admin).sendMessage(ChatColor.RED+"Error:"+pname+" does not exist or fits multiple players");
			}
			return;
		}
		Player toKick=toKickCandidates.get(0);
		if (toKick != null) {
			toKick.getWorld().strikeLightningEffect(toKick.getLocation());
			String name = toKick.getName();
			if (reason!="") {
				toKick.kickPlayer("Kicked: " + reason);
				j2.sendAdminPlusLog(ChatColor.RED + "Kicking " + name + " by " + admin + ": " + reason);
				j2.chat.msgByFlagless(Flag.ADMIN,ChatColor.RED + name + " kicked ("+reason+")");
				j2.irc.ircMsg(name + " kicked ("+reason+")");
			} else {
				toKick.kickPlayer("Kicked.");
				j2.sendAdminPlusLog(ChatColor.RED + "Kicking " + name + " by " + admin);
				j2.chat.msgByFlagless(Flag.ADMIN,ChatColor.RED + name + " kicked");
				j2.irc.ircMsg(name + " kicked");
			}
		} else {
			if(!admin.equalsIgnoreCase("console")&&!quiet)
				j2.getServer().getPlayer(admin).sendMessage(ChatColor.RED+"Error:"+pname+" does not exist or fits multiple players");
		}
	}

	public void forceKick(String name,String reason){
		boolean msged=false;
		for (Player p : j2.getServer().getOnlinePlayers()) {
			if (p != null && p.getName().equalsIgnoreCase(name)) {
				p.getWorld().strikeLightningEffect(p.getLocation());
				p.kickPlayer(reason);
				if(!msged){
					if(reason!="")
						j2.irc.ircMsg(name+" kicked");
					else 
						j2.irc.ircMsg(name+" kicked ("+reason+")");
					j2.sendAdminPlusLog(ChatColor.RED+"Knocked "+name+" out of the server");
					msged=!msged;
				}
			}
		}
	}

	public void kickAll(String reason){
		j2.log(ChatColor.RED+"Kicking all players: "+reason);
		if(reason.equalsIgnoreCase("")){
			reason="Count to 30 and try again.";
		}
		for (Player p : j2.getServer().getOnlinePlayers()) {
			if (p != null) {
				p.kickPlayer(reason);
			}
		}
	}

	public void unban(String adminName,String name){
		j2.mysql.unban(name);
		j2.sendAdminPlusLog(ChatColor.RED + "Unbanning " + name + " by " + adminName);
	}
	
	public synchronized void ixrai(String name,String commandName){
		if(this.xrayers.contains(name)){
			this.callBan("BobTheVigilant", (name+" xray hacking").split(" "), new Location(j2.getServer().getWorld("world"),0,0,0));
			j2.log(ChatColor.AQUA+"[BOB] Detected /"+commandName+" from "+name+" and bant");
			this.xrayers.remove(name);
		}
		else{
			this.xrayers.add(name);
			this.callKick(name, "BobTheVigilant", "Remove your hacks, then rejoin :)");
			j2.log(ChatColor.AQUA+"[BOB] Detected /"+commandName+" from "+name+" and kicked");
		}
	}
}
