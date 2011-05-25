package to.joe.manager;

import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import to.joe.J2;

public class Configurator {

	private J2 j2;
	Configuration conf;
	public Configurator(J2 j2){
		this.j2=j2;
		this.conf=this.j2.getConfiguration();
	}
	public void load(){
		conf.load();
		ConfigurationNode general=conf.getNode("General");

		this.general_allow_explosions=general.getBoolean("allow-explosions", true);
		this.general_block_nontrusted=general.getBoolean("block-nontrusted", false);
		this.general_debug_mode=general.getBoolean("debug-mode",false);
		this.general_disable_wolves=general.getBoolean("disable-wolves", false);
		String jail=general.getString("jail-xyzpy", "1,1,1,1,1");
		String[] jail_split=jail.split(",");
		this.general_jail_xyzpy=new Location(this.j2.getServer().getWorld("world"),Integer.valueOf(jail_split[0]),Integer.valueOf(jail_split[0]),Integer.valueOf(jail_split[0]),Integer.valueOf(jail_split[0]),Integer.valueOf(jail_split[0]));
		this.general_max_players=general.getInt("max-players", 30);
		this.general_mcbans_api=general.getString("mcbans-api","");
		this.general_random_namecolor=general.getBoolean("random-namecolor",false);
		this.general_safemode=general.getBoolean("safemode", false);
		this.general_server_number=general.getInt("server-number", 0);

		ConfigurationNode irc=conf.getNode("IRC");

		this.irc_admin_channel=irc.getString("admin-channel","#aVeryMinecraftAdminChannel");
		this.irc_channel_join_message=irc.getString("channel-join-message","Beep boop I am bot");
		this.irc_char_limit=irc.getInt("char-limit",390);
		this.irc_debug_spam=irc.getBoolean("debug-spam", false);
		this.irc_echo_messages=irc.getBoolean("echo-messages", true);
		this.irc_enable=irc.getBoolean("enable",false);
		this.irc_gamesurge_pass=irc.getString("gamesurge-pass","");
		this.irc_gamesurge_user=irc.getString("gamesurge-user","");
		this.irc_host=irc.getString("host","localhost");
		this.irc_ingame_color=ChatColor.getByCode(irc.getInt("ingame-color",11));
		this.irc_ingame_separator=irc.getString("ingame-separator", "<,>").split(",");
		this.irc_level2_commands=irc.getString("level2-commands", "").split(",");
		this.irc_nick=irc.getString("nick","iAmBot");
		this.irc_port=irc.getInt("port",6667);
		this.irc_relay_channel=irc.getString("relay-channel","#aVeryMinecraftRelay");
		this.irc_require_msg_cmd=irc.getBoolean("require-msg-cmd",true);

		ConfigurationNode mysql=conf.getNode("MySQL");

		this.mysql_database=mysql.getString("database","jdbc:mysql://localhost:3306/minecraft");
		this.mysql_password=mysql.getString("password","root");
		this.mysql_username=mysql.getString("username","root");

		ConfigurationNode blacklists=conf.getNode("Blacklists");

		String bl_trust=blacklists.getString("prevent-trusted","0");
		for(String s:bl_trust.split(",")){
			if(s!=null){
				this.blacklist_prevent_trusted.add(Integer.valueOf(s));
			}
		}
		String bl_reg=blacklists.getString("prevent-general","0");
		for(String s:bl_reg.split(",")){
			if(s!=null){
				this.blacklist_prevent_general.add(Integer.valueOf(s));
			}
		}
		String bl_watch=blacklists.getString("watchlist","0");
		for(String s:bl_watch.split(",")){
			if(s!=null){
				this.blacklist_watchlist.add(Integer.valueOf(s));
			}
		}
		String bl_summon=blacklists.getString("prevent-summon","0");
		for(String s:bl_summon.split(",")){
			if(s!=null){
				blacklist_prevent_summon.add(Integer.valueOf(s));
			}
		}

		ConfigurationNode tips=conf.getNode("Tips");

		this.tips_color=ChatColor.getByCode(tips.getInt("color", 11));
		this.tips_delay=tips.getInt("delay", 120);

		ConfigurationNode maintenance=conf.getNode("Maintenance");

		this.maintenance_enable=maintenance.getBoolean("enable", false);
		this.maintenance_message=maintenance.getString("message","Server offline for maintenance");
	}

	public String irc_relay_channel;
	public int irc_port;
	public ChatColor irc_ingame_color;
	public String irc_host;
	public boolean irc_enable;
	public String irc_gamesurge_user;
	public String irc_admin_channel;
	public String[] irc_ingame_separator;
	public String irc_channel_join_message;
	public String irc_gamesurge_pass;
	public String irc_nick;
	public int irc_char_limit;
	public boolean irc_echo_messages;
	public boolean irc_require_msg_cmd;
	public boolean irc_debug_spam;
	public String[] irc_level2_commands;

	public String mysql_username;
	public String mysql_password;
	public String mysql_database;

	public boolean general_random_namecolor;
	public boolean general_allow_explosions;
	public boolean general_debug_mode;
	public Location general_jail_xyzpy;
	public int general_server_number;
	public boolean general_disable_wolves;
	public int general_max_players;
	public String general_mcbans_api;
	public boolean general_safemode;
	public boolean general_block_nontrusted;

	public ArrayList<Integer> blacklist_prevent_summon;
	public ArrayList<Integer> blacklist_watchlist;
	public ArrayList<Integer> blacklist_prevent_trusted;
	public ArrayList<Integer> blacklist_prevent_general;

	public ChatColor tips_color;
	public int tips_delay;

	public String maintenance_message;
	public boolean maintenance_enable;


}
