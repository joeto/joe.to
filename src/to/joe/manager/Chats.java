package to.joe.manager;

import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import to.joe.J2Plugin;
import to.joe.util.ChatChannel;
import to.joe.util.Flag;

public class Chats {
	private String[] colorlist;
	private J2Plugin j2;
	public Chats(J2Plugin j2p){
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
	
	public static final int[] characterWidths = new int[] {
        1, 9, 9, 8, 8, 8, 8, 7, 9, 8, 9, 9, 8, 9, 9, 9,
        8, 8, 8, 8, 9, 9, 8, 9, 8, 8, 8, 8, 8, 9, 9, 9,
        4, 2, 5, 6, 6, 6, 6, 3, 5, 5, 5, 6, 2, 6, 2, 6,
        6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 2, 2, 5, 6, 5, 6,
        7, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6, 6, 6, 6, 6, 6,
        6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6, 4, 6, 6,
        3, 6, 6, 6, 6, 6, 5, 6, 6, 2, 6, 5, 3, 6, 6, 6,
        6, 6, 6, 6, 4, 6, 6, 6, 6, 6, 6, 5, 2, 5, 7, 6,
        6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6, 3, 6, 6,
        6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6,
        6, 3, 6, 6, 6, 6, 6, 6, 6, 7, 6, 6, 6, 2, 6, 6,
        8, 9, 9, 6, 6, 6, 8, 8, 6, 8, 8, 8, 8, 8, 6, 6,
        9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9,
        9, 9, 9, 9, 9, 9, 9, 9, 9, 6, 9, 9, 9, 5, 9, 9,
        8, 7, 7, 8, 7, 8, 8, 8, 7, 8, 8, 7, 9, 9, 6, 7,
        7, 7, 7, 7, 9, 6, 7, 8, 7, 6, 6, 9, 7, 6, 7, 1
    };

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
		String msg="<"+ChatColor.LIGHT_PURPLE+name+ChatColor.WHITE+"> "+message;
		msgByFlag(Flag.ADMIN,msg);
		j2.log.log(Level.INFO, "adminsChat: <"+name+"> "+message);
	}
	public void gMsg(String name,String message){
		String amessage="<"+name+"> "+ChatColor.LIGHT_PURPLE+message;
		String pmessage="<ADMIN> "+ChatColor.LIGHT_PURPLE+message;
		String imessage="<ADMIN> "+message;
		for (Player p : j2.getServer().getOnlinePlayers()) {
			if (p != null && j2.hasFlag(p, Flag.ADMIN)) {
				p.sendMessage(amessage);
			}
			else if (p !=null && !j2.hasFlag(p,Flag.ADMIN)) {
				p.sendMessage(pmessage);
			}
		}
		j2.log.log(Level.INFO, "GOD: <"+name+"> "+message);
		logChat(name,message);
		j2.irc.ircMsg(imessage);
	}

	public void handleChat(Player player,String chat){
		String name=player.getName();
		if(!(j2.users.getUser(player).canChat()||j2.hasFlag(player, Flag.ADMIN))){
			player.sendMessage(ChatColor.RED+"Trying to send too many messages too quickly.");
			player.sendMessage(ChatColor.RED+"Wait 5 seconds and try again");
			return;
		}
		
		logChat(name, chat);
		j2.irc.ircMsg("<"+name+"> "+chat);
		//j2.irc.chatQueue.offer("<"+name+"> "+chat);
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
		//j2.mysql.chanAdd(chan);
		channels.put(chan.getID(),chan);
	}
	public void dropChannel(int id){
		//j2.mysql.chanDrop(id);
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
	
	public void logChat(String name, String message) {
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
