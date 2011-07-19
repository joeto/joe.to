package to.joe.manager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import org.bukkit.Location;

import to.joe.J2;
import to.joe.util.IRC.ircAdmin;
import to.joe.util.IRC.ircBot;

/**
 * Manager for the IRC relay
 * @author matt
 *
 */
public class IRC {
	private J2 j2;
	private ircBot bot;
	private Object adminsLock = new Object();
	private ArrayList<ircAdmin> admins;
	private HashMap<String,Long> msgs;
	public LinkedBlockingQueue<String> chatQueue = new LinkedBlockingQueue<String>();
	//private boolean stop = false;
	//private HashMap<String,Long> recent;

	public IRC(J2 j2p){
		this.j2=j2p;
		this.cleanStartup();
	}
	
	/**
	 * Restarts the bot. 
	 */
	public void restartManager(){
		this.cleanStartup();
		if(this.bot!=null){
			bot.quitServer("Restarting...");
		}
		this.prepIRC();
	}
	
	/**
	 * Load IRC admins, clear message queue.
	 */
	public void cleanStartup(){
		loadIRCAdmins();
		this.msgs=new HashMap<String,Long>();
		//this.recent=new HashMap<String,Long>();
	}
	
	/**
	 * Checks if a string matches a previously submitted string from the last hour
	 * If it's a repeat ban announce, dont' send. 
	 * @param string
	 * @return
	 */
	public boolean goodToGo(String string){
		if(string.startsWith("[J2BANS]")||string.startsWith("[BANS]")){
			long rightNow=(new Date()).getTime();
			if(this.msgs.containsKey(string)){
				if((this.msgs.get(string).longValue()+3600000L)<rightNow){
					this.msgs.remove(string);
					this.msgs.put(string, rightNow);
					return true;
				}
				else{
					return false;
				}
			}
			else{
				this.msgs.put(string, rightNow);
				return true;
			}
		}
		return true;
	}

	/**
	 * Announces player joining - currently disabled too spammy
	 * Also queries to see if it's not in the admin channel
	 * @param name
	 */
	public void processJoin(String name){
		if(j2.ircEnable && j2.getServer().getOnlinePlayers().length<2){
			//j2.irc.ircMsg(name+" has logged in");
			j2.irc.adminChannel();
		}
	}
	
	/**
	 * Announces player leaving - currently disabled too spammy
	 * @param name
	 */
	public void processLeave(String name){
		if(j2.ircEnable && j2.getServer().getOnlinePlayers().length<10){
			//j2.irc.ircMsg(name+" has left the server");
		}
	}
	
