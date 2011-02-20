package to.joe;

import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class J2PlugChat {
	private String[] colorlist;
	private J2Plugin j2;
	public J2PlugChat(J2Plugin j2p){
		j2=j2p;
		
		//colorslist, minus lightblue white and purple
		colorlist=new String[13];
		colorlist[0]=ChatColor.BLACK.toString();
		colorlist[1]=ChatColor.BLUE.toString();
		colorlist[2]=ChatColor.DARK_PURPLE.toString();
		colorlist[3]=ChatColor.GOLD.toString();
		colorlist[4]=ChatColor.GRAY.toString();
		colorlist[5]=ChatColor.GREEN.toString();
		colorlist[6]=ChatColor.DARK_GRAY.toString();
		colorlist[7]=ChatColor.DARK_GREEN.toString();
		colorlist[8]=ChatColor.DARK_AQUA.toString();
		colorlist[9]=ChatColor.DARK_RED.toString();
		colorlist[10]=ChatColor.RED.toString();
		colorlist[11]=ChatColor.YELLOW.toString();
		colorlist[12]=ChatColor.DARK_BLUE.toString();
	}
	
	public String[] getColorlist(){
		return colorlist;
	}
	
	public void msgByFlag(Flag flag,String msg){
		for (Player plr : j2.getServer().getOnlinePlayers()) {
			if (plr != null && j2.hasFlag(plr, flag)) {
				plr.sendMessage(msg);
			}
		}
	}

	public void msgByFlagless(Flag flag,String msg){
		for (Player plr : j2.getServer().getOnlinePlayers()) {
			if (plr != null && !j2.hasFlag(plr, flag)) {
				plr.sendMessage(msg);
			}
		}
	}

	public void msgAll(String msg){
		for (Player p : j2.getServer().getOnlinePlayers()) {
			if (p != null) {
				p.sendMessage(msg);
			}
		}
	}
	public void aMsg(String name,String message){
		String msg="<§d"+name+"§f> "+message;
		msgByFlag(Flag.ADMIN,msg);
		j2.log.log(Level.INFO, "adminsChat: <"+name+"> "+message);
	}
	public void gMsg(String name,String message){
		String amessage="<"+name+"> "+message;
		String pmessage="<ADMIN> "+message;
		for (Player p : j2.getServer().getOnlinePlayers()) {
			if (p != null && j2.hasFlag(p, Flag.ADMIN)) {
				p.sendMessage(amessage);
			}
			else if (p !=null && !j2.hasFlag(p,Flag.ADMIN)) {
				p.sendMessage(pmessage);
			}
		}
		j2.log.log(Level.INFO, "GOD: <"+name+"> "+message);
		addChat(name,message);
		j2.getIRC().ircMsg(pmessage);
	}
	
	public void addChat(String name, String message) {
		/*this is a terrible, horrible idea. Never do it again.
		 * 
		 * 
		 * Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getConnection();
			Date curTime=new Date();
			ps = conn.prepareStatement("INSERT INTO " + chatTable + " (time, name, message) VALUES (?,?,?)");
			ps.setLong(1, curTime.getTime());
			ps.setString(2, name);
			ps.setString(3, message);
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
		}*/
	}
}
