/*
 * The epic j2Plugin
 * A bunch of fun features, put together for joe.to
 * DO NOT DISTRIBUTE!
 * 
 * Credits:
 * mbaxter
 * 
 * Thanks to;
 * bootswithdefer for Tips code
 * Nijikokun for properties management and the item command
 * 
 */

package to.joe;

import java.io.File;
import java.util.HashMap;
import org.bukkit.entity.*;
import org.bukkit.Location;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.ChatColor;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * J2 Plugin, on Bukkit
 *
 * @author mbaxter
 */
public class J2Plugin extends JavaPlugin {
	private final listenPlrChat plrlisChat = new listenPlrChat(this);
	private final listenPlrCommands plrlisCommands = new listenPlrCommands(this);
	private final listenPlrItem plrlisItem = new listenPlrItem(this);
	private final listenPlrJoinQuit plrlisJoinQuit = new listenPlrJoinQuit(this);
	private final listenBlock blockListener = new listenBlock(this);
	private final listenEntity entityListener = new listenEntity(this);
	private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
	public final managerChat chat = new managerChat(this);
	public final managerIRC irc = new managerIRC(this);
	public final managerKickBan kickbans = new managerKickBan(this);
	public final managerUsers users = new managerUsers(this);
	public final managerReport reports = new managerReport(this);
	public final managerWarps warps = new managerWarps(this);
	public managerBlockLog blogger;
	public managerMySQL mysql;

	
	public void onDisable() {

		irc.kill();
		stopTimer();
		// NOTE: All registered events are automatically unregistered when a plugin is disabled

	}

