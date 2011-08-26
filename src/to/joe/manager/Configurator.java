package to.joe.manager;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.util.config.Configuration;

import to.joe.J2;

/**
 * Configuration handler. Not yet implemented
 * 
 */
public class Configurator {

    private final J2 j2;

    public Configurator(J2 j2) {
        this.j2 = j2;
    }


    /**
     * Loads the config
     */
    public void load() {
        Configuration conf = this.j2.getConfiguration();
        conf.load();

        this.access_block_nontrusted = conf.getBoolean("Access.block-nontrusted", false);
        this.access_max_players = conf.getInt("Access.max-players", 30);
        
        this.world_weather_enable = conf.getBoolean("World.weather", false);
        this.world_disable_wolves = conf.getBoolean("World.disable-wolves", false);
        this.world_allow_explosions = conf.getBoolean("World.allow-explosions", true);
        this.world_safemode = conf.getBoolean("World.safemode", false);
        
        this.bans_mcbans_api = conf.getString("Bans.mcbans-api", "");
        this.bans_mcbouncer_api = conf.getString("Bans.mcbouncer-api", "");
        
        this.general_website_enable = conf.getBoolean("General.website", false);
        this.general_debug_mode = conf.getBoolean("General.debug-mode", false);
        final String jail = conf.getString("General.jail-xyzpy", "1,1,1,1,1");
        final String[] jail_split = jail.split(",");
        this.general_jail_xyzpy = new Location(this.j2.getServer().getWorld("world"), Integer.valueOf(jail_split[0]), Integer.valueOf(jail_split[0]), Integer.valueOf(jail_split[0]), Integer.valueOf(jail_split[0]), Integer.valueOf(jail_split[0]));
        this.general_random_namecolor = conf.getBoolean("General.random-namecolor", false);
        this.general_server_number = conf.getInt("General.server-number", 0);
        

        this.irc_admin_channel = conf.getString("IRC.Channels.admin", "#aVeryMinecraftAdminChannel");
        this.irc_relay_channel = conf.getString("IRC.Channels.relay", "#aVeryMinecraftRelay");
        
        this.irc_host = conf.getString("IRC.Connect.host", "localhost");
        this.irc_nick = conf.getString("IRC.Connect.nick", "iAmBot");
        this.irc_port = conf.getInt("IRC.Connect.port", 6667);
        
        this.irc_gamesurge_pass = conf.getString("IRC.GameSurge.pass", "");
        this.irc_gamesurge_user = conf.getString("IRC.GameSurge.user", "");
        
        this.irc_channel_join_message = conf.getString("IRC.Setting.channel-join-message", "Beep boop I am bot");
        this.irc_char_limit = conf.getInt("IRC.Setting.char-limit", 390);
        this.irc_debug_spam = conf.getBoolean("IRC.Setting.debug-spam", false);
        this.irc_echo_messages = conf.getBoolean("IRC.Setting.echo-messages", true);
        this.irc_enable = conf.getBoolean("IRC.enable", false);
        this.irc_ingame_color = ChatColor.getByCode(conf.getInt("IRC.Setting.ingame-color", 11));
        this.irc_ingame_separator = conf.getString("IRC.Setting.ingame-separator", "<,>").split(",");
        this.irc_require_msg_cmd = conf.getBoolean("IRC.Setting.require-msg-cmd", true);

        this.mysql_database = conf.getString("MySQL.database", "jdbc:mysql://localhost:3306/minecraft");
        this.mysql_password = conf.getString("MySQL.password", "root");
        this.mysql_username = conf.getString("MySQL.username", "root");

        final String bl_trust = conf.getString("Blacklists.prevent-trusted", "0");
        for (final String s : bl_trust.split(",")) {
            if (s != null) {
                this.blacklist_prevent_trusted.add(Integer.valueOf(s));
            }
        }
        final String bl_reg = conf.getString("Blacklists.prevent-general", "0");
        for (final String s : bl_reg.split(",")) {
            if (s != null) {
                this.blacklist_prevent_general.add(Integer.valueOf(s));
            }
        }
        final String bl_watch = conf.getString("Blacklists.watchlist", "0");
        for (final String s : bl_watch.split(",")) {
            if (s != null) {
                this.blacklist_watchlist.add(Integer.valueOf(s));
            }
        }
        final String bl_summon = conf.getString("Blacklists.prevent-summon", "0");
        for (final String s : bl_summon.split(",")) {
            if (s != null) {
                this.blacklist_summon.add(Integer.valueOf(s));
            }
        }

        this.maintenance_enable = conf.getBoolean("Access.Maintenance.enable", false);
        this.maintenance_message = conf.getString("Access.Maintenance.message", "Server offline for maintenance");
        
        conf.save();
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

    public String mysql_username;
    public String mysql_password;
    public String mysql_database;

    public boolean general_random_namecolor;
    public boolean world_allow_explosions;
    public boolean general_debug_mode;
    public Location general_jail_xyzpy;
    public int general_server_number;
    public boolean world_disable_wolves;
    public int access_max_players;
    public String bans_mcbans_api;
    public String bans_mcbouncer_api;
    public boolean world_safemode;
    public boolean access_block_nontrusted;
    public boolean world_weather_enable;
    public boolean general_website_enable;

    public ArrayList<Integer> blacklist_summon;
    public ArrayList<Integer> blacklist_watchlist;
    public ArrayList<Integer> blacklist_prevent_trusted;
    public ArrayList<Integer> blacklist_prevent_general;

    public String maintenance_message;
    public boolean maintenance_enable;

}
