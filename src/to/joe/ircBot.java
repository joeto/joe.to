package to.joe;

/*
 * mbaxter's irc bot main code for bukkit
 * cannot be distributed as pircbot is under GPL
 * 
 */

import org.bukkit.entity.*;
import org.jibble.pircbot.*;

public class ircBot extends PircBot {

	private J2PlugIRC plug;
	public ircBot(String mah_name,boolean msgenabled,int charlim,String usercolor,boolean echo,String[] sep,J2PlugIRC j) {
		this.setName(mah_name);
		this.setAutoNickChange(true);
		ircMsg=msgenabled;
		ircCharLim = charlim;
		ircUserColor = usercolor;
		ircEcho = echo;
		ircSeparator=sep;
		plug=j;
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
				for (Player p : plug.getJ2().getServer().getOnlinePlayers()) {
					if (p != null) {
						if(curPlayers==""){
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
						sendMessage(channel,"Currently "+ cPlayers +" of "+ plug.getJ2().playerLimit +" on the server");
					else
						sendMessage(channel,"Players ("+ cPlayers +" of "+ plug.getJ2().playerLimit + "):" + curPlayers);
				}
			}
			else if (message.equalsIgnoreCase("!admins")) {
				String curAdmins = "Admins: ";
				for (Player p : plug.getJ2().getServer().getOnlinePlayers()) {
					if (p != null && (plug.getJ2().hasFlag(p,Flag.ADMIN))) {
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
				else if(channel.equalsIgnoreCase(plug.getJ2().ircAdminChannel))
					sendMessage(channel,curAdmins);
				else
					sendMessage(channel,"There are admins online!");
			}
			else if (ircMsg && parts[0].equalsIgnoreCase("!msg")){
				String damessage = "";
				for(int $x=1;$x<parts.length;$x++)
				{
					damessage+=" "+parts[$x];
				}
				doMsg(channel,sender,damessage);
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
		if(message.charAt(0)=='.' && channel.equalsIgnoreCase(plug.getJ2().ircChannel)){
			String[] parts=message.split(" ");
			if(plug.ircCommand(hostname,sender, parts)){
				sendMessage(channel,"Done :)");
			}
			else{
				sendMessage(channel,"You don't have access to that command :(");
			}
			return;
		}
		if (!ircMsg){
			doMsg(channel,sender,message);
		}

	}
	public void doMsg(String channel, String sender, String message){
		if(addMsg(message,sender))
		{
			if(ircEcho)
				sendMessage(channel,"[IRC] <"+sender+">"+message);

		}
		else
		{
			sendMessage(channel,sender+": Your message was too long. The limit's " + ircCharLim + " characters");
		}
	}
	public void doMeMsg(String channel, String sender, String message){
		if(addMeMsg(message,sender))
		{
			if(ircEcho)
				sendMessage(channel,"[IRC] * "+sender+message);

		}
		else
		{
			sendMessage(channel,sender+": Your message was too long. The limit's " + ircCharLim + " characters");
		}
	}
	public boolean addMsg(String thenewmsg,String theuser)
	{
		String combined=ircSeparator[0]+"§"+ircUserColor+theuser+"§f"+ircSeparator[1]+thenewmsg;
		if(combined.length() > ircCharLim)
		{
			return false;
		}
		else
		{
			plug.getJ2().log.info("IRC:<"+theuser+"> "+thenewmsg);
			plug.getJ2().getChat().addChat("[irc]"+theuser, thenewmsg);
			for (Player p : plug.getJ2().getServer().getOnlinePlayers()) {
				if (p != null) {
					p.sendMessage(combined);
				}
			}
			return true;
		}

	}
	public boolean addMeMsg(String thenewmsg,String theuser)
	{
		String combined="* §"+ircUserColor+theuser+"§f"+thenewmsg;
		if(combined.length() > ircCharLim)
		{
			return false;
		}
		else
		{
			plug.getJ2().log.info("IRC: * "+theuser+thenewmsg);
			plug.getJ2().getChat().addChat("[irc]* "+theuser, thenewmsg);
			for (Player p : plug.getJ2().getServer().getOnlinePlayers()) {
				if (p != null) {
					p.sendMessage(combined);
				}
			}
			return true;
		}

	}
	protected void onPrivateMessage(String sender,String login,String hostname,String message){
		if(plug.ircCommand(hostname,sender,message.split(" "))){
			sendMessage(sender,"Done :)");
		}
		else{
			sendMessage(sender,"You don't have access to that command :(");
		}
	}
	
	
	private boolean ircMsg;
	private boolean ircEcho;
	private int ircCharLim;
	private String ircUserColor;
	private String[] ircSeparator;
}