	/**
	 * Connecting to IRC, authenticating, etc
	 */
	public void prepIRC(){

		bot=new ircBot(j2.ircName,j2.ircMsg,this);

		if(j2.ircDebug)bot.setVerbose(true);
		System.out.println("Connecting to "+j2.ircChannel+" on "+j2.ircHost+":"+j2.ircPort+" as "+j2.ircName);
		try {
			bot.connect(j2.ircHost,j2.ircPort,j2.getServer().getIp());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(j2.gsAuth!=""){
			bot.sendMessage("authserv@services.gamesurge.net", "auth "+j2.gsAuth+" "+j2.gsPass);
			bot.sendMessage("ChanServ", "inviteme "+j2.ircAdminChannel);
		}
		bot.joinChannel(j2.ircChannel);
		loadIRCAdmins();
		if(j2.ircOnJoin!="")bot.sendMessage(j2.ircChannel,j2.ircOnJoin);

	}

	/**
	 * Murder the bot
	 */
	public void kill(){
		if(bot!=null)bot.disconnect();
	}

	/**
	 * Acquires the bot
	 * @return the bot
	 */
	public ircBot getBot(){
		return bot;
	}

	/**
	 * If not connected to admin channel, join it.
	 */
	public void adminChannel(){
		if(j2.ircEnable&&!(new ArrayList<String>(Arrays.asList((bot.getChannels()))).contains(j2.ircAdminChannel))){
			if(j2.gsAuth!=""){
				bot.sendMessage("authserv@services.gamesurge.net", "auth "+j2.gsAuth+" "+j2.gsPass);
				bot.sendMessage("ChanServ", "inviteme "+j2.ircAdminChannel);
				bot.joinChannel(j2.ircAdminChannel);
			}
		}
	}

	/**
	 * check if the hostmask is an admin
	 * @param hostname
	 * @return
	 */
	public boolean isIRCAuth(String hostname){
		synchronized(adminsLock){
			for(ircAdmin admin:admins){
				if(admin!=null && admin.getHostname().equals(hostname)){
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Is the command a level 2 command
	 * @param command
	 * @return
	 */
	public boolean ircLevel2(String command) {
		for (String str : j2.ircLevel2) {
			if (command.equalsIgnoreCase(str)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Send a message to target
	 * @param target
	 * @param message
	 */
	public void doIRC(String target,String message){
		if(j2.ircEnable)
			bot.sendMessage(target, message);
	}

	/**
	 * Process attempted command
	 * @param host
	 * @param nick
	 * @param command
	 * @return
	 */
	public boolean ircCommand(String host,String nick,String[] command){
		if(!j2.ircEnable)
			return false;
		int lvl=0;
		String[] args=new String[command.length-1];
		System.arraycopy(command, 1, args, 0, command.length -1);
		String adminName="";
		synchronized(adminsLock){
			for(ircAdmin admin:admins){
				if(admin!=null && admin.getHostname().equals(host)){
					lvl=admin.getLevel();
					adminName=admin.getUsername();
				}
			}
		}
		if(command[0].charAt(0)=='.'){
			command[0]=command[0].substring(1);
		}
		if(lvl==0 || (lvl==2 && !ircLevel2(command[0])  )  ){
			return false;
		}
		String commands=j2.combineSplit(0, command, " ");
		String com=command[0];
		boolean done=false;
		if(com.equalsIgnoreCase("kick")&&command.length>2){
			j2.kickbans.callKick(command[1], adminName, j2.combineSplit(2,command," "));
			//j2.kickbans.forceKick(command[1], j2.combineSplit(2,command," "));
			done=true;
		}
		if(com.equalsIgnoreCase("ban")&&command.length>2){
			j2.kickbans.callBan(adminName, args, new Location(j2.getServer().getWorlds().get(0), 0,0,0,0,0));
			done=true;
		}
		if(com.equalsIgnoreCase("g")&&command.length>1){
			j2.chat.globalAdminMessage(adminName, j2.combineSplit(1, command, " "));
			done=true;
		}
		if(com.equalsIgnoreCase("a")&&command.length>1){
			j2.chat.adminOnlyMessage(adminName, j2.combineSplit(1, command, " "));
			done=true;
		}
		if(com.equalsIgnoreCase("addban")&&command.length>2){
			j2.kickbans.callAddBan(adminName, args, new Location(j2.getServer().getWorlds().get(0), 0,0,0,0,0));
			done=true;
		}
		if(com.equalsIgnoreCase("unban")&&command.length>1){
			j2.kickbans.unban(adminName, command[1]);
			done=true;
		}
		if(done){
			j2.log("IRC admin "+adminName+"("+nick+"@"+host+") used command: "+commands);
		}
		else {
			j2.log("IRC admin "+adminName+"("+nick+"@"+host+") tried: "+commands);
		}

		return done;
	}
	
	/**
	 * Attempted shutdown
	 * @param hostname
	 */
	public void cough(String hostname){
		if(this.isIRCAuth(hostname)){
			this.j2.madagascar(hostname);
		}
	}

	/**
	 * Reload IRC admins list
	 */
	public void loadIRCAdmins(){
		String location="ircAdmins.txt";
		if (!new File(location).exists()) {
			FileWriter writer = null;
			try {
				writer = new FileWriter(location);
				writer.write("#Add IRC admins here.\r\n");
				writer.write("#The format is:\r\n");
				writer.write("#NAME:HOSTNAME:ACCESSLEVEL\r\n");
				writer.write("#Access levels: 2=kick,ban 3=everything");
				writer.write("#Example:\r\n");
				writer.write("#notch:pancakes.lolcakes:3\r\n");
			} catch (Exception e) {
				j2.logWarn("Exception while creating " + location);
			} finally {
				try {
					if (writer != null) {
						writer.close();
					}
				} catch (IOException e) {
					j2.logWarn("Exception while closing writer for " + location);
				}
			}
		}
		synchronized (adminsLock) {
			admins = new ArrayList<ircAdmin>();
			try {
				Scanner scanner = new Scanner(new File(location));
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					if (line.startsWith("#") || line.equals("") || line.startsWith("﻿")) {
						continue;
					}
					String[] split = line.split(":");
					if(split.length!=3)
						continue;
					ircAdmin admin=new ircAdmin(split[0],split[1],Integer.parseInt(split[2]));
					admins.add(admin);
				}
				scanner.close();
			} catch (Exception e) {
				j2.log( "Exception while reading " + location + " (Are you sure you formatted it correctly?)");
			}
		}
	}

	/**
	 * Send message to regular channel
	 * @param message
	 */
	public void ircMsg(String message){
		if(j2.ircEnable)
			bot.sendMessage(j2.ircChannel,message);
	}

	/**
	 * Send message to admin channel
	 * @param message
	 */
	public void ircAdminMsg(String message){
		if(j2.ircEnable&&this.goodToGo(message))
			bot.sendMessage(j2.ircAdminChannel,message);
	}

	/**
	 * @return the manager's J2
	 */
	public J2 getJ2(){
		return j2;
	}

	private boolean stop;
	public boolean restart = false;
	/**
	 * Start 10 second checks to see if online, if in channels, etc.
	 */
	public void startIRCTimer() {
		stop = false;
		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (stop) {
					timer.cancel();
					return;
				}
				checkStatus();
			}
		}, 1000, 10000);
	}

	private void checkStatus(){
		if(!j2.ircEnable&&restart){
			prepIRC();
			restart=false;
			j2.ircEnable=true;
		}
		else{
			this.adminChannel();
		}
	}

}
