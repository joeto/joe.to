package to.joe;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import org.bukkit.Location;

public class managerIRC {
	private J2Plugin j2;
	private ircBot bot;
	private Object adminsLock = new Object();
	private ArrayList<ircAdmin> admins;
	public LinkedBlockingQueue<String> chatQueue = new LinkedBlockingQueue<String>();
	//private boolean stop = false;

	public managerIRC(J2Plugin j2p){
		this.j2=j2p;
		loadIRCAdmins();

	}

	public void prepIRC(){

		bot=new ircBot(j2.ircName,j2.ircMsg,j2.ircCharLim,j2.ircUserColor,j2.ircEcho,j2.ircSeparator,this);

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

	public void kill(){
		if(bot!=null)bot.disconnect();
	}

	public ircBot getBot(){
		return bot;
	}

	public void adminChannel(){
		if(j2.ircEnable&&!(new ArrayList<String>(Arrays.asList((bot.getChannels()))).contains(j2.ircAdminChannel))){
			if(j2.gsAuth!=""){
				bot.sendMessage("authserv@services.gamesurge.net", "auth "+j2.gsAuth+" "+j2.gsPass);
				bot.sendMessage("ChanServ", "inviteme "+j2.ircAdminChannel);
				bot.joinChannel(j2.ircAdminChannel);
			}
		}
	}

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

	public boolean ircLevel2(String command) {
		for (String str : j2.ircLevel2) {
			if (command.equalsIgnoreCase(str)) {
				return true;
			}
		}
		return false;
	}

	public void doIRC(String to,String message){
		if(j2.ircEnable)
			bot.sendMessage(to, message);
	}

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
			j2.chat.gMsg(adminName, j2.combineSplit(1, command, " "));
			done=true;
		}
		if(com.equalsIgnoreCase("a")&&command.length>1){
			j2.chat.aMsg(adminName, j2.combineSplit(1, command, " "));
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
			j2.log.log(Level.INFO,"IRC admin "+adminName+"("+nick+"@"+host+") used command: "+commands);
		}
		else {
			j2.log.log(Level.INFO,"IRC admin "+adminName+"("+nick+"@"+host+") tried: "+commands);
		}

		return done;
	}

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
				j2.log.log(Level.SEVERE, "Exception while creating " + location, e);
			} finally {
				try {
					if (writer != null) {
						writer.close();
					}
				} catch (IOException e) {
					j2.log.log(Level.SEVERE, "Exception while closing writer for " + location, e);
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
				j2.log.log(Level.SEVERE, "Exception while reading " + location + " (Are you sure you formatted it correctly?)", e);
			}
		}
	}

	public void ircMsg(String message){
		if(j2.ircEnable)
			bot.sendMessage(j2.ircChannel,message);
	}

	public void ircAdminMsg(String message){
		if(j2.ircEnable)
			bot.sendMessage(j2.ircAdminChannel,message);
	}

	public J2Plugin getJ2(){
		return j2;
	}

	private boolean stop;
	public boolean restart = false;
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
