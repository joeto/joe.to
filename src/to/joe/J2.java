/*
 * j2Plugin
 * A bunch of fun features, put together for joe.to
 */

package to.joe;

import java.io.File;

import jline.ConsoleReader;
import jline.Terminal;
import jline.ANSIBuffer.ANSICodes;
import org.bukkit.command.*;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.*;
import org.bukkit.Location;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.config.Configuration;
import org.bukkit.ChatColor;

import to.joe.Commands.*;
import to.joe.Commands.Admin.*;
import to.joe.Commands.Fun.*;
import to.joe.Commands.Info.*;
import to.joe.Commands.SeniorStaff.*;
import to.joe.listener.*;
import to.joe.manager.*;
import to.joe.util.*;
import to.joe.util.Runnables.AutoSave;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * J2 Plugin, on Bukkit
 *
 */
public class J2 extends JavaPlugin {
	private final PlayerChat plrlisChat = new PlayerChat(this);
	private final PlayerInteract plrlisInteract = new PlayerInteract(this);
	private final PlayerJoinQuit plrlisJoinQuit = new PlayerJoinQuit(this);
	private final BlockAll blockListener = new BlockAll(this);
	private final EntityAll entityListener = new EntityAll(this);
	private final PlayerMovement plrlisMovement = new PlayerMovement(this);
	/**
	 * Chat manager
	 */
	public final Chats chat = new Chats(this);
	/**
	 * IRC manager
	 */
	public final IRC irc = new IRC(this);
	/**
	 * Kick/ban manager
	 */
	public final KicksBans kickbans = new KicksBans(this);
	/**
	 * User manager
	 */
	public final Users users = new Users(this);
	/**
	 * Report manager
	 */
	public final Reports reports = new Reports(this);
	/**
	 * Warp manager
	 */
	public final Warps warps = new Warps(this);
	/**
	 * Webpage manager
	 */
	public final WebPage webpage = new WebPage(this);
	/**
	 * IP Tracking manager
	 */
	public final IPTracker ip=new IPTracker(this);
	/**
	 * Ban cooperative manager
	 */
	public final BanCooperative banCoop=new BanCooperative(this);
	/**
	 * Damage manager
	 */
	public final Damages damage=new Damages(this);
	/**
	 * Permission manager
	 */
	public final Permissions perms=new Permissions(this);
	/**
	 * Recipe implementer
	 */
	public final Recipes recipes=new Recipes(this);
	/**
	 * Ministry of Truth
	 */
	public final Minitrue minitrue=new Minitrue(this);
	/**
	 * Jail manager
	 */
	public final Jailer jail = new Jailer(this);
	/**
	 * Movement tracker
	 */
	public final MoveTracker move = new MoveTracker(this);
	/**
	 * Activity tracker
	 */
	public final ActivityTracker activity = new ActivityTracker(this);
	/**
	 * Craftual Harassment Panda
	 */
	public final CraftualHarassmentPanda panda=new CraftualHarassmentPanda(this);
	/**
	 * Vote manager
	 */
	public final Voting voting=new Voting(this);
	//public managerBlockLog blogger;
	/**
	 * MySQL stuffs
	 */
	public MySQL mysql;


	/* (non-Javadoc)
	 * @see org.bukkit.plugin.Plugin#onDisable()
	 */
	public void onDisable() {
		irc.kill();
		stopTimer();
	}

