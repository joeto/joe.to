package to.joe;

import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class managerChat {
	private String[] colorlist;
	private J2Plugin j2;
	public managerChat(J2Plugin j2p){
		j2=j2p;

		//colorslist, minus lightblue white and purple
		colorlist=new String[11];
		colorlist[0]=ChatColor.BLUE.toString();
		colorlist[1]=ChatColor.DARK_PURPLE.toString();
		colorlist[2]=ChatColor.GOLD.toString();
		colorlist[3]=ChatColor.GRAY.toString();
		colorlist[4]=ChatColor.GREEN.toString();
		colorlist[5]=ChatColor.DARK_GRAY.toString();
		colorlist[6]=ChatColor.DARK_GREEN.toString();
		colorlist[7]=ChatColor.DARK_AQUA.toString();
		colorlist[8]=ChatColor.DARK_RED.toString();
		colorlist[9]=ChatColor.RED.toString();
		colorlist[10]=ChatColor.DARK_BLUE.toString();
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
		j2.irc.ircMsg(pmessage);
	}

	public void handleChat(Player player,String chat){
		String name=player.getName();

		addChat(name, chat);
		//j2.irc.ircMsg("<"+name+"> "+chat);
		j2.irc.chatQueue.offer("<"+name+"> "+chat);
		j2.log.info("<"+name+"> "+chat);

		/* method to madness here.
		 * This is going to be written to handle channels.
		 */
		String message="";
		if(!j2.randomcolor){
			message=ChatColor.WHITE+"<"+j2.users.getUser(player).getColorName()+ChatColor.WHITE+"> "+chat;			
		}
		else {

			String[] colorlist=j2.chat.getColorlist();
			int size=colorlist.length;
			int rand=j2.random.nextInt(size);
			if(rand<size){
				message=ChatColor.WHITE+"<"+colorlist[rand]+name+ChatColor.WHITE+"> "+chat;
			}
			else
			{
				for(int x=0;x<name.length();x++){
					name+=colorlist[j2.random.nextInt(size)]+name.charAt(x);
				}
				message=ChatColor.WHITE+"<"+name+ChatColor.WHITE+"> "+chat;
			}
		}
		msgAll(message);

		/*if(player.getName().equalsIgnoreCase("mbaxter")){
		String[] colorlist=j2.chat.getColorlist();
		String dname="";
		int size=colorlist.length;
		for(int x=0;x<7;x++){
			dname+=colorlist[j2.random.nextInt(size)]+name.charAt(x);
		}
		j2.chat.msgAll(ChatColor.WHITE+"<"+dname+ChatColor.WHITE+"> "+message);
		}*/
	}

	private HashMap<Integer,ChatChannel> channels;
	public void addChannel(ChatChannel chan){
		j2.mysql.chanAdd(chan);
		channels.put(chan.getID(),chan);
	}
	public void dropChannel(int id){
		j2.mysql.chanDrop(id);
		if(channels.containsKey(id)){
			channels.remove(id);
		}
	}
	public ChatChannel getChannel(String name){
		return channels.get(name);
	}
	public void loadChannel(ChatChannel chan){
		channels.put(chan.getID(), chan);
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
