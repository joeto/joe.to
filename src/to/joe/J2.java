/*
 * j2Plugin
 * A bunch of fun features, put together for joe.to
 */

package to.joe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import jline.ANSIBuffer.ANSICodes;
import jline.ConsoleReader;
import jline.Terminal;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import to.joe.Commands.AmITrustedCommand;
import to.joe.Commands.FlexCommand;
import to.joe.Commands.GetLocationCommand;
import to.joe.Commands.MeCommand;
import to.joe.Commands.MessageCommand;
import to.joe.Commands.NoteCommand;
import to.joe.Commands.RemoveHomeCommand;
import to.joe.Commands.RemoveItemCommand;
import to.joe.Commands.ReportCommand;
import to.joe.Commands.TrustCommand;
import to.joe.Commands.TrustedRequestCommand;
import to.joe.Commands.VoteCommand;
import to.joe.Commands.Admin.AddBanCommand;
import to.joe.Commands.Admin.AdminChatCommand;
import to.joe.Commands.Admin.AdminGlobalChatCommand;
import to.joe.Commands.Admin.AuthCommand;
import to.joe.Commands.Admin.BanCommand;
import to.joe.Commands.Admin.CoordinateTeleportCommand;
import to.joe.Commands.Admin.GetFlagsCommand;
import to.joe.Commands.Admin.GetGroupCommand;
import to.joe.Commands.Admin.GodmodeCommand;
import to.joe.Commands.Admin.HarassCommand;
import to.joe.Commands.Admin.HatCommand;
import to.joe.Commands.Admin.HomeInvasionCommand;
import to.joe.Commands.Admin.IPLookupCommand;
import to.joe.Commands.Admin.ImAToolCommand;
import to.joe.Commands.Admin.J2LookupCommand;
import to.joe.Commands.Admin.KickCommand;
import to.joe.Commands.Admin.LookupCommand;
import to.joe.Commands.Admin.MuteAllCommand;
import to.joe.Commands.Admin.MuteCommand;
import to.joe.Commands.Admin.NSACommand;
import to.joe.Commands.Admin.RemoveWarpCommand;
import to.joe.Commands.Admin.ReportHandlingCommand;
import to.joe.Commands.Admin.SetWarpCommand;
import to.joe.Commands.Admin.ShushCommand;
import to.joe.Commands.Admin.SlapCommand;
import to.joe.Commands.Admin.SlayCommand;
import to.joe.Commands.Admin.SmiteCommand;
import to.joe.Commands.Admin.StormCommand;
import to.joe.Commands.Admin.TeleportHereCommand;
import to.joe.Commands.Admin.ThorCommand;
import to.joe.Commands.Admin.TimeCommand;
import to.joe.Commands.Admin.UnBanCommand;
import to.joe.Commands.Admin.VanishCommand;
import to.joe.Commands.Admin.WhereIsPlayerCommand;
import to.joe.Commands.Admin.WoofCommand;
import to.joe.Commands.Fun.ClearInventoryCommand;
import to.joe.Commands.Fun.HomeCommand;
import to.joe.Commands.Fun.ItemCommand;
import to.joe.Commands.Fun.ProtectMeCommand;
import to.joe.Commands.Fun.SetHomeCommand;
import to.joe.Commands.Fun.SpawnCommand;
import to.joe.Commands.Fun.StationCommand;
import to.joe.Commands.Fun.TeleportCommand;
import to.joe.Commands.Fun.WarpCommand;
import to.joe.Commands.Info.BlacklistCommand;
import to.joe.Commands.Info.HelpCommand;
import to.joe.Commands.Info.IntroCommand;
import to.joe.Commands.Info.MOTDCommand;
import to.joe.Commands.Info.PlayerListCommand;
import to.joe.Commands.Info.RulesCommand;
import to.joe.Commands.SeniorStaff.FlagsCommand;
import to.joe.Commands.SeniorStaff.J2ReloadCommand;
import to.joe.Commands.SeniorStaff.IRCMessageCommand;
import to.joe.Commands.SeniorStaff.IRCAdminReloadCommand;
import to.joe.Commands.SeniorStaff.KickAllCommand;
import to.joe.Commands.SeniorStaff.MadagascarCommand;
import to.joe.Commands.SeniorStaff.MaintenanceCommand;
import to.joe.Commands.SeniorStaff.MaxPlayersCommand;
import to.joe.Commands.SeniorStaff.MobCommand;
import to.joe.Commands.SeniorStaff.SayCommand;
import to.joe.Commands.SeniorStaff.SetSpawnCommand;
import to.joe.Commands.SeniorStaff.ShowerCommand;
import to.joe.Commands.SeniorStaff.SmackIRCCommand;
import to.joe.listener.BlockAll;
import to.joe.listener.EntityAll;
import to.joe.listener.MapAll;
import to.joe.listener.PlayerChat;
import to.joe.listener.PlayerInteract;
import to.joe.listener.PlayerJoinQuit;
import to.joe.listener.PlayerMovement;
import to.joe.listener.WeatherAll;
import to.joe.manager.ActivityTracker;
import to.joe.manager.BanCooperative;
import to.joe.manager.Chats;
import to.joe.manager.Configurator;
import to.joe.manager.CraftualHarassmentPanda;
import to.joe.manager.Damages;
import to.joe.manager.IPTracker;
import to.joe.manager.IRC;
import to.joe.manager.Jailer;
import to.joe.manager.KicksBans;
import to.joe.manager.Minitrue;
import to.joe.manager.MoveTracker;
import to.joe.manager.MySQL;
import to.joe.manager.Permissions;
import to.joe.manager.Recipes;
import to.joe.manager.Reports;
import to.joe.manager.Users;
import to.joe.manager.Voting;
import to.joe.manager.Warps;
import to.joe.manager.WebPage;
import to.joe.util.Flag;
import to.joe.util.MCLogFilter;
import to.joe.util.Property;
import to.joe.util.User;
import to.joe.util.Runnables.AutoSave;

