package to.joe.util.IRC;

import org.bukkit.entity.*;
import org.jibble.pircbot.*;

import to.joe.manager.IRC;
import to.joe.util.Flag;

public class ircBot extends PircBot {

	private IRC irc;
	public ircBot(String mah_name,boolean msgenabled,IRC irc) {
		this.setName(mah_name);
		this.setAutoNickChange(true);
		this.ircMsg=msgenabled;
		this.irc=irc;
		this.setMessageDelay(1100);
	}
	@Override
	public void onDisconnect(){
		if(this.irc.getJ2().ircEnable){
			this.irc.restart=true;
			this.irc.getJ2().ircEnable=false;
			this.dispose();
		}
	}
	@Override
	public void onInvite(String targetNick, String sourceNick, String sourceLogin, String sourceHostname, String channel)  
	{
		if(targetNick.equalsIgnoreCase(this.getNick())&&channel.equalsIgnoreCase(this.irc.getJ2().ircAdminChannel)){
			this.joinChannel(channel);
		}
	}
	@Override
	public void onMessage(String channel, String sender, String login, String hostname, String message) {
		if(message.charAt(0)=='!'){
			String[] parts=message.split(" ");
			if (parts[0].toLowerCase().equals("!help")) {
				this.sendNotice(sender, "!msg - Send a message to players on the server | !players - Get current playercount | !playerlist - List players online");
			}
			else if (message.equalsIgnoreCase("!players") || message.equalsIgnoreCase("!playerlist")) {
				String curPlayers = "";
				int cPlayers=0;
				Player[] players;
				if(channel.equalsIgnoreCase(this.irc.getJ2().ircAdminChannel)){
					players=this.irc.getJ2().getServer().getOnlinePlayers();
				}
				else{
					players=this.irc.getJ2().minitrue.getOnlinePlayers();
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
				if(curPlayers==""){
					this.sendMessage(channel,"No players online.");
				}
				else{
					if(message.equalsIgnoreCase("!players")){
						this.sendMessage(channel,"Currently "+ cPlayers +" of "+ this.irc.getJ2().playerLimit +" on the server");
					}
					else{
						this.sendMessage(channel,"Players ("+ cPlayers +" of "+ this.irc.getJ2().playerLimit + "): " + curPlayers);
					}
				}
			}
			else if (message.equalsIgnoreCase("!admins")) {
				String curAdmins = "Admins: ";
				for (Player p : this.irc.getJ2().getServer().getOnlinePlayers()) {
					if (p != null && (this.irc.getJ2().hasFlag(p,Flag.ADMIN))) {
						if(curAdmins=="Admins: "){
							curAdmins+=p.getName();
						}
						else{
							curAdmins+=", "+p.getName();
						}
					}
				}
				boolean adminsOnline=!curAdmins.equals("Admins: ");
				if(channel.equalsIgnoreCase(this.irc.getJ2().ircAdminChannel)){
					if(!adminsOnline){
						this.sendMessage(channel,"No admins online.");
					}
					else {
						this.sendMessage(channel,curAdmins);
					}
				}
				else{
					if(!adminsOnline){
						this.sendMessage(channel,"No admins online. Find one on #joe.to or #minecraft");
					}
					else {
						this.sendMessage(channel,"There are admins online!");
					}
				}
			}
			else if (this.ircMsg && parts[0].equalsIgnoreCase("!msg")){
				if(channel.equalsIgnoreCase(irc.getJ2().ircAdminChannel)){
					this.sendMessage(channel,"Try that in the other channel.");
				}
				else{
					this.doMsg(channel,sender,this.irc.getJ2().combineSplit(1, parts, " "));
				}
			}
			else if (parts[0].equalsIgnoreCase("!broadcast")){
				if(!channel.equalsIgnoreCase(irc.getJ2().ircAdminChannel)){
					sendMessage(channel,"Try that in the other channel.");
				}
				else{
					this.irc.getJ2().chat.handleBroadcastFromIRC(sender, this.irc.getJ2().combineSplit(1, parts, " "));
				}
			}
			else if (parts[0].equalsIgnoreCase("!reports")){
				if(!channel.equalsIgnoreCase(irc.getJ2().ircAdminChannel)){
					this.sendMessage(channel,"Try that in the other channel.");
				}
				else{
					String response = "";
					int size = this.irc.getJ2().reports.getReports().size();
					response = "There are currently " + size + " reports open. ";
					switch(size){
					case 0:
						response+="\\o/";
						break;
					case 1:
						response+=":|";
						break;
					case 2:
						response+=":(";
						break;
					case 3:
						response+=":'(";
						break;
					case 4:
						response+="D:";
						break;
					default:
						response += "Seriously guys? Start cleaning up.";
						break;
					}
					this.sendMessage(channel, response);
				}
			}
			else if (this.ircMsg && parts[0].equalsIgnoreCase("!me")){
				if(channel.equalsIgnoreCase(irc.getJ2().ircAdminChannel)){
					this.sendMessage(channel,"Try that in the other channel.");
				}
				this.doMeMsg(channel,sender,this.irc.getJ2().combineSplit(1, parts, " "));
			}
			else if (parts[0].equalsIgnoreCase("!has")){
				if(!channel.equalsIgnoreCase(irc.getJ2().ircAdminChannel)){
					this.sendMessage(channel,"Try that in the other channel.");
				}
				String has=null;
				for(Player player:this.irc.getJ2().getServer().getOnlinePlayers()){
					String name=player.getName();
					if(name.equalsIgnoreCase(parts[1])){
						has=name;
					}
				}
				if(has!=null){
					this.sendMessage(channel, "I have "+has+"!");
				}
			}
			return;
		}
		if(message.charAt(0)=='.' && channel.equalsIgnoreCase(this.irc.getJ2().ircChannel)){
			String[] parts=message.split(" ");
			if(irc.ircCommand(hostname,sender, parts)){
				this.sendRawLine("NOTICE "+sender+" :Done");
			}
			else if (!this.ircMsg){
				this.doMsg(channel,sender," "+message);
			}
			return;
		}
		if(message.equals("A MAN IN BRAZIL IS COUGHING")){
			this.irc.cough(hostname);
		}
		if (!this.ircMsg){
			this.doMsg(channel,sender," "+message);
		}
	}
	private void doMsg(String channel, String sender, String message){
		this.irc.getJ2().chat.handleIRCChat(sender, message, false,channel);
	}
	private void doMeMsg(String channel, String sender, String message){
		this.irc.getJ2().chat.handleIRCChat(sender, message, true,channel);
	}
	@Override
	protected void onPrivateMessage(String sender,String login,String hostname,String message){
		if(irc.ircCommand(hostname,sender,message.split(" "))){
			this.sendRawLine("NOTICE "+sender+" :Done");
		}
		else{
			this.sendRawLine("NOTICE "+sender+" :No access to that command");
		}
	}
	private boolean ircMsg;
}