	public void onEnable() {
		log=Logger.getLogger("Minecraft");
		protectedUsers=new ArrayList<String>();
		loadData();
		if(debug)log.info("Data loaded");
		
		//irc start
		if(ircEnable)irc.prepIRC();
		if(debug)log.info("IRC up (or disabled)");
		//irc end
		loadTips();
		if(debug)log.info("Tips loaded");
		startTipsTimer();
		if(debug)log.info("Tips timer started");
		
		//Initialize BlockLogger
		this.blogger = new managerBlockLog(this.mysql.getConnection(),this.mysql.servnum());
		if(debug)log.info("Blogger init");
		new Thread(blogger).start();
		if(debug)log.info("Blogger is go");
		// Register our events
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_COMMAND, plrlisCommands, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_CHAT, plrlisChat, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, plrlisJoinQuit, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_ITEM, plrlisItem, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_CANBUILD, blockListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_DAMAGED, blockListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_PLACED, blockListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_IGNITE, blockListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_RIGHTCLICKED, blockListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.SIGN_CHANGE, blockListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_EXPLODE, entityListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGED, entityListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_LOGIN, plrlisJoinQuit, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, plrlisJoinQuit, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, plrlisJoinQuit, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_KICK, plrlisJoinQuit, Priority.Normal, this);
		if(debug)log.info("Events registered");
		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
	}

	public boolean isDebugging(final Player player) {
		if (debugees.containsKey(player)) {
			return debugees.get(player);
		} else {
			return false;
		}
	}

	public void setDebugging(final Player player, final boolean value) {
		debugees.put(player, value);
	}

	public void loadData(){
		
		
		rules=readDaFile("rules.txt");
		blacklist=readDaFile("blacklistinfo.txt");
		intro=readDaFile("intro.txt");		
		PropFile j2properties = new PropFile("j2.properties");
		try { 
			debug = j2properties.getBoolean("debug",false);
			//mysql start
			String mysql_username = j2properties.getString("user", "root");
			String mysql_password = j2properties.getString("pass", "root");
			String mysql_db = j2properties.getString("db", "jdbc:mysql://localhost:3306/minecraft");
			//chatTable = properties.getString("chat","chat");
			int mysql_server = j2properties.getInt("server-number", 1);
			mysql = new managerMySQL(mysql_username,mysql_password,mysql_db, mysql_server, this);
			mysql.loadMySQLData();
			//mysql end
			
			playerLimit=j2properties.getInt("max-players",20);
			tips_delay = j2properties.getInt("tip-delay", 120);
			tips_prefix = j2properties.getString("tip-prefix","");
			tips_color = "\u00A7"+j2properties.getString("tip-color", "b");
			if (tips_prefix == null || tips_prefix.length() == 0)
				tips_prefix = "";
			else if (tips_prefix.charAt(tips_prefix.length()-1) != ' ')
				tips_prefix += " ";
			ircHost = j2properties.getString("irc-host","localhost");
			ircName = j2properties.getString("irc-name","aMinecraftBot");
			ircChannel = j2properties.getString("irc-channel","#minecraftbot");
			ircAdminChannel = j2properties.getString("irc-adminchannel","#minecraftbotadmin");
			ircUserColor = j2properties.getString("irc-usercolor","f");
			ircSeparator= j2properties.getString("irc-separator","<,>").split(",");
			ircCharLim = j2properties.getInt("irc-charlimit",390);
			ircMsg=j2properties.getBoolean("irc-msg-enable",false);
			ircEnable=j2properties.getBoolean("irc-enable",false);
			ircEcho = j2properties.getBoolean("irc-echo",false);
			ircPort = j2properties.getInt("irc-port",6667);
			ircDebug = j2properties.getBoolean("irc-debug",false);
			ircOnJoin = j2properties.getString("irc-onjoin","");
			gsAuth = j2properties.getString("gs-auth","");
			gsPass = j2properties.getString("gs-pass","");
			ircLevel2 = j2properties.getString("irc-level2","").split(",");
			safemode=j2properties.getBoolean("safemode",false);
			maintenance = j2properties.getBoolean("maintenance",false);
			fun=j2properties.getBoolean("funmode",false);
			randomcolor=j2properties.getBoolean("randcolor",false);
			String superBlacklist = j2properties.getString("superblacklist", "0");				
			String regBlacklist = j2properties.getString("regblacklist", "0");
			superblacklist=new ArrayList<Integer>();
			itemblacklist=new ArrayList<Integer>();
			for(String s:superBlacklist.split(",")){
				if(s!=null){
					superblacklist.add(Integer.valueOf(s));
				}
			}
			for(String s:regBlacklist.split(",")){
				if(s!=null){
					itemblacklist.add(Integer.valueOf(s));
				}
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "Exception while reading from j2.properties", e);
		}
	}

	

	public String[] readDaFile(String filename)
	{
			
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(filename);
		} catch (FileNotFoundException e2) {
			//e2.printStackTrace();
			log.severe("File not found: "+filename);
			String[] uhOh=new String[1];
			uhOh[0]="";
			return uhOh;
		}
		BufferedReader rulesBuffer = new BufferedReader(fileReader);
		List<String> fileLines = new ArrayList<String>();
		String line = null;
		try {
			while ((line = rulesBuffer.readLine()) != null) {
				fileLines.add(line);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			rulesBuffer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileLines.toArray(new String[fileLines.size()]);
	}

	

	

	//tips
	private void startTipsTimer() {
		tips_stopTimer = false;
		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (tips_stopTimer) {
					timer.cancel();
					return;
				}
				broadcastTip();
			}
		}, 3000, tips_delay*1000);
	}


	private void stopTimer() {
		tips_stopTimer = true;
	}

	public void broadcastTip()
	{
		broadcastTip(currentTip);
		if (currentTip >= tips.size())
			currentTip = 0;
		else
			currentTip++;
	}

	public void broadcastTip(int tipnum)
	{
		if (tips.isEmpty())
			return;
		if (tipnum < 0 || tipnum >= tips.size())
			return;
		String message = tips_color + tips_prefix + tips.get(tipnum);
		for (Player p : this.getServer().getOnlinePlayers()) {
			if (p != null) {
				p.sendMessage(message);
			}
		}
	}

	public void loadTips() {
		tips = new ArrayList<String>();
		if (!new File(tips_location).exists()) {

			return;
		}
		try {
			Scanner scanner = new Scanner(new File(tips_location));
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (line.startsWith("#") || line.equals(""))
					continue;
				tips.add(line);
			}
			scanner.close();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Exception while reading " + tips_location, e);
			stopTimer();
		}
	}
	
	//end tips

	public boolean isOnSuperBlacklist(int id) {
		return superblacklist.contains(Integer.valueOf(id));
	}
	public boolean isOnRegularBlacklist(int id) {
		return itemblacklist.contains(Integer.valueOf(id));
	}

	public void travelLog(String name,int distance){

	}

	/*public Block locationCheck(Player player,Block block,boolean placed){
		int x,z;

		if(block==null && !placed){
			Location l=player.getLocation();
			x=(int)l.x;
			z=(int)l.z;
			int minX=natureXmin-10;
			int maxX=natureXmax+10;
			int minZ=natureZmin-10;
			int maxZ=natureZmax+10;
			boolean pancakes=false;
			if( (x==minX || x==maxX) && z>minZ && z<maxZ){
				pancakes=true;
			}
			if( (z==minZ || z==maxZ) && x>minX && x<maxX){
				pancakes=true;
			}
			if(pancakes)
			{
				player.sendMessage(Colors.LightBlue+"IMPORTANT MESSAGE: "+Colors.LightGreen+"Nature");
				player.sendMessage(Colors.LightGreen+"You are 10 blocks from the nature conservatory");
				player.sendMessage(Colors.LightGreen+"DO NOT MODIFY, DO NOT BUILD. ONLY OBSERVE.");
				player.sendMessage(Colors.LightGreen+"Harsh punishments for damaging nature");
				player.sendMessage(Colors.LightGreen+"- Bob the Naturalist");
				//player.sendMessage(x+" "+z+" "+natureXmin+" "+natureXmax+" "+natureZmin+" "+natureZmax);
			}
		}
		else{
			x=block.getX();
			z=block.getZ();
			if(x>(natureXmin) && x<(natureXmax) && z>(natureZmin) && z<(natureZmax))
			{
				int type=19;
				if(!placed)
					type=block.getType();
				Block james=new Block(type,x,block.getY(),z);
				if(isJ2Admin(player)){
					player.sendMessage(Colors.LightBlue+"IMPORTANT MESSAGE: "+Colors.LightGreen+"Nature");
					player.sendMessage(Colors.LightGreen+"You just touched the conservatory");
					player.sendMessage(Colors.LightGreen+"Please undo what you changed");
					player.sendMessage(Colors.LightGreen+"- Bob the Naturalist");
					return null;
				}
				return james;
			}
		}
		return null;
	}*/

	public void tpToCoord(Player player, double x, double y, double z, float rotation, float pitch){
		player.teleportTo(new Location(this.getServer().getWorlds().get(0), x, y, z, rotation, pitch));
	}

	public String combineSplit(int startIndex, String[] string, String seperator) {
		StringBuilder builder = new StringBuilder();
		for (int i = startIndex; i < string.length; i++) {
			builder.append(string[i]);
			builder.append(seperator);
		}
		builder.deleteCharAt(builder.length() - seperator.length()); 
		return builder.toString();
	}

	
	
	public boolean hasFlag(Player player, Flag flag){
		User user=users.getUser(player);
		if(user!=null && (user.getUserFlags().contains(flag) || users.groupHasFlag(user.getGroup(), flag))){
			return true;
		}
		return false;
	}
	
	public boolean debug;
	public Logger log;
	
	public ArrayList<String> protectedUsers;
	public String[] rules, blacklist, intro;

	public String ircName,ircHost,ircChannel,ircUserColor,ircOnJoin,gsAuth,gsPass,ircAdminChannel;
	public boolean ircMsg,ircEcho,ircDebug;
	public int ircCharLim,ircPort;
	public String[] ircSeparator;
	private String tips_location = "tips.txt";
	private String  tips_color = ChatColor.AQUA.toString();
	private String  tips_prefix = "";
	private boolean tips_stopTimer = false;
	private int tips_delay = 120;
	private ArrayList<String> tips;
	private int currentTip = 0;
	public String[] ircLevel2;
	public boolean ircEnable;
	private ArrayList<Integer> itemblacklist,superblacklist;
	//private int natureXmin,natureXmax,natureZmin,natureZmax;
	public boolean maintenance=false;
	public boolean safemode;
	public PropFile tpProtect=new PropFile("tpProtect.list");
	public Player OneByOne = null;
	public boolean fun,randomcolor;
	Random random = new Random();
	int playerLimit;
	ArrayList<String> srstaffList,adminsList,trustedList;
}