/**
 * J2 Plugin, on Bukkit
 * 
 */
public class J2 extends JavaPlugin {
    private final BlockAll blockListener = new BlockAll(this);
    private final EntityAll entityListener = new EntityAll(this);
    private final MapAll mapListener = new MapAll(this);
    private final PlayerChat playerListenerChat = new PlayerChat(this);
    private final PlayerInteract playerListenerInteract = new PlayerInteract(this);
    private final PlayerJoinQuit playerListenerJoinQuit = new PlayerJoinQuit(this);
    private final PlayerMovement playerListenerMovement = new PlayerMovement(this);
    private final WeatherAll weatherListener = new WeatherAll(this);
    /**
     * Chat manager
     */
    public final Chats chat = new Chats(this);
    /**
     * IRC manager
     */
    public final IRC irc = new IRC(this);
    /**
     * Kick/ban manager
     */
    public final KicksBans kickbans = new KicksBans(this);
    /**
     * User manager
     */
    public final Users users = new Users(this);
    /**
     * Report manager
     */
    public final Reports reports = new Reports(this);
    /**
     * Warp manager
     */
    public final Warps warps = new Warps(this);
    /**
     * Webpage manager
     */
    public final WebPage webpage = new WebPage(this);
    /**
     * IP Tracking manager
     */
    public final IPTracker ip = new IPTracker(this);
    /**
     * Ban cooperative manager
     */
    public final BanCooperative banCoop = new BanCooperative(this);
    /**
     * Damage manager
     */
    public final Damages damage = new Damages(this);
    /**
     * Permission manager
     */
    public final Permissions perms = new Permissions(this);
    /**
     * Recipe implementer
     */
    public final Recipes recipes = new Recipes(this);
    /**
     * Ministry of Truth
     */
    public final Minitrue minitrue = new Minitrue(this);
    /**
     * Jail manager
     */
    public final Jailer jail = new Jailer(this);
    /**
     * Movement tracker
     */
    public final MoveTracker move = new MoveTracker(this);
    /**
     * Activity tracker
     */
    public final ActivityTracker activity = new ActivityTracker(this);
    /**
     * Craftual Harassment Panda
     */
    public final CraftualHarassmentPanda panda = new CraftualHarassmentPanda(this);
    /**
     * Vote manager
     */
    public final Voting voting = new Voting(this);
    /**
     * MySQL stuffs
     */
    public MySQL mysql;
    /**
     * Configuration
     */
    public Configurator config=new Configurator(this);

