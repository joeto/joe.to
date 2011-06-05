package to.joe.util;

import org.bukkit.entity.*;
import org.jibble.pircbot.*;

import to.joe.manager.IRC;

public class ircBot extends PircBot {

	private IRC ircman;
	public ircBot(String mah_name,boolean msgenabled,IRC j) {
		this.setName(mah_name);
		this.setAutoNickChange(true);
		ircMsg=msgenabled;
		ircman=j;
		this.setMessageDelay(1100);
	}
	public void onDisconnect(){
		if(ircman.getJ2().ircEnable){
			ircman.restart=true;
			ircman.getJ2().ircEnable=false;
			this.dispose();
		}
	}
	public void onInvite(String targetNick, String sourceNick, String sourceLogin, String sourceHostname, String channel)  
	{
		if(targetNick.equalsIgnoreCase(this.getNick())&&channel.equalsIgnoreCase(ircman.getJ2().ircAdminChannel)){
			this.joinChannel(channel);
		}
	}
	public void onMessage(String channel, String sender,
			String login, String hostname, String message) {
		if(message.charAt(0)=='!'){
			String[] parts=message.split(" ");
			if (message.equalsIgnoreCase("!help")) {
				sendMessage(channel, sender + ": I am here to set you free.");
			}
			else if (message.equalsIgnoreCase("!players") || message.equalsIgnoreCase("!playerlist")) {
				String curPlayers = "";
				int cPlayers=0;
				Player[] players;
				if(channel.equalsIgnoreCase(ircman.getJ2().ircAdminChannel)){
					players=ircman.getJ2().getServer().getOnlinePlayers();
				}
				else{
					players=ircman.getJ2().minitrue.getOnlinePlayers();
				}
				for (Player p : players) {
					if (p != null){
						if(curPlayers.equals("")){
							curPlayers+=p.getName();
						}
						else{
							curPlayers+=", "+p.getName();
						}
						cPlayers++;
					}
				}
				if(curPlayers=="")
					sendMessage(channel,"No players online.");
				else{
					if(message.equalsIgnoreCase("!players"))
						sendMessage(channel,"Currently "+ cPlayers +" of "+ ircman.getJ2().playerLimit +" on the server");
					else
						sendMessage(channel,"Players ("+ cPlayers +" of "+ ircman.getJ2().playerLimit + "): " + curPlayers);
				}
			}
			else if (message.equalsIgnoreCase("!admins")) {
				String curAdmins = "Admins: ";
				for (Player p : ircman.getJ2().getServer().getOnlinePlayers()) {
					if (p != null && (ircman.getJ2().hasFlag(p,Flag.ADMIN))) {
						if(curAdmins=="Admins: "){
							curAdmins+=p.getName();
						}
						else{
							curAdmins+=", "+p.getName();
						}
					}
				}
				if(curAdmins=="Admins: ")
					sendMessage(channel,"No admins online. Find one on #joe.to or #minecraft");
				else if(channel.equalsIgnoreCase(ircman.getJ2().ircAdminChannel))
					sendMessage(channel,curAdmins);
				else
					sendMessage(channel,"There are admins online!");
			}
			else if (ircMsg && parts[0].equalsIgnoreCase("!msg")){
				if(channel.equalsIgnoreCase(ircman.getJ2().ircAdminChannel)){
					sendMessage(channel,"Try that in the other channel.");
				}
				else{
					String damessage = "";
					for(int $x=1;$x<parts.length;$x++)
					{
						damessage+=" "+parts[$x];
					}
					doMsg(channel,sender,damessage);
				}
			}
			else if (ircMsg && parts[0].equalsIgnoreCase("!me")){
				String damessage = "";
				for(int $x=1;$x<parts.length;$x++)
				{
					damessage+=" "+parts[$x];
				}
				doMeMsg(channel,sender,damessage);
			}
			return;
		}
		if(message.charAt(0)=='.' && channel.equalsIgnoreCase(ircman.getJ2().ircChannel)){
			String[] parts=message.split(" ");
			if(ircman.ircCommand(hostname,sender, parts)){
				//sendMessage(sender,"Done :)");
				sendRawLine("NOTICE "+sender+" :Done");
			}
			else{
				if (!ircMsg){
					doMsg(channel,sender," "+message);
				}
				//sendMessage(channel,"You don't have access to that command :(");
			}
			return;
		}
		if (!ircMsg){
			doMsg(channel,sender," "+message);
		}

	}
	public void doMsg(String channel, String sender, String message){
		this.ircman.getJ2().chat.handleIRCChat(sender, message, false,channel);
	}
	public void doMeMsg(String channel, String sender, String message){
		this.ircman.getJ2().chat.handleIRCChat(sender, message, true,channel);
	}
	
	protected void onPrivateMessage(String sender,String login,String hostname,String message){
		if(ircman.ircCommand(hostname,sender,message.split(" "))){
			//sendMessage(sender,"Done :)");
			sendRawLine("NOTICE "+sender+" :Done");
		}
		else{
			//sendMessage(sender,"You don't have access to that command :(");
			sendRawLine("NOTICE "+sender+" :No access to that command");
		}
	}
	
	
	private boolean ircMsg;
}
