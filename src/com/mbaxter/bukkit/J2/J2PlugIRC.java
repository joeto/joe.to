package com.mbaxter.bukkit.J2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;

public class J2PlugIRC {
	private J2Plugin j2;
	private ircBot bot;
	private Object adminsLock = new Object();
	private ArrayList<ircAdmin> admins;

	
	public J2PlugIRC(J2Plugin j2p){
		this.j2=j2p;
		loadIRCAdmins();
		
	}
	
	public void prepIRC(){

		bot=new ircBot(j2.ircName,j2.ircMsg,j2.ircCharLim,j2.ircUserColor,j2.ircEcho,j2.ircSeparator,this);

		if(j2.ircDebug)bot.setVerbose(true);
		System.out.println("Connecting to "+j2.ircChannel+" on "+j2.ircHost+":"+j2.ircPort+" as "+j2.ircName);
		try {
			bot.connect(j2.ircHost,j2.ircPort);
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
		if(bot.getChannels().length==1){
			bot.joinChannel(j2.ircAdminChannel);
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
		/*if(!ircEnable)
			return false;
		int lvl=0;
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
		String commands=combineSplit(0, command, " ");
		if(etc.getInstance().parseConsoleCommand(commands, etc.getMCServer())){
			log.log(Level.INFO,"IRC admin "+adminName+"("+nick+"@"+host+") used command: "+commands);
			return true;
		}
		etc.getServer().useConsoleCommand(commands);
		log.log(Level.INFO,"IRC admin "+adminName+"("+nick+"@"+host+") used command: "+commands);
		 */
		return true;
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
	
	public void ircMsg(String mess){
		if(j2.ircEnable)
			bot.sendMessage(j2.ircChannel,mess);
	}

	public void ircAdminMsg(String mess){
		if(j2.ircEnable)
			bot.sendMessage(j2.ircAdminChannel,mess);
	}
	
	public J2Plugin getJ2(){
		return j2;
	}
}