    /*
     * (non-Javadoc)
     * 
     * @see org.bukkit.plugin.Plugin#onDisable()
     */
    @Override
    public void onDisable() {
        this.irc.kill();
        this.stopTimer();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bukkit.plugin.Plugin#onEnable()
     */
    @Override
    public void onEnable() {
        this.initLog();
        this.log = Logger.getLogger("Minecraft");
        this.log.setFilter(new MCLogFilter());
        this.protectedUsers = new ArrayList<String>();
        this.loadData();
        this.debug("Data loaded");
        // irc start
        if (this.config.irc_enable) {
            this.irc.connectAndAuth();
        }
        this.irc.startIRCTimer();
        this.debug("IRC up (or disabled)");
        // irc end
        this.loadTips();
        this.debug("Tips loaded");
        this.startTipsTimer();
        this.debug("Tips timer started");

        // Register our events
        final PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_CHAT, this.playerListenerChat, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, this.playerListenerChat, Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_QUIT, this.playerListenerJoinQuit, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_INTERACT, this.playerListenerInteract, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_PICKUP_ITEM, this.playerListenerInteract, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_CANBUILD, this.blockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_BREAK, this.blockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_PLACE, this.blockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_IGNITE, this.blockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_BURN, this.blockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.SIGN_CHANGE, this.blockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.ENTITY_EXPLODE, this.entityListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.ENTITY_TARGET, this.entityListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.ENTITY_DAMAGE, this.entityListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.ENTITY_DEATH, this.entityListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_PRELOGIN, this.playerListenerJoinQuit, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_QUIT, this.playerListenerJoinQuit, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_JOIN, this.playerListenerJoinQuit, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_KICK, this.playerListenerJoinQuit, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_TELEPORT, this.playerListenerMovement, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_MOVE, this.playerListenerMovement, Priority.Normal, this);
        pm.registerEvent(Event.Type.CREATURE_SPAWN, this.entityListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.ITEM_SPAWN, this.entityListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.MAP_INITIALIZE, this.mapListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.WEATHER_CHANGE, this.weatherListener, Priority.Normal, this);
            
        this.debug("Events registered");
        
        this.getCommand("kickall").setExecutor(new KickAllCommand(this));
        this.getCommand("smackirc").setExecutor(new SmackIRCCommand(this));
        this.getCommand("blacklist").setExecutor(new BlacklistCommand(this));
        this.getCommand("help").setExecutor(new HelpCommand(this));
        this.getCommand("intro").setExecutor(new IntroCommand(this));
        this.getCommand("motd").setExecutor(new MOTDCommand(this));
        this.getCommand("rules").setExecutor(new RulesCommand(this));
        this.getCommand("protectme").setExecutor(new ProtectMeCommand(this));
        this.getCommand("tp").setExecutor(new TeleportCommand(this));
        this.getCommand("tphere").setExecutor(new TeleportHereCommand(this));
        this.getCommand("spawn").setExecutor(new SpawnCommand(this));
        this.getCommand("msg").setExecutor(new MessageCommand(this));
        this.getCommand("tell").setExecutor(new MessageCommand(this));
        this.getCommand("i").setExecutor(new ItemCommand(this));
        this.getCommand("item").setExecutor(new ItemCommand(this));
        this.getCommand("time").setExecutor(new TimeCommand(this));
        this.getCommand("who").setExecutor(new PlayerListCommand(this));
        this.getCommand("playerlist").setExecutor(new PlayerListCommand(this));
        this.getCommand("list").setExecutor(new PlayerListCommand(this));
        this.getCommand("a").setExecutor(new AdminChatCommand(this));
        this.getCommand("report").setExecutor(new ReportCommand(this));
        this.getCommand("r").setExecutor(new ReportHandlingCommand(this));
        this.getCommand("g").setExecutor(new AdminGlobalChatCommand(this));
        this.getCommand("ban").setExecutor(new BanCommand(this));
        this.getCommand("b").setExecutor(new BanCommand(this));
        this.getCommand("kick").setExecutor(new KickCommand(this));
        this.getCommand("k").setExecutor(new KickCommand(this));
        this.getCommand("addban").setExecutor(new AddBanCommand(this));
        this.getCommand("unban").setExecutor(new UnBanCommand(this));
        this.getCommand("pardon").setExecutor(new UnBanCommand(this));
        this.getCommand("trust").setExecutor(new TrustCommand(this));
        this.getCommand("getflags").setExecutor(new GetFlagsCommand(this));
        this.getCommand("getgroup").setExecutor(new GetGroupCommand(this));
        this.getCommand("me").setExecutor(new MeCommand(this));
        this.getCommand("ircadminreload").setExecutor(new IRCAdminReloadCommand(this));
        this.getCommand("j2reload").setExecutor(new J2ReloadCommand(this));
        this.getCommand("maintenance").setExecutor(new MaintenanceCommand(this));
        this.getCommand("flags").setExecutor(new FlagsCommand(this));
        this.getCommand("warp").setExecutor(new WarpCommand(this));
        this.getCommand("home").setExecutor(new HomeCommand(this));
        this.getCommand("setwarp").setExecutor(new SetWarpCommand(this));
        this.getCommand("sethome").setExecutor(new SetHomeCommand(this));
        this.getCommand("removewarp").setExecutor(new RemoveWarpCommand(this));
        this.getCommand("removehome").setExecutor(new RemoveHomeCommand(this));
        this.getCommand("homeinvasion").setExecutor(new HomeInvasionCommand(this));
        this.getCommand("hi").setExecutor(new HomeInvasionCommand(this));
        this.getCommand("clearinventory").setExecutor(new ClearInventoryCommand(this));
        this.getCommand("ci").setExecutor(new ClearInventoryCommand(this));
        this.getCommand("removeitem").setExecutor(new RemoveItemCommand(this));
        this.getCommand("ri").setExecutor(new RemoveItemCommand(this));
        this.getCommand("mob").setExecutor(new MobCommand(this));
        this.getCommand("kibbles").setExecutor(new GodmodeCommand(this));
        this.getCommand("bits").setExecutor(new GodmodeCommand(this));
        this.getCommand("coo").setExecutor(new CoordinateTeleportCommand(this));
        this.getCommand("whereis").setExecutor(new WhereIsPlayerCommand(this));
        this.getCommand("madagascar").setExecutor(new MadagascarCommand(this));
        this.getCommand("lookup").setExecutor(new LookupCommand(this));
        this.getCommand("j2lookup").setExecutor(new J2LookupCommand(this));
        this.getCommand("iplookup").setExecutor(new IPLookupCommand(this));
        this.getCommand("smite").setExecutor(new SmiteCommand(this));
        this.getCommand("storm").setExecutor(new StormCommand(this));
        this.getCommand("ircmsg").setExecutor(new IRCMessageCommand(this));
        this.getCommand("flex").setExecutor(new FlexCommand(this));
        this.getCommand("thor").setExecutor(new ThorCommand(this));
        this.getCommand("slay").setExecutor(new SlayCommand(this));
        this.getCommand("amitrusted").setExecutor(new AmITrustedCommand(this));
        this.getCommand("vanish").setExecutor(new VanishCommand(this));
        this.getCommand("imatool").setExecutor(new ImAToolCommand(this));
        this.getCommand("f3").setExecutor(new GetLocationCommand(this));
        this.getCommand("loc").setExecutor(new GetLocationCommand(this));
        this.getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
        this.getCommand("auth").setExecutor(new AuthCommand(this));
        this.getCommand("harass").setExecutor(new HarassCommand(this));
        this.getCommand("muteall").setExecutor(new MuteAllCommand(this));
        this.getCommand("mute").setExecutor(new MuteCommand(this));
        this.getCommand("say").setExecutor(new SayCommand(this));
        this.getCommand("nsa").setExecutor(new NSACommand(this));
        this.getCommand("station").setExecutor(new StationCommand(this));
        this.getCommand("vote").setExecutor(new VoteCommand(this));
        this.getCommand("maxplayers").setExecutor(new MaxPlayersCommand(this));
        this.getCommand("shush").setExecutor(new ShushCommand(this));
        this.getCommand("hat").setExecutor(new HatCommand(this));
        this.getCommand("note").setExecutor(new NoteCommand(this));
        this.getCommand("anote").setExecutor(new NoteCommand(this));
        this.getCommand("trustreq").setExecutor(new TrustedRequestCommand(this));
        this.getCommand("woof").setExecutor(new WoofCommand(this));
        this.getCommand("slap").setExecutor(new SlapCommand(this));
        this.getCommand("shower").setExecutor(new ShowerCommand(this));
        final PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
        this.webpage.go(this.config.general_server_number);
        this.recipes.addRecipes();
        this.minitrue.restartManager();
        this.activity.restartManager();
        this.banCoop.startCallback();
        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new AutoSave(this), 1, 6000);
    }

    /**
     * Load a butt-ton of data for startup.
     */
    public void loadData() {
        this.rules = this.readDaFile("rules.txt");
        this.blacklist = this.readDaFile("blacklistinfo.txt");
        this.intro = this.readDaFile("intro.txt");
        this.motd = this.readDaFile("motd.txt");
        this.help = this.readDaFile("help.txt");
        this.config.load();

        this.mysql = new MySQL(this.config.mysql_username, this.config.mysql_password, this.config.mysql_database, this.config.general_server_number, this);
        this.warps.restartManager();
        this.reports.restartManager();
        this.users.restartGroups();
        this.mysql.loadMySQLData();
        if (this.config.world_safemode) {
            final Player[] online = this.getServer().getOnlinePlayers();
            if (online.length > 0) {
                for (final Player p : online) {
                    if (p != null) {
                        this.damage.protect(p.getName());
                    }
                }
            }
        } else {
            this.damage.clear();
        }
        if (this.config.world_safemode) {
            final Player[] online = this.getServer().getOnlinePlayers();
            if (online.length > 0) {
                for (final Player p : online) {
                    if (p != null) {
                        this.damage.protect(p.getName());
                    }
                }
            }
        } else {
            this.damage.clear();
        }
        this.perms.load();
    }

    /**
     * Read named file
     * 
     * @param filename
     * @return array of lines
     */
    public String[] readDaFile(String filename) {

        FileReader fileReader = null;
        try {
            fileReader = new FileReader(filename);
        } catch (final FileNotFoundException e2) {
            // e2.printStackTrace();
            this.log.severe("File not found: " + filename);
            final String[] uhOh = new String[1];
            uhOh[0] = "";
            return uhOh;
        }
        final BufferedReader rulesBuffer = new BufferedReader(fileReader);
        final List<String> fileLines = new ArrayList<String>();
        String line = null;
        try {
            while ((line = rulesBuffer.readLine()) != null) {
                fileLines.add(line);
            }
        } catch (final IOException e1) {
            e1.printStackTrace();
        }
        try {
            rulesBuffer.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return fileLines.toArray(new String[fileLines.size()]);
    }

    // tips
    private void startTipsTimer() {
        this.tips_stopTimer = false;
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (J2.this.tips_stopTimer) {
                    timer.cancel();
                    return;
                }
                J2.this.broadcastTip();
            }
        }, 3000, 120 * 1000);
    }

    private void stopTimer() {
        this.tips_stopTimer = true;
    }

    private void broadcastTip() {
        if (this.tips.isEmpty()) {
            return;
        }
        final String message = ChatColor.AQUA + "[TIP] " + this.tips.get(this.curTipNum);
        this.chat.messageAll(message);
        this.log(message);
        this.curTipNum++;
        if (this.curTipNum >= this.tips.size()) {
            this.curTipNum = 0;
        }
    }

    private void loadTips() {
        this.tips = new ArrayList<String>();
        if (!new File(this.tips_location).exists()) {

            return;
        }
        try {
            final Scanner scanner = new Scanner(new File(this.tips_location));
            while (scanner.hasNextLine()) {
                final String line = scanner.nextLine();
                if (line.startsWith("#") || line.equals("")) {
                    continue;
                }
                this.tips.add(line);
            }
            scanner.close();
        } catch (final Exception e) {
            this.log.log(Level.SEVERE, "Exception while reading " + this.tips_location, e);
            this.stopTimer();
        }
    }

    // end tips

    /**
     * Is the ID on the super-blacklist?
     * 
     * @param id
     * @return
     */
    public boolean isOnSuperBlacklist(int id) {
        return this.config.blacklist_prevent_trusted.contains(Integer.valueOf(id));
    }

    /**
     * Is the ID on the regular blacklist?
     * 
     * @param id
     * @return
     */
    public boolean isOnRegularBlacklist(int id) {
        return this.config.blacklist_prevent_general.contains(Integer.valueOf(id));
    }

    /**
     * Is the ID being watched for summoning?
     * 
     * @param id
     * @return
     */
    public boolean isOnWatchlist(int id) {
        return this.config.blacklist_watchlist.contains(Integer.valueOf(id));
    }

    /**
     * Is the ID being blocked from summoning?
     * 
     * @param id
     * @return
     */
    public boolean isOnSummonlist(int id) {
        return this.config.blacklist_summon.contains(Integer.valueOf(id));
    }

    /**
     * Combine a String array from startIndex with separator
     * 
     * @param startIndex
     * @param string
     * @param seperator
     * @return
     */
    public String combineSplit(int startIndex, String[] string, String seperator) {
        final StringBuilder builder = new StringBuilder();
        for (int i = startIndex; i < string.length; i++) {
            builder.append(string[i]);
            builder.append(seperator);
        }
        builder.deleteCharAt(builder.length() - seperator.length());
        return builder.toString();
    }

    /**
     * Does the user have this flag when authed?
     * 
     * @param playername
     * @param flag
     * @return
     */
    public boolean reallyHasFlag(String playername, Flag flag) {
        final User user = this.users.getUser(playername);
        if (user != null) {
            if (user.getUserFlags().contains(flag) || this.users.groupHasFlag(user.getGroup(), flag)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Does user have this flag currently?
     * 
     * @param playername
     * @param flag
     * @return
     */
    public boolean hasFlag(String playername, Flag flag) {
        final User user = this.users.getUser(playername);
        if (user != null) {
            if ((flag.equals(Flag.ADMIN) || flag.equals(Flag.SRSTAFF)) && !this.users.isAuthed(playername)) {
                return false;
            }
            if (user.getUserFlags().contains(flag) || this.users.groupHasFlag(user.getGroup(), flag)) {
                return true;
            }
        } else {
            final Player player = this.getServer().getPlayer(playername);
            if ((player != null) && player.isOnline()) {
                player.kickPlayer("Rejoin in 10 seconds.");
            }
        }
        return false;
    }

    /**
     * Lazy hasFlag
     * 
     * @param player
     * @param flag
     * @return
     */
    public boolean hasFlag(Player player, Flag flag) {
        return this.hasFlag(player.getName(), flag);
    }

    /**
     * Send jail message to player
     * 
     * @param player
     */
    public void jailMsg(Player player) {
        player.sendMessage(ChatColor.RED + "You are " + ChatColor.DARK_RED + "IN JAIL");
        player.sendMessage(ChatColor.RED + "for violation of our server rules");
        player.sendMessage(ChatColor.RED + "Look around you for info on freedom");
    }

    /**
     * Part of fakeCraftIRC. Send message to named tag.
     * 
     * @param message
     * @param tag
     */
    public void craftIRC_sendMessageToTag(String message, String tag) {
        this.debug("J2: Got message, tag \"" + tag + "\"");
        if (tag.equalsIgnoreCase("nocheat")) {
            this.irc.messageAdmins(message);
            this.debug("J2.2: Got message, tag \"" + tag + "\"");
        }
    }

    /**
     * How many sanitized users does this string match?
     * 
     * @param name
     * @return
     */
    public int playerMatches(String name) {
        final List<Player> list = this.minitrue.matchPlayer(name, true);
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    private final Map<ChatColor, String> ANSI_replacements = new EnumMap<ChatColor, String>(ChatColor.class);
    private final ChatColor[] ANSI_colors = ChatColor.values();
    private Terminal ANSI_terminal;
    private ConsoleReader ANSI_reader;

    private void initLog() {
        this.ANSI_reader = ((CraftServer) this.getServer()).getReader();
        this.ANSI_terminal = this.ANSI_reader.getTerminal();
        this.ANSI_replacements.put(ChatColor.BLACK, ANSICodes.attrib(0));
        this.ANSI_replacements.put(ChatColor.RED, ANSICodes.attrib(31));
        this.ANSI_replacements.put(ChatColor.DARK_RED, ANSICodes.attrib(31));
        this.ANSI_replacements.put(ChatColor.GREEN, ANSICodes.attrib(32));
        this.ANSI_replacements.put(ChatColor.DARK_GREEN, ANSICodes.attrib(32));
        this.ANSI_replacements.put(ChatColor.YELLOW, ANSICodes.attrib(33));
        this.ANSI_replacements.put(ChatColor.GOLD, ANSICodes.attrib(33));
        this.ANSI_replacements.put(ChatColor.BLUE, ANSICodes.attrib(34));
        this.ANSI_replacements.put(ChatColor.DARK_BLUE, ANSICodes.attrib(34));
        this.ANSI_replacements.put(ChatColor.LIGHT_PURPLE, ANSICodes.attrib(35));
        this.ANSI_replacements.put(ChatColor.DARK_PURPLE, ANSICodes.attrib(35));
        this.ANSI_replacements.put(ChatColor.AQUA, ANSICodes.attrib(36));
        this.ANSI_replacements.put(ChatColor.DARK_AQUA, ANSICodes.attrib(36));
        this.ANSI_replacements.put(ChatColor.WHITE, ANSICodes.attrib(37));
    }

    private String logPrep(String message) {
        if (this.ANSI_terminal.isANSISupported()) {
            String result = message;

            for (final ChatColor color : this.ANSI_colors) {
                if (this.ANSI_replacements.containsKey(color)) {
                    result = result.replaceAll(color.toString(), this.ANSI_replacements.get(color));
                } else {
                    result = result.replaceAll(color.toString(), "");
                }
            }
            return result + ANSICodes.attrib(0);
        } else {
            return ChatColor.stripColor(message);
        }
    }

    /**
     * Add string to log, as INFO
     * 
     * @param message
     */
    public void log(String message) {
        this.log.info(this.logPrep(message));
    }

    /**
     * Add string to log, as WARNING
     * 
     * @param message
     */
    public void logWarn(String message) {
        this.log.warning(this.logPrep(message));
    }

    /**
     * If debugging enabled, log message.
     * 
     * @param message
     */
    public void debug(String message) {
        if (this.config.general_debug_mode) {
            this.log(message);
        }
    }

    /**
     * Message admins and the log.
     * 
     * @param message
     */
    public void sendAdminPlusLog(String message) {
        this.chat.messageByFlag(Flag.ADMIN, message);
        this.log(message);
    }

    /**
     * SHUT. DOWN. EVERYTHING.
     * 
     * @param name
     *            Admin shutting down
     */
    public void madagascar(String name) {
        this.sendAdminPlusLog(name + " wants to SHUT. DOWN. EVERYTHING.");
        if (this.config.irc_enable) {
            if (name.equalsIgnoreCase("console")) {
                this.irc.getBot().sendMessage(this.config.irc_admin_channel, "A MAN IN BRAZIL IS COUGHING");
            }
            this.config.irc_enable = false;
            this.irc.getBot().quitServer("SHUT. DOWN. EVERYTHING.");
        }
        this.config.maintenance_enable = true;
        this.kickbans.kickAll("We'll be back after these brief messages");
        this.getServer().dispatchCommand(new ConsoleCommandSender(this.getServer()), "stop");
    }

    /**
     * Safe teleporting, removing players from vehicles
     * 
     * @param player
     * @param location
     */
    public void safePort(Player player, Location location) {
        final Entity vehicle = player.getVehicle();
        if (vehicle != null) {
            player.leaveVehicle();
            vehicle.remove();
        }
        player.teleport(location);
    }

    public SimpleDateFormat shortdateformat = new SimpleDateFormat("yyyy-MM-dd kk:mm");
    private Logger log;
    public ArrayList<String> protectedUsers;
    public String[] rules, blacklist, intro, motd, help;
    private final String tips_location = "tips.txt";
    private boolean tips_stopTimer = false;
    private ArrayList<String> tips;
    private int curTipNum = 0;
    public Property tpProtect = new Property("tpProtect.list");
    public Random random = new Random();
}