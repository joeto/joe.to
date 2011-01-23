package com.mbaxter.bukkit.J2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
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
	
	public void ban(String name,String reason, long time, String admin){
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = j2.getConnection();
			Date curTime=new Date();
			long timeNow=curTime.getTime()/1000;
			long unBanTime;
			if(time==0)
				unBanTime=0;
			else
				unBanTime=timeNow+(60*time);

			ps = conn.prepareStatement("INSERT INTO " + j2.bansTable + " (name,reason,admin,unbantime,timeofban) VALUES (?,?,?,?,?)");
			ps.setString(1, j2.stringClean(name.toLowerCase()));
			ps.setString(2, j2.stringClean(reason));
			ps.setString(3, j2.stringClean(admin));
			ps.setLong(4, unBanTime);
			ps.setLong(5, timeNow);
			ps.executeUpdate();
			j2Ban newban=new j2Ban(name.toLowerCase(),reason,unBanTime,timeNow);
			bans.add(newban);
		} catch (SQLException ex) {

		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException ex) {
			}
		}
	}
	
	public String checkBans(String user){
		Date curTime=new Date();
		long timeNow=curTime.getTime()/1000;
		String reason=null;
		ArrayList<j2Ban> banhat=new ArrayList<j2Ban>(bans);
		for (j2Ban ban : banhat){
			if(ban.isBanned() && ban.isTemp() && ban.getTime()<timeNow){
				//unban(user);
				//tempbans
			}
			if(ban.getTimeLoaded()>timeNow-60 && ban.getName().equalsIgnoreCase(user) && ban.isBanned()){
				reason="Banned: "+ban.getReason();
			}
			if(ban.getTimeLoaded()<timeNow-60){
				bans.remove(ban);
			}
		}
		if(reason==null){
			Connection conn = null;
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				conn = j2.getConnection();
				ps = conn.prepareStatement("SELECT name,reason,unbantime FROM "+j2.bansTable+" WHERE unbanned=false and name=\""+user+"\"");
				rs = ps.executeQuery();
				while (rs.next()) {
					reason=rs.getString("reason");
					j2Ban ban=new j2Ban(rs.getString("name"),reason,rs.getLong("unbantime"),timeNow);
					bans.add(ban);
					reason="Banned: "+reason;
				}
			} catch (SQLException ex) {
				j2.log.log(Level.SEVERE, "Unable to load j2Bans. You're not going to like this.", ex);
			} finally {
				try {
					if (ps != null) {
						ps.close();
					}
					if (rs != null) {
						rs.close();
					}
					if (conn != null) {
						conn.close();
					}
				} catch (SQLException ex) {
				}
			}
		}
		return reason;
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
			ban(name,banReason,banTime,adminName);
			if (split.length > 2) {
				toBan.kickPlayer("Banned: " + banReason);
				j2.log.log(Level.INFO, "Banning " + name + " by " + adminName + ": " + banReason);
				j2.getChat().msgByLvlPlus(2,ChatColor.RED + "Banning " + name + " by " + adminName + ": " + banReason);
				j2.getChat().msgByLvlMinus(1,ChatColor.RED + name + " banned (" + banReason+")");
				j2.getIRC().ircMsg(name + " banned (" + banReason+")");
			} else {
				toBan.kickPlayer("Banned.");
				j2.log.log(Level.INFO, "Banning " + name + " by " + adminName);
				j2.getChat().msgByLvlPlus(2,ChatColor.RED + "Banning " + name + " by " + adminName);
				j2.getChat().msgByLvlMinus(1,ChatColor.RED + name + " banned");
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
		ban(name,banReason,banTime,adminName);
		forceKick(name,"Banned: "+banReason);
		if (split.length > 2) {
			j2.log.log(Level.INFO, "Banning " + name + " by " + adminName + ": " + banReason);
			j2.getChat().msgByLvlPlus(2,ChatColor.RED + "Banning " + name + " by " + adminName + ": " + banReason);
		} else {
			j2.log.log(Level.INFO, "Banning " + name + " by " + adminName);
			j2.getChat().msgByLvlPlus(2,ChatColor.RED + "Banning " + name + " by " + adminName);
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
				j2.getChat().msgByLvlPlus(2,ChatColor.RED + "Kicking " + name + " by " + admin + ": " + reason);
				j2.getChat().msgByLvlMinus(1,ChatColor.RED + name + "kicked ("+reason+")");
				j2.getIRC().ircMsg(name + " kicked ("+reason+")");
			} else {
				toKick.kickPlayer("Kicked.");
				j2.log.log(Level.INFO, "Kicking " + name + " by " + admin);
				j2.getChat().msgByLvlPlus(2,ChatColor.RED + "Kicking " + name + " by " + admin);
				j2.getChat().msgByLvlMinus(1,ChatColor.RED + name + "kicked");
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
	
	public void unban(String aname){
		Connection conn = null;
		PreparedStatement ps = null;
		String name=j2.stringClean(aname);
		try {
			conn = j2.getConnection();

			for (j2Ban ban : bans) {
				if (ban.getName().equalsIgnoreCase(name)) {
					ban.unBan();
				}
			}
			ps = conn.prepareStatement("UPDATE " + j2.bansTable + " SET unbanned=1 WHERE name=\""+ name.toLowerCase() +"\"");
			ps.executeUpdate();
		} catch (SQLException ex) {

		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException ex) {
			}
		}
	}
}
