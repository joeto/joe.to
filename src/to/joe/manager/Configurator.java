package to.joe.manager;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import to.joe.J2;

/**
 * Configuration handler. Not yet implemented
 *
 */
public class Configurator {

	private J2 j2;
	Configuration conf;
	public Configurator(J2 j2){
		this.j2=j2;
		this.conf=this.j2.getConfiguration();
	}
	/**
	 * Create a fresh configuration.
	 */
	public void newConf(){
		HashMap<String,Object> conf_general=new HashMap<String,Object>();
		HashMap<String,Object> conf_mysql=new HashMap<String,Object>();
		HashMap<String,Object> conf_irc=new HashMap<String,Object>();
		HashMap<String,Object> conf_tips=new HashMap<String,Object>();
		HashMap<String,Object> conf_maint=new HashMap<String,Object>();
		HashMap<String,Object> conf_blacklists=new HashMap<String,Object>();
		conf_general.put("debug-mode", false);
		conf_mysql.put("username", "root");
		conf_mysql.put("database", "minecraft");
		conf_mysql.put("password", "root");
		conf_general.put("server-number", 0);
		conf_general.put("max-players", 30);
		conf_tips.put("delay", 120);
		conf_tips.put("color", 12);
		conf_irc.put("host", "localhost");
		conf_irc.put("nick", "aMinecraftBot");
		conf_irc.put("relay-channel", "#minecraftServ");
		conf_irc.put("admin-channel", "#minecraftServA");
		conf_irc.put("ingame-color", 12);
		conf_irc.put("ingame-separator", "<,>");
		conf_irc.put("char-limit", 390);
		conf_irc.put("require-msg-cmd", false);
		conf_irc.put("enable", false);
		conf_irc.put("echo-messages", false);
		conf_irc.put("port", 6667);
		conf_irc.put("debug-spam", false);
		conf_irc.put("channel-join-message", "Hi durr");
		conf_irc.put("gamesurge-user", "");
		conf_irc.put("gamesurge-pass", "");
		conf_irc.put("level2-commands", "kick");
		conf_general.put("safemode", false);
		conf_general.put("allow-explosions", true);
		conf_general.put("disable-wolves", false);
		conf_maint.put("enable", false);
		conf_maint.put("message", "We will be back");
		conf_general.put("block-nontrusted", false);
		conf_general.put("random-namecolor", false);
		conf_blacklists.put("prevent-trusted", "0");
		conf_blacklists.put("prevent-general", "0");
		conf_blacklists.put("watchlist", "0");
		conf_blacklists.put("prevent-summon", "0");
		conf_general.put("mcbans-api", "");
		conf_general.put("mcbouncerapi", "");
		conf_general.put("jail-xyzpy", "10,10,10,0,0");
		conf.setProperty("General", conf_general);
		conf.setProperty("MySQL", conf_mysql);
		conf.setProperty("IRC", conf_irc);
		conf.setProperty("Maintenance", conf_maint);
		conf.setProperty("Tips", conf_tips);
		conf.setProperty("Blacklists", conf_blacklists);
		conf.save();
	}
	/**
	 * Loads the config
	 */
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
		this.general_mcbouncer_api=general.getString("mcbouncer-api","");
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
	public String general_mcbouncer_api;
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
