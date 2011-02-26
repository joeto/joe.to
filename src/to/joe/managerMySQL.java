package to.joe;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Location;

public class managerMySQL {
	private String user,pass,db;
	private int serverNumber;
	private J2Plugin j2;
	//private SimpleDateFormat formatter = new SimpleDateFormat("MM-dd hh:mm");
	public managerMySQL(String User,String Pass, String DB, int ServerNumber, J2Plugin J2){
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
	public int servnum(){
		return serverNumber;
	}
	public Connection getConnection() {
		try {
			try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException ex) {
				j2.log.severe(ex.getMessage());
			}
			return DriverManager.getConnection(db + "?autoReconnect=true&user=" + user + "&password=" + pass);
		} catch (SQLException ex) {
			j2.log.severe("Well that's no good");
			j2.log.severe(ex.getMessage());
		}
		return null;
	}


	public String stringClean(String toClean){
		return toClean.replace('\"', '_').replace('\'', '_').replace(';', '_');
	}

	public User getUser(String name){
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			String state="SELECT * FROM j2users WHERE name=\""+ name +"\"";
			if(j2.debug)j2.log.info("Query: "+state);
			ps = conn.prepareStatement(state);
			rs = ps.executeQuery();
			if(rs.next()){
				String Flags=rs.getString("flags");
				ArrayList<Flag> flags=new ArrayList<Flag>();
				for(int x=0;x<Flags.length();x++){
					flags.add(Flag.byChar(Flags.charAt(x)));
				}
				if(j2.debug)j2.log.info("User "+name+" in "+rs.getString("group")+" with "+ Flags);
				return new User(name, ChatColor.getByCode(rs.getInt("color")), rs.getString("group"), flags);
			}
			else{
				String state2="INSERT INTO j2users (`name`,`group`,`color`,`flags`) values (\""+name+"\",\"regular\",15,\"n\")";
				if(j2.debug)j2.log.info("Query: "+state2);
				ps = conn.prepareStatement(state2);
				ps.executeUpdate();
				ArrayList<Flag> f=new ArrayList<Flag>();
				f.add(Flag.NEW);
				return new User(name, ChatColor.WHITE, "regular", f);
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
			String state="UPDATE j2users SET flags=\""+ flaglist +"\" WHERE name=\""+ name +"\"";
			if(j2.debug)j2.log.info("Query: "+state);
			ps = conn.prepareStatement(state);
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
			String state="UPDATE j2users SET group=\""+ group +"\" WHERE name=\""+ name +"\"";
			if(j2.debug)j2.log.info("Query: "+state);
			ps = conn.prepareStatement(state);
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
	public void ban(String name,String reason, long time, String admin,Location location){
		Connection conn = null;
		PreparedStatement ps = null;
		double x=0,y=0,z=0;
		float pitch=0,yaw=0;
		String world="";
		if(location!=null){
			x=location.getX();
			y=location.getY();
			z=location.getZ();
			pitch=location.getPitch();
			yaw=location.getYaw();
			world=location.getWorld().getName();
		}
		try {
			conn = getConnection();
			Date curTime=new Date();
			long timeNow=curTime.getTime()/1000;
			long unBanTime;
			if(time==0)
				unBanTime=0;
			else
				unBanTime=timeNow+(60*time);
			String state="INSERT INTO j2bans (name,reason,admin,unbantime,timeofban,x,y,z,pitch,yaw,world) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
			if(j2.debug)j2.log.info("Query: "+state);
			ps = conn.prepareStatement(state);
			ps.setString(1, stringClean(name.toLowerCase()));
			ps.setString(2, stringClean(reason));
			ps.setString(3, stringClean(admin));
			ps.setLong(4, unBanTime);
			ps.setLong(5, timeNow);
			ps.setDouble(6, x);
			ps.setDouble(7,y);
			ps.setDouble(8,z);
			ps.setFloat(9,pitch);
			ps.setFloat(10, yaw);
			ps.setString(11, world);
			ps.executeUpdate();
			Ban newban=new Ban(name.toLowerCase(),reason,unBanTime,timeNow);
			j2.kickbans.bans.add(newban);
			
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
		ArrayList<Ban> banhat=new ArrayList<Ban>(j2.kickbans.bans);
		for (Ban ban : banhat){
			if(ban.isBanned() && ban.isTemp() && ban.getTime()<timeNow){
				//unban(user);
				//tempbans
			}
			if(ban.getTimeLoaded()>timeNow-60 && ban.getName().equalsIgnoreCase(user) && ban.isBanned()){
				reason="Banned: "+ban.getReason();
			}
			if(ban.getTimeLoaded()<timeNow-60){
				j2.kickbans.bans.remove(ban);
			}
		}
		if(reason==null){
			Connection conn = null;
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				conn = getConnection();
				String state="SELECT name,reason,unbantime FROM j2bans WHERE unbanned=0 and name=\""+user+"\"";
				if(j2.debug)j2.log.info("Query: "+state);
				ps = conn.prepareStatement(state);
				rs = ps.executeQuery();
				while (rs.next()) {
					reason=rs.getString("reason");
					Ban ban=new Ban(rs.getString("name"),reason,rs.getLong("unbantime"),timeNow);
					j2.kickbans.bans.add(ban);
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

			for (Ban ban : j2.kickbans.bans) {
				if (ban.getName().equalsIgnoreCase(name)) {
					ban.unBan();
				}
			}
			String state="UPDATE j2bans SET unbanned=1 WHERE name=\""+ name.toLowerCase() +"\"";
			if(j2.debug)j2.log.info("Query: "+state);
			ps = conn.prepareStatement(state);
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
		
		try {
			conn = getConnection();
			HashMap<String, ArrayList<Flag>> groups = new HashMap<String, ArrayList<Flag>>();
			String state="SELECT name,flags FROM j2groups where server=" + serverNumber;
			if(j2.debug)j2.log.info("Query: "+state);
			ps = conn.prepareStatement(state);
			rs = ps.executeQuery();
			while (rs.next()) {
				String name=rs.getString("name");
				
				String Flags=rs.getString("flags");
				if(j2.debug)j2.log.info("Group: "+name+" with flags "+Flags);
				ArrayList<Flag> flags=new ArrayList<Flag>();
				for(int x=0;x<Flags.length();x++){
					flags.add(Flag.byChar(Flags.charAt(x)));
				}
				groups.put(name, flags);
			}
			if(j2.debug)j2.log.info("Loaded "+groups.size()+ " groups");
			j2.users.setGroups(groups);
			
			//reports
			String state2="SELECT id,user,x,y,z,pitch,yaw,message,world from reports where server="+serverNumber+" and closed=0";
			ps = conn.prepareStatement(state2);
			if(j2.debug)j2.log.info("Query: "+state2);
			rs = ps.executeQuery();
			while (rs.next()){
				String user=rs.getString("user");
				Location loc=new Location(j2.getServer().getWorld(rs.getString("world")), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"), rs.getFloat("pitch"), rs.getFloat("yaw"));
				j2.reports.addReport(new Report(rs.getInt("id"), loc, user, rs.getString("message"),rs.getLong("time")));
				if(j2.debug)j2.log.info("Adding new report to list, user "+user);
				
			}
			
			//warps
			
			String state3="SELECT * FROM warps where server=" + serverNumber+" and flag!=\"w\"";
			if(j2.debug)j2.log.info("Query: "+state3);
			ps = conn.prepareStatement(state3);
			rs = ps.executeQuery();
			int count=0;
			while (rs.next()) {
				j2.warps.addWarp(new Warp(rs.getString("name"), rs.getString("player"), 
						new Location(j2.getServer().getWorld(rs.getString("world")),
								rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"),
								rs.getFloat("pitch"), rs.getFloat("yaw")),
						Flag.byChar(rs.getString("flag").charAt(0))));
				count++;
			}
			if(j2.debug)j2.log.info("Loaded "+count+ " warps");
			
			
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
	
	public void addReport(Report report){
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getConnection();
			String state="INSERT INTO reports (user,message,x,y,z,pitch,yaw,server,world,time) VALUES (?,?,?,?,?,?,?,?,?,?)";
			if(j2.debug)j2.log.info("Query: "+state);
			ps = conn.prepareStatement(state);
			ps.setString(1, stringClean(report.getUser()));
			ps.setString(2, stringClean(report.getMessage()));
			Location loc=report.getLocation();
			ps.setDouble(3, loc.getX());
			ps.setDouble(4, loc.getY());
			ps.setDouble(5, loc.getZ());
			ps.setFloat(6, loc.getPitch());
			ps.setFloat(7, loc.getYaw());
			ps.setInt(8, serverNumber);
			ps.setString(9, loc.getWorld().getName());
			long time=report.getTime();
			ps.setLong(10, time);
			ps.executeUpdate();
			ps = conn.prepareStatement("SELECT id FROM reports where time=? and message=?");
			ps.setLong(1, time);
			ps.setString(2, report.getMessage());
			ResultSet rs=ps.executeQuery();
			int id=rs.getInt("id");
			j2.reports.reportID(time, id);
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
	
	public void closeReport(int id, String admin, String reason){
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getConnection();
			String state="UPDATE reports SET closed=1,admin=?,reason where id=?";
			if(j2.debug)j2.log.info("Query: "+state);
			ps = conn.prepareStatement(state);
			ps.setString(1, admin);
			ps.setString(2,reason);
			ps.executeUpdate();
			j2.log.info("Report "+id+" closed by "+admin);
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
	
	public ArrayList<Warp> getHomes(String playername){
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<Warp> homes=new ArrayList<Warp>();
		try {
			conn = getConnection();
			String state="SELECT * FROM warps where server=? and flag=? and player=?";
			if(j2.debug)j2.log.info("Query: "+state);
			ps = conn.prepareStatement(state);
			ps.setInt(1, serverNumber);
			ps.setString(2, String.valueOf(Flag.Z_HOME_DESIGNATION.getChar()));
			ps.setString(3, playername);
			rs = ps.executeQuery();
			while (rs.next()) {
				homes.add(new Warp(rs.getString("name"), rs.getString("player"), 
						new Location(j2.getServer().getWorld(rs.getString("world")),
								rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"),
								rs.getFloat("pitch"), rs.getFloat("yaw")),
						Flag.byChar(rs.getString("flag").charAt(0))));
			}
			if(j2.debug)j2.log.info("Loaded "+homes.size()+ " warps");
			
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
		return homes;
	}

	public void removeWarp(Warp warp){
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getConnection();
			String state="DELETE FROM warps WHERE name=? and player=? and server=? and flag=?";
			if(j2.debug)j2.log.info("Query: "+state);
			ps = conn.prepareStatement(state);
			ps.setString(1, warp.getName());
			ps.setString(2, warp.getPlayer());
			ps.setInt(3, serverNumber);
			ps.setString(4, String.valueOf(warp.getFlag().getChar()));
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
	public void addWarp(Warp warp){
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getConnection();
			String state="INSERT INTO warps (name,player,server,flag,world,x,y,z,pitch,yaw) VALUES (?,?,?,?,?,?,?,?,?,?)";
			if(j2.debug)j2.log.info("Query: "+state);
			ps = conn.prepareStatement(state);
			ps.setString(1, stringClean(warp.getName()));
			ps.setString(2, stringClean(warp.getPlayer()));
			ps.setInt(3, serverNumber);
			ps.setString(4, String.valueOf(warp.getFlag().getChar()));
			Location loc=warp.getLocation();
			ps.setString(5,loc.getWorld().getName());
			ps.setDouble(6, loc.getX());
			ps.setDouble(7, loc.getY());
			ps.setDouble(8, loc.getZ());
			ps.setFloat(9, loc.getPitch());
			ps.setFloat(10, loc.getPitch());
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