	/* (non-Javadoc)
	 * @see org.bukkit.plugin.Plugin#onEnable()
	 */
	public void onEnable() {
		this.initLog();
		log=Logger.getLogger("Minecraft");
		log.setFilter(new MCLogFilter());
		protectedUsers=new ArrayList<String>();
		loadData();
		this.debug("Data loaded");
		//irc start
		if(ircEnable)irc.connectAndAuth();
		irc.startIRCTimer();
		//if(ircEnable)irc.startIRCTimer();
		this.debug("IRC up (or disabled)");
		//irc end
		loadTips();
		this.debug("Tips loaded");
		startTipsTimer();
		this.debug("Tips timer started");

		//Initialize BlockLogger
		//this.blogger = new managerBlockLog(this.mysql.getConnection(),this.mysql.servnum());
		//if(debug)this.log("Blogger init");
		//new Thread(blogger).start();
		//if(debug)this.log("Blogger is go");
		// Register our events
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_CHAT, plrlisChat, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, plrlisChat, Priority.Monitor, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, plrlisJoinQuit, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, plrlisInteract, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_CANBUILD, blockListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_IGNITE, blockListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_BURN, blockListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.SIGN_CHANGE, blockListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_EXPLODE, entityListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DEATH, entityListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_PRELOGIN, plrlisJoinQuit, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, plrlisJoinQuit, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, plrlisJoinQuit, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_KICK, plrlisJoinQuit, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_TELEPORT, plrlisMovement, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_MOVE,plrlisMovement,Priority.Normal,this);
		pm.registerEvent(Event.Type.CREATURE_SPAWN, entityListener, Priority.Normal, this);
		if(debug)this.log("Events registered");
		this.getCommand("kickall").setExecutor(new KickAllCommand(this));
		this.getCommand("smackirc").setExecutor(new SmackIRCCommand(this));
		this.getCommand("blacklist").setExecutor(new BlacklistCommand(this));
		this.getCommand("help").setExecutor(new HelpCommand(this));
		this.getCommand("intro").setExecutor(new IntroCommand(this));
		this.getCommand("motd").setExecutor(new MOTDCommand(this));
		this.getCommand("rules").setExecutor(new RulesCommand(this));
		this.getCommand("protectme").setExecutor(new ProtectMeCommand(this));
		this.getCommand("tp").setExecutor(new TeleportCommand(this));
		this.getCommand("tphere").setExecutor(new TeleportHereCommand(this));
		this.getCommand("spawn").setExecutor(new SpawnCommand(this));
		this.getCommand("msg").setExecutor(new MessageCommand(this));
		this.getCommand("tell").setExecutor(new MessageCommand(this));
		this.getCommand("i").setExecutor(new ItemCommand(this));
		this.getCommand("item").setExecutor(new ItemCommand(this));
		this.getCommand("time").setExecutor(new TimeCommand(this));
		this.getCommand("who").setExecutor(new PlayerListCommand(this));
		this.getCommand("playerlist").setExecutor(new PlayerListCommand(this));
		this.getCommand("list").setExecutor(new PlayerListCommand(this));
		this.getCommand("a").setExecutor(new AdminChatCommand(this));
		this.getCommand("report").setExecutor(new ReportCommand(this));
		this.getCommand("r").setExecutor(new ReportHandlingCommand(this));
		this.getCommand("g").setExecutor(new AdminGlobalChatCommand(this));
		this.getCommand("ban").setExecutor(new BanCommand(this));
		this.getCommand("b").setExecutor(new BanCommand(this));
		this.getCommand("kick").setExecutor(new KickCommand(this));
		this.getCommand("k").setExecutor(new KickCommand(this));
		this.getCommand("addban").setExecutor(new AddBanCommand(this));
		this.getCommand("unban").setExecutor(new UnBanCommand(this));
		this.getCommand("pardon").setExecutor(new UnBanCommand(this));
		this.getCommand("trust").setExecutor(new TrustCommand(this));
		this.getCommand("getflags").setExecutor(new GetFlagsCommand(this));
		this.getCommand("getgroup").setExecutor(new GetGroupCommand(this));
		this.getCommand("me").setExecutor(new MeCommand(this));
		this.getCommand("ircadminreload").setExecutor(new IRCAdminReloadCommand(this));
		this.getCommand("j2reload").setExecutor(new J2ReloadCommand(this));
		this.getCommand("maintenance").setExecutor(new MaintenanceCommand(this));
		this.getCommand("flags").setExecutor(new FlagsCommand(this));
		this.getCommand("warp").setExecutor(new WarpCommand(this));
		this.getCommand("home").setExecutor(new HomeCommand(this));
		this.getCommand("setwarp").setExecutor(new SetWarpCommand(this));
		this.getCommand("sethome").setExecutor(new SetHomeCommand(this));
		this.getCommand("removewarp").setExecutor(new RemoveWarpCommand(this));
		this.getCommand("removehome").setExecutor(new RemoveHomeCommand(this));
		this.getCommand("homeinvasion").setExecutor(new HomeInvasionCommand(this));
		this.getCommand("hi").setExecutor(new HomeInvasionCommand(this));
		this.getCommand("clearinventory").setExecutor(new ClearInventoryCommand(this));
		this.getCommand("ci").setExecutor(new ClearInventoryCommand(this));
		this.getCommand("removeitem").setExecutor(new RemoveItemCommand(this));
		this.getCommand("ri").setExecutor(new RemoveItemCommand(this));
		this.getCommand("mob").setExecutor(new MobCommand(this));
		this.getCommand("kibbles").setExecutor(new GodmodeCommand(this));
		this.getCommand("bits").setExecutor(new GodmodeCommand(this));
		this.getCommand("coo").setExecutor(new CoordinateTeleportCommand(this));
		this.getCommand("whereis").setExecutor(new WhereIsPlayerCommand(this));
		this.getCommand("madagascar").setExecutor(new MadagascarCommand(this));
		this.getCommand("lookup").setExecutor(new LookupCommand(this));
		this.getCommand("j2lookup").setExecutor(new J2LookupCommand(this));
		this.getCommand("iplookup").setExecutor(new IPLookupCommand(this));
		this.getCommand("smite").setExecutor(new SmiteCommand(this));
		this.getCommand("storm").setExecutor(new StormCommand(this));
		this.getCommand("ircmsg").setExecutor(new IRCMessageCommand(this));
		this.getCommand("flex").setExecutor(new FlexCommand(this));
		this.getCommand("thor").setExecutor(new ThorCommand(this));
		this.getCommand("slay").setExecutor(new SlayCommand(this));
		this.getCommand("amitrusted").setExecutor(new AmITrustedCommand(this));
		this.getCommand("vanish").setExecutor(new VanishCommand(this));
		this.getCommand("imatool").setExecutor(new ImAToolCommand(this));
		this.getCommand("f3").setExecutor(new GetLocationCommand(this));
		this.getCommand("loc").setExecutor(new GetLocationCommand(this));
		this.getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
		this.getCommand("auth").setExecutor(new AuthCommand(this));
		this.getCommand("harass").setExecutor(new HarassCommand(this));
		this.getCommand("muteall").setExecutor(new MuteAllCommand(this));
		this.getCommand("mute").setExecutor(new MuteCommand(this));
		this.getCommand("say").setExecutor(new SayCommand(this));
		this.getCommand("nsa").setExecutor(new NSACommand(this));
		this.getCommand("station").setExecutor(new StationCommand(this));
		this.getCommand("voteadmin").setExecutor(new VoteAdminCommand(this));
		this.getCommand("vote").setExecutor(new VoteCommand(this));
		this.getCommand("maxplayers").setExecutor(new MaxPlayersCommand(this));
		this.getCommand("shush").setExecutor(new ShushCommand(this));
		this.getCommand("hat").setExecutor(new HatCommand(this));
		this.getCommand("note").setExecutor(new NoteCommand(this));
		this.getCommand("anote").setExecutor(new NoteCommand(this));
		this.getCommand("trustreq").setExecutor(new TrustedRequestCommand(this));
		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
		webpage.go(servernumber);
		recipes.addRecipes();
		minitrue.restartManager();
		this.activity.restartManager();
		this.banCoop.startCallback();
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new AutoSave(this),1,6000);//Saves every 5 minutes
	}

	/**
	 * Load a butt-ton of data for startup.
	 */
	public void loadData(){
		rules=readDaFile("rules.txt");
		blacklist=readDaFile("blacklistinfo.txt");
		intro=readDaFile("intro.txt");
		motd=readDaFile("motd.txt");
		help=readDaFile("help.txt");
		Property j2properties = new Property("j2.properties");
		Configuration conf=this.getConfiguration();
		HashMap<String,Object> conf_general=new HashMap<String,Object>();
		HashMap<String,Object> conf_mysql=new HashMap<String,Object>();
		HashMap<String,Object> conf_irc=new HashMap<String,Object>();
		HashMap<String,Object> conf_tips=new HashMap<String,Object>();
		HashMap<String,Object> conf_maint=new HashMap<String,Object>();
		HashMap<String,Object> conf_blacklists=new HashMap<String,Object>();


		try { 
			debug = j2properties.getBoolean("debug",false);
			conf_general.put("debug-mode", this.debug);
			//mysql start
			String mysql_username = j2properties.getString("user", "root");
			String mysql_password = j2properties.getString("pass", "root");
			String mysql_db = j2properties.getString("db", "jdbc:mysql://localhost:3306/minecraft");
			conf_mysql.put("username", mysql_username);
			conf_mysql.put("database", mysql_db);
			conf_mysql.put("password", mysql_password);
			//chatTable = properties.getString("chat","chat");
			servernumber = j2properties.getInt("server-number", 0);
			conf_general.put("server-number", this.servernumber);
			mysql = new MySQL(mysql_username,mysql_password,mysql_db, servernumber, this);
			this.warps.restartManager();
			this.reports.restartManager();
			this.users.restartGroups();
			mysql.loadMySQLData();
			//mysql end

			playerLimit=j2properties.getInt("max-players",20);
			conf_general.put("max-players", this.playerLimit);
			tips_delay = j2properties.getInt("tip-delay", 120);
			tips_color = "\u00A7"+j2properties.getString("tip-color", "b");
			conf_tips.put("delay", tips_delay);
			conf_tips.put("color", tips_color);
			ircHost = j2properties.getString("irc-host","localhost");
			conf_irc.put("host", ircHost);
			ircName = j2properties.getString("irc-name","aMinecraftBot");
			conf_irc.put("nick", ircName);
			ircChannel = j2properties.getString("irc-channel","#minecraftbot");
			conf_irc.put("relay-channel", ircChannel);
			ircAdminChannel = j2properties.getString("irc-adminchannel","#minecraftbotadmin");
			conf_irc.put("admin-channel", ircAdminChannel);
			int ircuc = j2properties.getInt("irc-usercolor",15);
			conf_irc.put("ingame-color", ircuc);
			ircUserColor=mysql.toColor(ircuc);
			ircSeparator= j2properties.getString("irc-separator","<,>").split(",");
			conf_irc.put("ingame-separator", j2properties.getString("irc-separator","<,>"));
			ircCharLim = j2properties.getInt("irc-charlimit",390);
			conf_irc.put("char-limit", ircCharLim);
			ircMsg=j2properties.getBoolean("irc-msg-enable",false);
			conf_irc.put("require-msg-cmd", ircMsg);
			ircEnable=j2properties.getBoolean("irc-enable",false);
			conf_irc.put("enable", ircEnable);
			ircEcho = j2properties.getBoolean("irc-echo",false);
			conf_irc.put("echo-messages", ircEcho);
			ircPort = j2properties.getInt("irc-port",6667);
			conf_irc.put("port", ircPort);
			ircDebug = j2properties.getBoolean("irc-debug",false);
			conf_irc.put("debug-spam", ircDebug);
			ircOnJoin = j2properties.getString("irc-onjoin","");
			conf_irc.put("channel-join-message", ircOnJoin);
			gsAuth = j2properties.getString("gs-auth","");
			conf_irc.put("gamesurge-user", gsAuth);
			gsPass = j2properties.getString("gs-pass","");
			conf_irc.put("gamesurge-pass", gsPass);
			ircLevel2 = j2properties.getString("irc-level2","").split(",");
			conf_irc.put("level2-commands", j2properties.getString("irc-level2"));
			safemode=j2properties.getBoolean("safemode",false);
			conf_general.put("safemode", safemode);
			explodeblocks=j2properties.getBoolean("explodeblocks",true);
			conf_general.put("allow-explosions", explodeblocks);
			ihatewolves=j2properties.getBoolean("ihatewolves", false);
			conf_general.put("disable-wolves", ihatewolves);
			maintenance = j2properties.getBoolean("maintenance",false);
			conf_maint.put("enable", maintenance);
			maintmessage = j2properties.getString("maintmessage","Server offline for maintenance");
			conf_maint.put("message", maintmessage);
			trustedonly=j2properties.getBoolean("trustedonly",false);
			conf_general.put("block-nontrusted", trustedonly);
			randomcolor=j2properties.getBoolean("randcolor",false);
			conf_general.put("random-namecolor", randomcolor);
			String superBlacklist = j2properties.getString("superblacklist", "0");
			conf_blacklists.put("prevent-trusted", superBlacklist);
			String regBlacklist = j2properties.getString("regblacklist", "0");
			conf_blacklists.put("prevent-general", regBlacklist);
			String watchList = j2properties.getString("watchlist","0");
			conf_blacklists.put("watchlist", watchlist);
			String summonList = j2properties.getString("summonlist","0");
			conf_blacklists.put("prevent-summon", summonList);
			mcbansapi=j2properties.getString("mcbans-api", "");
			conf_general.put("mcbans-api", mcbansapi);
			mcbouncerapi=j2properties.getString("mcbouncer-api", "");
			conf_general.put("mcbouncer-api", mcbouncerapi);
			String[] jail=j2properties.getString("jail","10,11,10,0,0").split(",");
			conf_general.put("jail-xyzpy", j2properties.getString("jail"));
			this.jail.jailSet(jail);
			superblacklist=new ArrayList<Integer>();
			itemblacklist=new ArrayList<Integer>();
			watchlist=new ArrayList<Integer>();
			summonlist=new ArrayList<Integer>();
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
			for(String s:watchList.split(",")){
				if(s!=null){
					watchlist.add(Integer.valueOf(s));
				}
			}
			for(String s:summonList.split(",")){
				if(s!=null){
					summonlist.add(Integer.valueOf(s));
				}
			}
			if(safemode){
				Player[] online=getServer().getOnlinePlayers();
				if(online.length>0){
					for(Player p:online){
						if(p!=null)
							damage.protect(p.getName());
					}
				}
			}
			else {
				damage.clear();
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "Exception while reading from j2.properties", e);
		}
		conf.setProperty("General", conf_general);
		conf.setProperty("MySQL", conf_mysql);
		conf.setProperty("IRC", conf_irc);
		conf.setProperty("Maintenance", conf_maint);
		conf.setProperty("Tips", conf_tips);
		conf.setProperty("Blacklists", conf_blacklists);
		conf.save();
		if(safemode){
			Player[] online=getServer().getOnlinePlayers();
			if(online.length>0){
				for(Player p:online){
					if(p!=null)
						damage.protect(p.getName());
				}
			}
		}
		else {
			damage.clear();
		}
		this.perms.load();
	}



	/**
	 * Read named file
	 * @param filename
	 * @return array of lines
	 */
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

	private void broadcastTip()
	{
		if (tips.isEmpty())
			return;
		String message = ChatColor.AQUA+"[TIP] "+tips.get(curTipNum);
		this.chat.messageAll(message);
		this.log(message);
		curTipNum++;
		if (curTipNum >= tips.size())
			curTipNum = 0;			
	}


	private void loadTips() {
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

	/**
	 * Is the ID on the super-blacklist?
	 * @param id
	 * @return
	 */
	public boolean isOnSuperBlacklist(int id) {
		return superblacklist.contains(Integer.valueOf(id));
	}
	/**
	 * Is the ID on the regular blacklist?
	 * @param id
	 * @return
	 */
	public boolean isOnRegularBlacklist(int id) {
		return itemblacklist.contains(Integer.valueOf(id));
	}
	/**
	 * Is the ID being watched for summoning?
	 * @param id
	 * @return
	 */
	public boolean isOnWatchlist(int id) {
		return watchlist.contains(Integer.valueOf(id));
	}
	/**
	 * Is the ID being blocked from summoning?
	 * @param id
	 * @return
	 */
	public boolean isOnSummonlist(int id) {
		return summonlist.contains(Integer.valueOf(id));
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


	/**
	 * Combine a String array from startIndex with separator
	 * @param startIndex
	 * @param string
	 * @param seperator
	 * @return
	 */
	public String combineSplit(int startIndex, String[] string, String seperator) {
		StringBuilder builder = new StringBuilder();
		for (int i = startIndex; i < string.length; i++) {
			builder.append(string[i]);
			builder.append(seperator);
		}
		builder.deleteCharAt(builder.length() - seperator.length()); 
		return builder.toString();
	}

	/**
	 * Does the user have this flag when authed?
	 * @param playername
	 * @param flag
	 * @return
	 */
	public boolean reallyHasFlag(String playername, Flag flag){
		User user=users.getUser(playername);
		if(user!=null){
			if(user.getUserFlags().contains(flag) || users.groupHasFlag(user.getGroup(), flag)){
				return true;
			}
		}
		return false;
	}

	/**
	 * Does user have this flag currently?
	 * @param playername
	 * @param flag
	 * @return
	 */
	public boolean hasFlag(String playername, Flag flag){
		User user=users.getUser(playername);
		if(user!=null){
			if((flag.equals(Flag.ADMIN)||flag.equals(Flag.SRSTAFF))&&!users.isAuthed(playername)){
				return false;
			}
			if(user.getUserFlags().contains(flag) || users.groupHasFlag(user.getGroup(), flag)){
				return true;
			}
		}
		else{
			Player player=this.getServer().getPlayer(playername);
			if(player!=null&&player.isOnline()){
				player.kickPlayer("Rejoin in 10 seconds.");
			}
		}
		return false;
	}

	/**
	 * Lazy hasFlag
	 * @param player
	 * @param flag
	 * @return
	 */
	public boolean hasFlag(Player player, Flag flag){
		return this.hasFlag(player.getName(), flag);
	}

	/**
	 * Send jail message to player
	 * @param player
	 */
	public void jailMsg(Player player){
		player.sendMessage(ChatColor.RED+"You are "+ChatColor.DARK_RED+"IN JAIL");
		player.sendMessage(ChatColor.RED+"for violation of our server rules");
		player.sendMessage(ChatColor.RED+"Look around you for info on freedom");
	}

	/**
	 * Part of fakeCraftIRC. Send message to named tag.
	 * @param message
	 * @param tag
	 */
	public void craftIRC_sendMessageToTag(String message, String tag){
		if(debug){
			this.log("J2: Got message, tag \""+tag+"\"");
		}
		if(tag.equalsIgnoreCase("nocheat")){
			irc.messageAdmins(message);
			if(debug){
				this.log("J2.2: Got message, tag \""+tag+"\"");
			}
		}
	}

	/**
	 * How many sanitized users does this string match?
	 * @param name
	 * @return
	 */
	public int playerMatches(String name){
		List<Player> list=this.minitrue.matchPlayer(name,true);
		if(list==null){
			return 0;
		}
		return list.size();
	}

	private final Map<ChatColor, String> ANSI_replacements = new EnumMap<ChatColor, String>(ChatColor.class);
	private final ChatColor[] ANSI_colors = ChatColor.values();
	private Terminal ANSI_terminal;
	private ConsoleReader ANSI_reader;
	private void initLog(){
		this.ANSI_reader = ((CraftServer)this.getServer()).getReader();
		this.ANSI_terminal = ANSI_reader.getTerminal();
		ANSI_replacements.put(ChatColor.BLACK, ANSICodes.attrib(0));
		ANSI_replacements.put(ChatColor.RED, ANSICodes.attrib(31));
		ANSI_replacements.put(ChatColor.DARK_RED, ANSICodes.attrib(31));
		ANSI_replacements.put(ChatColor.GREEN, ANSICodes.attrib(32));
		ANSI_replacements.put(ChatColor.DARK_GREEN, ANSICodes.attrib(32));
		ANSI_replacements.put(ChatColor.YELLOW, ANSICodes.attrib(33));
		ANSI_replacements.put(ChatColor.GOLD, ANSICodes.attrib(33));
		ANSI_replacements.put(ChatColor.BLUE, ANSICodes.attrib(34));
		ANSI_replacements.put(ChatColor.DARK_BLUE, ANSICodes.attrib(34));
		ANSI_replacements.put(ChatColor.LIGHT_PURPLE, ANSICodes.attrib(35));
		ANSI_replacements.put(ChatColor.DARK_PURPLE, ANSICodes.attrib(35));
		ANSI_replacements.put(ChatColor.AQUA, ANSICodes.attrib(36));
		ANSI_replacements.put(ChatColor.DARK_AQUA, ANSICodes.attrib(36));
		ANSI_replacements.put(ChatColor.WHITE, ANSICodes.attrib(37));
	}

	private String logPrep(String message){
		if (ANSI_terminal.isANSISupported()) {
			String result = message;

			for (ChatColor color : ANSI_colors) {
				if (ANSI_replacements.containsKey(color)) {
					result = result.replaceAll(color.toString(), ANSI_replacements.get(color));
				} else {
					result = result.replaceAll(color.toString(), "");
				}
			}
			return result + ANSICodes.attrib(0);
		} else {
			return ChatColor.stripColor(message);
		}
	}

	/**
	 * Add string to log, as INFO
	 * @param message
	 */
	public void log(String message){
		this.log.info(this.logPrep(message));
	}

	/**
	 * Add string to log, as WARNING
	 * @param message
	 */
	public void logWarn(String message){
		this.log.warning(this.logPrep(message));
	}

	/**
	 * If debugging enabled, log message.
	 * @param message
	 */
	public void debug(String message){
		if(this.debug){
			this.log(message);
		}
	}

	/**
	 * Message admins and the log.
	 * @param message
	 */
	public void sendAdminPlusLog(String message){
		this.chat.messageByFlag(Flag.ADMIN, message);
		this.log(message);
	}

	/**
	 * SHUT. DOWN. EVERYTHING.
	 * @param name Admin shutting down
	 */
	public void madagascar(String name){
		this.sendAdminPlusLog(name+" wants to SHUT. DOWN. EVERYTHING.");
		if(this.ircEnable){
			if(name.equalsIgnoreCase("console")){
				irc.getBot().sendMessage(this.ircAdminChannel, "A MAN IN BRAZIL IS COUGHING");
			}
			ircEnable=false;
			irc.getBot().quitServer("SHUT. DOWN. EVERYTHING.");
		}
		this.maintenance=true;
		kickbans.kickAll("We'll be back after these brief messages");
		this.getServer().dispatchCommand(new ConsoleCommandSender(this.getServer()), "stop");
	}

	/**
	 * Safe teleporting, removing players from vehicles
	 * @param player
	 * @param location
	 */
	public void safePort(Player player, Location location){
		Entity vehicle=player.getVehicle();
		if(vehicle!=null){
			player.leaveVehicle();
			vehicle.remove();
		}
		player.teleport(location);
	}

	public SimpleDateFormat shortdateformat=new SimpleDateFormat("yyyy-MM-dd kk:mm");
	private boolean debug;
	private Logger log;

	public ArrayList<String> protectedUsers;
	public String[] rules, blacklist, intro, motd, help;

	public String ircName,ircHost,ircChannel,ircOnJoin,gsAuth,gsPass,ircAdminChannel;
	public ChatColor ircUserColor;
	public boolean ircMsg,ircEcho,ircDebug;
	public int ircCharLim,ircPort;
	public String[] ircSeparator;
	private String tips_location = "tips.txt";
	private String  tips_color = ChatColor.AQUA.toString();
	private boolean tips_stopTimer = false;
	private int tips_delay = 120;
	private ArrayList<String> tips;
	private int curTipNum = 0;
	public String[] ircLevel2;
	public boolean ircEnable;
	public ArrayList<Integer> itemblacklist,superblacklist,watchlist,summonlist;
	//private int natureXmin,natureXmax,natureZmin,natureZmax;
	public boolean maintenance=false;
	public String maintmessage;
	public boolean safemode;
	public boolean explodeblocks;
	public boolean ihatewolves;
	public boolean trustedonly;
	public Property tpProtect=new Property("tpProtect.list");
	public Player OneByOne = null;
	public boolean randomcolor;
	public Random random = new Random();
	public int playerLimit;
	public int servernumber;
	ArrayList<String> srstaffList,adminsList,trustedList;
	public String mcbansapi,mcbouncerapi;
}