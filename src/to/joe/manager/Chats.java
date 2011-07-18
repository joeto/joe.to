package to.joe.manager;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.util.ChatChannel;
import to.joe.util.Flag;
import to.joe.util.User;

public class Chats {
	private String[] colorlist;
	private J2 j2;
	public boolean muteAll;
	public Chats(J2 j2p){
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
		this.restartManager();
	}
	
	public void restartManager(){
		this.muteAll=false;
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
		j2.sendAdminPlusLog(msg);
	}
	
	public void gMsg(String name,String message){
		String amessage="<"+name+"> "+ChatColor.LIGHT_PURPLE+message;
		String pmessage="<ADMIN> "+ChatColor.LIGHT_PURPLE+message;
		String imessage="<ADMIN> "+message;
		this.msgByFlagless(Flag.ADMIN, pmessage);
		this.j2.sendAdminPlusLog(amessage);
		j2.irc.ircMsg(imessage);
	}

	public String formatNamelyArea(String name,ChatColor color,boolean me){
		String colorName="";
		if(color!=null){
			colorName=color+name;
		}
		else{
			String[] colorlist=j2.chat.getColorlist();
			int size=colorlist.length;
			int rand=j2.random.nextInt(size);
			if(rand<size){
				colorName=colorlist[rand]+name;
			}
			else
			{
				for(int x=0;x<name.length();x++){
					colorName+=colorlist[j2.random.nextInt(size)]+name.charAt(x);
				}
			}
		}
		if(me){
			return "* "+colorName+" ";
		}
		else{
			return "<"+colorName+ChatColor.WHITE+"> ";
		}
	}
	
	public void handleChat(Player player,String chat,boolean me){
		
		if(j2.minitrue.chat(player, chat)){
			return;
		}
		User user=j2.users.getUser(player);
		String name=player.getName();
		String chatlc=chat.toLowerCase();
		
		int spamCount=user.isRepeat(chatlc);
		if(spamCount>0){
			switch(spamCount){
			case 3:
				player.sendMessage(ChatColor.RED+"Do you really need to repeat that message?");
				this.j2.sendAdminPlusLog(ChatColor.LIGHT_PURPLE+"Warned "+name+" for chat spam. Kicking if continues.");
				this.j2.debug("User "+name+" warned for chatspam");
				break;
			case 5:
				this.j2.sendAdminPlusLog(ChatColor.LIGHT_PURPLE+"Kicked "+name+" for spamming");
				this.j2.irc.ircAdminMsg("Kicked "+name+" for spamming. Message in next line");
				this.j2.irc.ircAdminMsg(chat);
				this.j2.debug("User "+name+" kicked for chatspam");
				break;
			default:
				this.j2.debug("User "+name+" is spamming chat - "+spamCount);
				break;
			}
			return;
		}
		
		if(chatlc.contains("nigg") || chatlc.contains("fag")) {
			String msg = ChatColor.RED + "Watch " + ChatColor.LIGHT_PURPLE + name + ChatColor.RED + " for language.";
			j2.sendAdminPlusLog(msg);
			j2.irc.ircAdminMsg(ChatColor.stripColor(msg));
		}
		
		if((this.muteAll&&!j2.hasFlag(player, Flag.ADMIN)||this.j2.hasFlag(player, Flag.MUTED))){
			player.sendMessage(ChatColor.RED+"You are currently muted");
			String message=this.formatNamelyArea(name, ChatColor.YELLOW, me)+chat;
			this.msgByFlag(Flag.ADMIN, message);
			this.j2.log(message);
			return;
		}
		if(!(user.canChat()||j2.hasFlag(player, Flag.ADMIN))){
			player.sendMessage(ChatColor.RED+"Trying to send too many messages too quickly.");
			player.sendMessage(ChatColor.RED+"Wait 5 seconds and try again");
			return;
		}
		
		ChatColor color=null;
		if(!j2.randomcolor)
			color=j2.users.getUser(player).getColor();
		String message=this.formatNamelyArea(name, color, me)+chat;
		
		if(me)
			j2.irc.ircMsg("* "+name+" "+chat);
		else
			j2.irc.ircMsg("<"+name+"> "+chat);
		//j2.irc.chatQueue.offer("<"+name+"> "+chat);
		j2.log(message);
		msgAll(message);

	}
	
	
	public void handleIRCChat(String name,String message,boolean me,String channel){
		if(this.muteAll){
			this.j2.irc.getBot().sendMessage(channel,"All players currently muted. Message will not go through.");
			return;
		}
		String combined;
		if(me){
			combined="* "+this.j2.ircUserColor+name+ChatColor.WHITE+message;
		}
		else{
			combined=this.j2.ircSeparator[0]+this.j2.ircUserColor+name+ChatColor.WHITE+this.j2.ircSeparator[1]+message;
		}
		
		if(combined.length() > this.j2.ircCharLim)
		{
			this.j2.irc.getBot().sendMessage(channel,name+": Your message was too long. The limit's " + this.j2.ircCharLim + " characters");
		}
		else
		{
			j2.log("IRC:"+combined);
			this.msgAll(combined);
			if(j2.ircEcho){
				if(me){
					this.j2.irc.getBot().sendMessage(channel,"[IRC] *"+name+message);
				}
				else{
					this.j2.irc.getBot().sendMessage(channel,"[IRC] <"+name+">"+message);
				}
			}
		}
	}
	
	public void handleBroadcastFromIRC(String from,String message){
		this.j2.sendAdminPlusLog(ChatColor.AQUA+"Server-wide message from "+from);
		this.aMsg("irc-"+from, message);
	}
	
	public void handlePMsg(Player from,Player to, String message){
		User userTo=this.j2.users.getUser(to);
		User userFrom=this.j2.users.getUser(from);
		String colorTo=userTo.getColorName();
		String colorFrom=userFrom.getColorName();
		String complete=ChatColor.WHITE+"<"+colorFrom+"->"+colorTo+"> "+message;
		if(j2.hasFlag(from,Flag.MUTED)){
			from.sendMessage(ChatColor.RED+"You are muted");
		}
		else{
			to.sendMessage(complete);
			from.sendMessage(complete);
		}
		this.msgByFlag(Flag.NSA, this.nsaify(complete));
		this.j2.log(complete);
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
	
	public String nsaify(String string){
		return string.replace(ChatColor.WHITE.toString(), ChatColor.DARK_AQUA.toString());
	}
	
	/*public void logChat(String name, String message) {
		this is a terrible, horrible idea. Never do it again.
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
		}
	}*/
}
