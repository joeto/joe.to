package to.joe;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class J2PlugKickBan {
	private J2Plugin j2;
	public ArrayList<j2Ban> bans;
	
	public J2PlugKickBan(J2Plugin j2p){
		j2=j2p;
		bans = new ArrayList<j2Ban>();
	}
	
	
	
	

	public void callBan(String adminName, String[] split)
	{
		List<Player> toBanCandidates = j2.getServer().matchPlayer(split[1]);
		if(toBanCandidates.size()!=1){
			if(!adminName.equalsIgnoreCase("console")){
				j2.getServer().getPlayer(adminName).sendMessage(ChatColor.RED+"Error:"+split[1]+" does not exist or fits multiple players");
			}
			return;
		}
		Player toBan=toBanCandidates.get(0);
		String banReason="";
		//long banTime=Long.parseLong(split[2]);
		//tempbans
		long banTime=0;
		if(split.length>3)
			banReason=j2.combineSplit(3, split, " ");
		if (toBan != null) {
			String name = toBan.getName();
			j2.mysql.ban(name,banReason,banTime,adminName);
			if (split.length > 2) {
				toBan.kickPlayer("Banned: " + banReason);
				
				j2.log.log(Level.INFO, "Banning " + name + " by " + adminName + ": " + banReason);
				j2.getChat().msgByFlag(Flag.ADMIN,ChatColor.RED + "Banning " + name + " by " + adminName + ": " + banReason);
				j2.getChat().msgByFlagless(Flag.ADMIN,ChatColor.RED + name + " banned (" + banReason+")");
				j2.getIRC().ircMsg(name + " banned (" + banReason+")");
			} else {
				toBan.kickPlayer("Banned.");
				j2.log.log(Level.INFO, "Banning " + name + " by " + adminName);
				j2.getChat().msgByFlag(Flag.ADMIN,ChatColor.RED + "Banning " + name + " by " + adminName);
				j2.getChat().msgByFlagless(Flag.ADMIN,ChatColor.RED + name + " banned");
				j2.getIRC().ircMsg(name + " banned");
			}
			j2.getIRC().ircMsg(name+" has left the server");
		} else {
			if(!adminName.equalsIgnoreCase("console")){
				j2.getServer().getPlayer(adminName).sendMessage(ChatColor.RED+"Error:"+split[1]+" does not exist or fits multiple players");
			}
		}
	}
	public void callAddBan(String adminName, String[] split)
	{
		String banReason="";
		//long banTime=Long.parseLong(split[2]);
		//tempbans
		long banTime=0;
		banReason=j2.combineSplit(3, split, " ");
		String name=split[1];
		j2.mysql.ban(name,banReason,banTime,adminName);
		forceKick(name,"Banned: "+banReason);
		if (split.length > 2) {
			j2.log.log(Level.INFO, "Banning " + name + " by " + adminName + ": " + banReason);
			j2.getChat().msgByFlag(Flag.ADMIN,ChatColor.RED + "Banning " + name + " by " + adminName + ": " + banReason);
		} else {
			j2.log.log(Level.INFO, "Banning " + name + " by " + adminName);
			j2.getChat().msgByFlag(Flag.ADMIN,ChatColor.RED + "Banning " + name + " by " + adminName);
		}
	}
	

	public void callKick(String pname,String admin,String reason){
		List<Player> toKickCandidates = j2.getServer().matchPlayer(pname);
		if(toKickCandidates.size()!=1 && !admin.equalsIgnoreCase("console")){
			j2.getServer().getPlayer(admin).sendMessage(ChatColor.RED+"Error:"+pname+" does not exist or fits multiple players");
			return;
		}
		Player toKick=toKickCandidates.get(0);
		if (toKick != null) {
			String name = toKick.getName();
			if (reason!="") {
				toKick.kickPlayer("Kicked: " + reason);
				j2.log.log(Level.INFO, "Kicking " + name + " by " + admin + ": " + reason);
				j2.getChat().msgByFlag(Flag.ADMIN,ChatColor.RED + "Kicking " + name + " by " + admin + ": " + reason);
				j2.getChat().msgByFlagless(Flag.ADMIN,ChatColor.RED + name + "kicked ("+reason+")");
				j2.getIRC().ircMsg(name + " kicked ("+reason+")");
			} else {
				toKick.kickPlayer("Kicked.");
				j2.log.log(Level.INFO, "Kicking " + name + " by " + admin);
				j2.getChat().msgByFlag(Flag.ADMIN,ChatColor.RED + "Kicking " + name + " by " + admin);
				j2.getChat().msgByFlagless(Flag.ADMIN,ChatColor.RED + name + "kicked");
				j2.getIRC().ircMsg(name + " kicked");
			}
			j2.getIRC().ircMsg(name+" has left the server");
		} else {
			if(!admin.equalsIgnoreCase("console"))
				j2.getServer().getPlayer(admin).sendMessage(ChatColor.RED+"Error:"+pname+" does not exist or fits multiple players");
		}
	}

	public void forceKick(String name,String reason){
		boolean msged=false;
		for (Player p : j2.getServer().getOnlinePlayers()) {
			if (p != null && p.getName().equalsIgnoreCase(name)) {
				p.kickPlayer(reason);
				if(!msged){
					if(reason!="")
						j2.getIRC().ircMsg(name+"kicked");
					else 
						j2.getIRC().ircMsg(name+"kicked ("+reason+")");
					msged=!msged;
				}
			}
		}
	}
	
	public void kickAll(String reason){
		j2.log.info("Kicking all players: "+reason);
		if(reason.equalsIgnoreCase("")){
			reason="Count to 30 and try again.";
		}
		for (Player p : j2.getServer().getOnlinePlayers()) {
			if (p != null) {
				p.kickPlayer(reason);
			}
		}
	}
	
	
}
