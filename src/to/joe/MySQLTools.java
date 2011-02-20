package to.joe;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;

import org.bukkit.ChatColor;

public class MySQLTools {
	private String user,pass,db;
	private int serverNumber;
	private J2Plugin j2;
	public MySQLTools(String User,String Pass, String DB, int ServerNumber, J2Plugin J2){
		user=User;
		pass=Pass;
		db=DB;
		serverNumber=ServerNumber;
		j2=J2;
	}
	public String user(){
		return user;
	}
	public String pass(){
		return pass;
	}
	public String db(){
		return db;
	}
	public Connection getConnection() {
		try {
			return DriverManager.getConnection(db + "?autoReconnect=true&user=" + user + "&password=" + pass);
		} catch (SQLException ex) {

		}
		return null;
	}


	public String stringClean(String toClean){
		return toClean.replace('\"', '_').replace('\'', '_').replace(';', '_');
	}

	public j2User getUser(String name){
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("SELECT * FROM j2users WHERE name\""+ name +"\"");
			rs = ps.executeQuery();
			if(rs.next()){
				String Flags=rs.getString("flags");
				ArrayList<Flag> flags=new ArrayList<Flag>();
				for(int x=0;x<Flags.length();x++){
					flags.add(Flag.byChar(Flags.charAt(x)));
				}
				return new j2User(name, ChatColor.getByCode(rs.getInt("color")), j2.users.getGroup(rs.getString("group")), flags);
			}
			else{
				ps = conn.prepareStatement("INSERT INTO j2users (`name`,`group`,`color`,`flags`) values (\""+name+"\",\"regular\",0xF,\"\"");
				ps.executeUpdate();
				return new j2User(name, ChatColor.WHITE, j2.users.getGroup("regular"), new ArrayList<Flag>());
			}
		} catch (SQLException ex) {
			j2.log.log(Level.SEVERE, "Unable to load from MySQL. Oh hell", ex);
			j2.maintenance=true;
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
		return null;
	}
	public void setFlags(String name, ArrayList<Flag> flags){
		Connection conn = null;
		PreparedStatement ps = null;
		name=stringClean(name);
		String flaglist="";
		if(!flags.isEmpty()){
			for(Flag f : flags){
				flaglist+=f.getChar();
			}
		}
		try {
			conn = getConnection();
			ps = conn.prepareStatement("UPDATE j2users SET flags=\""+ flaglist +"\" WHERE name=\""+ name +"\"");
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
	public void setGroup(String name, String group){
		Connection conn = null;
		PreparedStatement ps = null;
		name=stringClean(name);
		group=stringClean(group);
		try {
			conn = getConnection();
			ps = conn.prepareStatement("UPDATE j2users SET group=\""+ group +"\" WHERE name=\""+ name +"\"");
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
	public void ban(String name,String reason, long time, String admin){
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getConnection();
			Date curTime=new Date();
			long timeNow=curTime.getTime()/1000;
			long unBanTime;
			if(time==0)
				unBanTime=0;
			else
				unBanTime=timeNow+(60*time);

			ps = conn.prepareStatement("INSERT INTO j2bans (name,reason,admin,unbantime,timeofban) VALUES (?,?,?,?,?)");
			ps.setString(1, stringClean(name.toLowerCase()));
			ps.setString(2, stringClean(reason));
			ps.setString(3, stringClean(admin));
			ps.setLong(4, unBanTime);
			ps.setLong(5, timeNow);
			ps.executeUpdate();
			j2Ban newban=new j2Ban(name.toLowerCase(),reason,unBanTime,timeNow);
			j2.getKickBan().bans.add(newban);
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
		ArrayList<j2Ban> banhat=new ArrayList<j2Ban>(j2.getKickBan().bans);
		for (j2Ban ban : banhat){
			if(ban.isBanned() && ban.isTemp() && ban.getTime()<timeNow){
				//unban(user);
				//tempbans
			}
			if(ban.getTimeLoaded()>timeNow-60 && ban.getName().equalsIgnoreCase(user) && ban.isBanned()){
				reason="Banned: "+ban.getReason();
			}
			if(ban.getTimeLoaded()<timeNow-60){
				j2.getKickBan().bans.remove(ban);
			}
		}
		if(reason==null){
			Connection conn = null;
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				conn = j2.mysql.getConnection();
				ps = conn.prepareStatement("SELECT name,reason,unbantime FROM j2bans WHERE unbanned=0 and name=\""+user+"\"");
				rs = ps.executeQuery();
				while (rs.next()) {
					reason=rs.getString("reason");
					j2Ban ban=new j2Ban(rs.getString("name"),reason,rs.getLong("unbantime"),timeNow);
					j2.getKickBan().bans.add(ban);
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
	public void unban(String aname){
		Connection conn = null;
		PreparedStatement ps = null;
		String name=stringClean(aname);
		try {
			conn = j2.mysql.getConnection();

			for (j2Ban ban : j2.getKickBan().bans) {
				if (ban.getName().equalsIgnoreCase(name)) {
					ban.unBan();
				}
			}
			ps = conn.prepareStatement("UPDATE j2bans SET unbanned=1 WHERE name=\""+ name.toLowerCase() +"\"");
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

	public void loadMySQLData() {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<j2Group> groups;
		try {
			conn = getConnection();
			groups = new ArrayList<j2Group>();
			ps = conn.prepareStatement("SELECT * FROM j2groups_" + serverNumber);
			rs = ps.executeQuery();
			while (rs.next()) {
				String name=rs.getString("name");
				String Flags=rs.getString("flags");
				ArrayList<Flag> flags=new ArrayList<Flag>();
				for(int x=0;x<Flags.length();x++){
					flags.add(Flag.byChar(Flags.charAt(x)));
				}
				j2Group group=new j2Group(name, null);
				groups.add(group);
			}
			j2.users.setGroups(groups);
		} catch (SQLException ex) {
			j2.log.log(Level.SEVERE, "Unable to load from MySQL. Oh hell", ex);
			j2.maintenance=true;
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

}
