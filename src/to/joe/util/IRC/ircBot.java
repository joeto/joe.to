package to.joe.util.IRC;

import org.bukkit.entity.*;
import org.jibble.pircbot.*;

import to.joe.manager.IRC;
import to.joe.util.Flag;

public class ircBot extends PircBot {

	private IRC irc;
	public ircBot(String mah_name,boolean msgenabled,IRC j) {
		this.setName(mah_name);
		this.setAutoNickChange(true);
		ircMsg=msgenabled;
		irc=j;
		this.setMessageDelay(1100);
	}
	public void onDisconnect(){
		if(irc.getJ2().ircEnable){
			irc.restart=true;
			irc.getJ2().ircEnable=false;
			this.dispose();
		}
	}
	public void onInvite(String targetNick, String sourceNick, String sourceLogin, String sourceHostname, String channel)  
	{
		if(targetNick.equalsIgnoreCase(this.getNick())&&channel.equalsIgnoreCase(irc.getJ2().ircAdminChannel)){
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
				if(channel.equalsIgnoreCase(irc.getJ2().ircAdminChannel)){
					players=irc.getJ2().getServer().getOnlinePlayers();
				}
				else{
					players=irc.getJ2().minitrue.getOnlinePlayers();
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
						sendMessage(channel,"Currently "+ cPlayers +" of "+ irc.getJ2().playerLimit +" on the server");
					else
						sendMessage(channel,"Players ("+ cPlayers +" of "+ irc.getJ2().playerLimit + "): " + curPlayers);
				}
			}
			else if (message.equalsIgnoreCase("!admins")) {
				String curAdmins = "Admins: ";
				for (Player p : irc.getJ2().getServer().getOnlinePlayers()) {
					if (p != null && (irc.getJ2().hasFlag(p,Flag.ADMIN))) {
						if(curAdmins=="Admins: "){
							curAdmins+=p.getName();
						}
						else{
							curAdmins+=", "+p.getName();
						}
					}
				}
				if(curAdmins=="Admins: ")
					sendMessage(channel,"No admins online.");
				else if(channel.equalsIgnoreCase(irc.getJ2().ircAdminChannel))
					sendMessage(channel,curAdmins);
				else
					sendMessage(channel,"There are admins online!");
			}
			else if (ircMsg && parts[0].equalsIgnoreCase("!msg")){
				if(channel.equalsIgnoreCase(irc.getJ2().ircAdminChannel)){
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
			else if (ircMsg && parts[0].equalsIgnoreCase("!broadcast")){
				if(!channel.equalsIgnoreCase(irc.getJ2().ircAdminChannel)){
					sendMessage(channel,"Try that in the other channel.");
				}
				else{
					String damessage = "";
					for(int $x=1;$x<parts.length;$x++)
					{
						damessage+=" "+parts[$x];
					}
					this.irc.getJ2().chat.handleBroadcastFromIRC(sender, damessage);
				}
			}
			else if (ircMsg && parts[0].equalsIgnoreCase("!reports")){
				if(!channel.equalsIgnoreCase(irc.getJ2().ircAdminChannel)){
					sendMessage(channel,"Try that in the other channel.");
				}
				else{
					String damessage = "";
					
					int size = irc.getJ2().reports.getReports().size();
					damessage = "There are currently " + size + " reports open.";
					if(size > 5)
					{
						damessage += " Seriously guys? Start cleaning up.";
					}
                                        if(size == 0)
                                        {
                                                damessage += " \o/"
                                        }
					sendMessage(channel, damessage);
				}
			}
			else if (ircMsg && parts[0].equalsIgnoreCase("!me")){
				if(channel.equalsIgnoreCase(irc.getJ2().ircAdminChannel)){
					sendMessage(channel,"Try that in the other channel.");
				}
				String damessage = "";
				for(int $x=1;$x<parts.length;$x++)
				{
					damessage+=" "+parts[$x];
				}
				doMeMsg(channel,sender,damessage);
			}
			return;
		}
		if(message.charAt(0)=='.' && channel.equalsIgnoreCase(irc.getJ2().ircChannel)){
			String[] parts=message.split(" ");
			if(irc.ircCommand(hostname,sender, parts)){
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
		if(message.equals("A MAN IN BRAZIL IS COUGHING") && channel.equalsIgnoreCase(irc.getJ2().ircChannel)){
			this.irc.cough(hostname);
		}
		if (!ircMsg){
			doMsg(channel,sender," "+message);
		}

	}
	public void doMsg(String channel, String sender, String message){
		this.irc.getJ2().chat.handleIRCChat(sender, message, false,channel);
	}
	public void doMeMsg(String channel, String sender, String message){
		this.irc.getJ2().chat.handleIRCChat(sender, message, true,channel);
	}
	
	protected void onPrivateMessage(String sender,String login,String hostname,String message){
		if(irc.ircCommand(hostname,sender,message.split(" "))){
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
