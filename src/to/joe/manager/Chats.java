package to.joe.manager;



import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.util.Flag;
import to.joe.util.User;

/**
 * Manager for handling chatting
 * @author matt
 *
 */
public class Chats {
	private String[] randomColorList;
	private J2 j2;
	public boolean muteAll;
	
	
	public Chats(J2 j2p){
		j2=j2p;
		//colorslist, minus lightblue white and purple
		randomColorList=new String[11];
		randomColorList[0]=ChatColor.BLUE.toString();
		randomColorList[1]=ChatColor.DARK_PURPLE.toString();
		randomColorList[2]=ChatColor.GOLD.toString();
		randomColorList[3]=ChatColor.GRAY.toString();
		randomColorList[4]=ChatColor.GREEN.toString();
		randomColorList[5]=ChatColor.DARK_GRAY.toString();
		randomColorList[6]=ChatColor.DARK_GREEN.toString();
		randomColorList[7]=ChatColor.DARK_AQUA.toString();
		randomColorList[8]=ChatColor.DARK_RED.toString();
		randomColorList[9]=ChatColor.RED.toString();
		randomColorList[10]=ChatColor.DARK_BLUE.toString();
		this.restartManager();
	}

	/**
	 * Restart manager. Sets muteall false.
	 */
	public void restartManager(){
		this.muteAll=false;
	}

	/**
	 * List of char widths in-game. 
	 */
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

	/**
	 * @return List of acceptable colors for randomization
	 */
	public String[] getRandomColorList(){
		return randomColorList;
	}

	/**
	 * Send message only to players with named flag
	 * @param flag
	 * @param message
	 */
	public void messageByFlag(Flag flag,String message){
		for (Player plr : j2.getServer().getOnlinePlayers()) {
			if (plr != null && j2.hasFlag(plr, flag)) {
				plr.sendMessage(message);
			}
		}
	}

	/**
	 * Send message only to players WITHOUT named flag
	 * @param flag
	 * @param message
	 */
	public void messageByFlagless(Flag flag,String message){
		for (Player plr : j2.getServer().getOnlinePlayers()) {
			if (plr != null && !j2.hasFlag(plr, flag)) {
				plr.sendMessage(message);
			}
		}
	}
	
	/**
	 * Send message to all players
	 * @param message
	 */
	public void messageAll(String message){
		for (Player p : j2.getServer().getOnlinePlayers()) {
			if (p != null) {
				if(!this.j2.hasFlag(p,Flag.SHUT_OUT_WORLD)){
					p.sendMessage(message);
				}
			}
		}
	}

	/**
	 * Admin-only chat.
	 * @param name Sender
	 * @param message
	 */
	public void adminOnlyMessage(String name,String message){
		String msg="<"+ChatColor.LIGHT_PURPLE+name+ChatColor.WHITE+"> "+message;
		j2.sendAdminPlusLog(msg);
	}

	/**
	 * Message from admin to all players.
	 * Sender appears as ADMIN except to admins.
	 * @param name Sender
	 * @param message
	 */
	public void globalAdminMessage(String name,String message){
		String amessage="<"+name+"> "+ChatColor.LIGHT_PURPLE+message;
		String pmessage="<ADMIN> "+ChatColor.LIGHT_PURPLE+message;
		String imessage="<ADMIN> "+message;
		this.messageByFlagless(Flag.ADMIN, pmessage);
		this.j2.sendAdminPlusLog(amessage);
		j2.irc.messageRelay(imessage);
	}

	private String formatNamelyArea(String name,ChatColor color,boolean me){
		String colorName="";
		if(color!=null){
			colorName=color+name;
		}
		else{
			String[] colorlist=j2.chat.getRandomColorList();
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

	/**
	 * Handle a message sent.
	 * Includes anti-spam measures
	 * And bigotry detection
	 * @param player
	 * @param chat Message from the player
	 * @param me Is the message a /me message
	 */
	public void handleChat(Player player,String chat,boolean me){

		if(j2.minitrue.chat(player, chat)){
			return;
		}
		String name=player.getName();
		String chatlc=chat.toLowerCase();

		if(chatlc.contains("nigg") || chatlc.contains("fag")) {
			String msg = ChatColor.RED + "Watch " + ChatColor.LIGHT_PURPLE + name + ChatColor.RED + " for language.";
			j2.sendAdminPlusLog(msg);
			j2.irc.messageAdmins(ChatColor.stripColor(msg));
		}

		if((this.muteAll&&!j2.hasFlag(player, Flag.ADMIN)||this.j2.hasFlag(player, Flag.MUTED))){
			player.sendMessage(ChatColor.RED+"You are currently muted");
			String message=this.formatNamelyArea(name, ChatColor.YELLOW, me)+chat;
			this.messageByFlag(Flag.ADMIN, message);
			this.j2.log(message);
			return;
		}

		ChatColor color=null;
		if(!j2.randomcolor)
			color=j2.users.getUser(player).getColor();
		String message=this.formatNamelyArea(name, color, me)+chat;

		if(me)
			j2.irc.messageRelay("* "+name+" "+chat);
		else
			j2.irc.messageRelay("<"+name+"> "+chat);
		//j2.irc.chatQueue.offer("<"+name+"> "+chat);
		j2.log(message);
		messageAll(message);

	}


	/**
	 * Handles a message coming from IRC.
	 * Does not send if all players muted.
	 * @param name Sender of the message
	 * @param message
	 * @param me Is the message a /me message
	 * @param channel Channel message was sent from
	 */
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
			this.messageAll(combined);
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

	/**
	 * Takes a admin broadcast from the admin irc channel
	 * @param from Sender of the message
	 * @param message
	 */
	public void handleBroadcastFromIRC(String from,String message){
		this.j2.sendAdminPlusLog(ChatColor.AQUA+"Server-wide message from "+from);
		this.adminOnlyMessage("irc-"+from, message);
	}

	/**
	 * Handle a /msg, secretly sends to any listening admins
	 * @param from Sender
	 * @param to Receiver
	 * @param message
	 */
	public void handlePrivateMessage(Player from,Player to, String message){
		if(to.equals(from)){
			to.sendMessage(ChatColor.RED+"I think you're lonely.");
			return;
		}
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
		String nsaified=this.nsaify(complete);
		for(Player p:this.j2.getServer().getOnlinePlayers()){
			if(p!=null&&p.isOnline()&&!p.equals(from)&&!p.equals(to)&&this.j2.hasFlag(p, Flag.NSA)){
				p.sendMessage(nsaified);
			}
		}
		this.j2.log(complete);
	}
	
	public boolean isSpam(Player player,String text){
		User user=j2.users.getUser(player);
		String name=player.getName();
		int spamCount=user.spamCheck(text);
		if(spamCount>0){
			switch(spamCount){
			case 2:
				player.sendMessage(ChatColor.RED+"SPAM DETECTED. Please stop :)");
				this.j2.sendAdminPlusLog(ChatColor.LIGHT_PURPLE+"Warned "+name+" for spam. Kicking if continues.");
				this.j2.debug("User "+name+" warned for spam");
				break;
			case 3:
				this.j2.kickbans.spamKick(player);
				break;
			default:
				this.j2.debug("User "+name+" is spamming - "+spamCount);
				break;
			}
			return true;
		}
		return false;
	}

	/*private HashMap<Integer,ChatChannel> channels;
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
	}*/

	private String nsaify(String string){
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
