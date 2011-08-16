package to.joe.manager;

//import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import to.joe.J2;
import to.joe.util.Ban;
import to.joe.util.Flag;
import to.joe.util.Note;
import to.joe.util.Report;
import to.joe.util.User;
import to.joe.util.Warp;

/**
 * All interactions with SQL
 * 
 */
public class MySQL {
    private final String user, pass, db;
    private final int serverNumber;
    private final J2 j2;
    private final String aliasdb = "alias";

    // private SimpleDateFormat formatter = new SimpleDateFormat("MM-dd hh:mm");
    /**
     * Initializes the MySQL manager
     * 
     * @param User
     * @param Pass
     * @param DB
     * @param ServerNumber
     * @param J2
     */
    public MySQL(String User, String Pass, String DB, int ServerNumber, J2 J2) {
        this.user = User;
        this.pass = Pass;
        this.db = DB;
        this.serverNumber = ServerNumber;
        this.j2 = J2;
    }

    /**
     * Get the SQL connection
     * 
     * @return
     */
    public Connection getConnection() {
        try {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (final ClassNotFoundException ex) {
                this.j2.logWarn(ChatColor.RED + "SQL FAIL D:");
            }
            return DriverManager.getConnection(this.db + "?autoReconnect=true&user=" + this.user + "&password=" + this.pass);
        } catch (final SQLException ex) {
            this.j2.logWarn(ChatColor.RED + "SQL FAIL :(");
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Replace number with color, from storage
     * 
     * @param input
     * @return
     */
    public ChatColor toColor(int input) {
        switch (input) {
            case 0:
                return ChatColor.BLACK;
            case 1:
                return ChatColor.DARK_BLUE;
            case 2:
                return ChatColor.DARK_GREEN;
            case 3:
                return ChatColor.DARK_AQUA;
            case 4:
                return ChatColor.DARK_RED;
            case 5:
                return ChatColor.DARK_PURPLE;
            case 6:
                return ChatColor.GOLD;
            case 7:
                return ChatColor.GRAY;
            case 8:
                return ChatColor.DARK_GRAY;
            case 9:
                return ChatColor.BLUE;
            case 10:
                return ChatColor.GREEN;
            case 11:
                return ChatColor.AQUA;
            case 12:
                return ChatColor.RED;
            case 13:
                return ChatColor.LIGHT_PURPLE;
            case 14:
                return ChatColor.YELLOW;
            case 15:
                return ChatColor.WHITE;
        }
        return null;
    }

    private String stringClean(String toClean) {
        return toClean.replace('\"', '_').replace('\'', '_').replace(';', '_').replace(',', '_');
    }
    /**
     * Trusted Methods
     * - for teh lulz
     * 
     * 
     */
    public boolean isRegistered(String authcode){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = this.getConnection();
            final String state = "SELECT * FROM minecraftusers WHERE minecraft_username <>\"\" AND authcode=\"" + authcode + "\"";
            this.j2.debug("Query: " + state);
            ps = conn.prepareStatement(state);
            rs = ps.executeQuery();
            while (rs.next ())
            {
            	return true;
            }
        }
        catch (final SQLException ex) {
            
        }
        return false;

        }

      
    
        public boolean authCorrect(String authcode){
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                conn = this.getConnection();
                final String state = "SELECT * FROM minecraftusers WHERE authcode=\"" + authcode + "\"";
                this.j2.debug("Query: " + state);
                ps = conn.prepareStatement(state);
                rs = ps.executeQuery();
                while (rs.next ())
                {
                	return true;
                }
            }
             catch (final SQLException ex) {
                
            }
            return false;

            }
        
    public void addLink(String playerName, String authcode){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = this.getConnection();
            final String state = "SELECT * FROM minecraftusers WHERE minecraft_username <> \"\" AND authcode=\"" + authcode + "\"";
            this.j2.debug("Query: " + state);
            ps = conn.prepareStatement(state);
            ps.executeUpdate();
        } catch (final SQLException ex) {
                this.j2.logWarn(ChatColor.RED + "Uh oh! An error occured while linking a forum account with a minecraft account");
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (rs != null) {
                        rs.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                } catch (final SQLException ex) {
                    this.j2.logWarn(ChatColor.RED + "Uh oh! An error occured while linking a forum account with a minecraft account");
                }

            }
    }
    /**
     * Get the User class of a username
     * 
     * @param name
     * @return
     */
    public User getUser(String name) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = this.getConnection();
            final String state = "SELECT * FROM j2users WHERE name=\"" + name + "\"";
            this.j2.debug("Query: " + state);
            ps = conn.prepareStatement(state);
            rs = ps.executeQuery();
            if (rs.next()) {
                final String Flags = rs.getString("flags");
                final ArrayList<Flag> flags = new ArrayList<Flag>();
                for (int x = 0; x < Flags.length(); x++) {
                    flags.add(Flag.byChar(Flags.charAt(x)));
                }
                this.j2.debug("User " + name + " in " + rs.getString("group") + " with " + Flags);
                return new User(name, this.toColor(rs.getInt("color")), rs.getString("group"), flags, this.j2.getServer().getWorld("world"), rs.getString("safeword"));
            } else {
                final String state2 = "INSERT INTO j2users (`name`,`group`,`color`,`flags`) values (\"" + name + "\",\"regular\",10,\"n\")";
                this.j2.debug("Query: " + state2);
                ps = conn.prepareStatement(state2);
                ps.executeUpdate();
                final ArrayList<Flag> f = new ArrayList<Flag>();
                f.add(Flag.NEW);
                return new User(name, ChatColor.GREEN, "regular", f, this.j2.getServer().getWorld("world"), "");
            }
        } catch (final SQLException ex) {
            this.j2.logWarn(ChatColor.RED + "Unable to load user " + name + " from MySQL. Oh hell");
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (final SQLException ex) {
                this.j2.logWarn(ChatColor.RED + "Unable to load user " + name + " from MySQL. Oh hell");
            }
        }
        this.j2.logWarn(ChatColor.RED + "Unable to load user " + name + " from MySQL. Oh hell");
        return null;
    }

    /**
     * Update a user's flags.
     * 
     * @param name
     * @param flags
     */
    public void setFlags(String name, ArrayList<Flag> flags) {
        this.j2.debug("Calling setFlags");
        Connection conn = null;
        PreparedStatement ps = null;
        name = this.stringClean(name);
        String flaglist = "";
        if (!flags.isEmpty()) {
            for (final Flag f : flags) {
                flaglist += f.getChar();
            }
        }
        try {
            conn = this.getConnection();
            final String state = "UPDATE j2users SET flags=\"" + flaglist + "\" WHERE name=\"" + name + "\"";
            this.j2.debug("Query: " + state);
            ps = conn.prepareStatement(state);
            ps.executeUpdate();
        } catch (final SQLException ex) {

        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (final SQLException ex) {
            }
        }
    }

    /**
     * Set a user's group
     * 
     * @param name
     * @param group
     */
    public void setGroup(String name, String group) {
        Connection conn = null;
        PreparedStatement ps = null;
        name = this.stringClean(name);
        group = this.stringClean(group);
        try {
            conn = this.getConnection();
            final String state = "UPDATE j2users SET group=\"" + group + "\" WHERE name=\"" + name + "\"";
            this.j2.debug("Query: " + state);
            ps = conn.prepareStatement(state);
            ps.executeUpdate();
        } catch (final SQLException ex) {

        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (final SQLException ex) {
            }
        }
    }

    /**
     * Ban a user
     * 
     * @param name
     * @param reason
     * @param time
     *            Currently unused. Just set to 0
     * @param admin
     * @param location
     */
    public void ban(String name, String reason, long time, String admin, Location location) {
        this.j2.banCoop.processBan(name, admin, reason);
        this.j2.panda.remove(name);
        if (this.serverNumber == 1) {
            this.j2.users.addFlag(name, Flag.BARRED_MC1);
        }
        Connection conn = null;
        PreparedStatement ps = null;
        double x = 0, y = 0, z = 0;
        float pitch = 0, yaw = 0;
        String world = "";
        if (location != null) {
            x = location.getX();
            y = location.getY();
            z = location.getZ();
            pitch = location.getPitch();
            yaw = location.getYaw();
            world = location.getWorld().getName();
        }
        try {
            conn = this.getConnection();
            final Date curTime = new Date();
            final long timeNow = curTime.getTime() / 1000;
            long unBanTime;
            if (time == 0) {
                unBanTime = 0;
            } else {
                unBanTime = timeNow + (60 * time);
            }
            final String state = "INSERT INTO j2bans (name,reason,admin,unbantime,timeofban,x,y,z,pitch,yaw,world,server) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
            this.j2.debug("Query: " + state);
            ps = conn.prepareStatement(state);
            ps.setString(1, this.stringClean(name.toLowerCase()));
            ps.setString(2, this.stringClean(reason));
            ps.setString(3, this.stringClean(admin));
            ps.setLong(4, unBanTime);
            ps.setLong(5, timeNow);
            ps.setDouble(6, x);
            ps.setDouble(7, y);
            ps.setDouble(8, z);
            ps.setFloat(9, pitch);
            ps.setFloat(10, yaw);
            ps.setString(11, world);
            ps.setInt(12, this.serverNumber);
            ps.executeUpdate();
            final Ban newban = new Ban(name.toLowerCase(), reason, unBanTime, timeNow, timeNow, false);
            this.j2.kickbans.bans.add(newban);

        } catch (final SQLException ex) {

        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (final SQLException ex) {
            }
        }
    }

    /**
     * Acquire latest ban reason if any
     * 
     * @param username
     * @return
     */
    public String checkBans(String username) {
        final Date curTime = new Date();
        final long timeNow = curTime.getTime() / 1000;
        String reason = null;
        final ArrayList<Ban> banhat = new ArrayList<Ban>(this.j2.kickbans.bans);
        for (final Ban ban : banhat) {
            if (ban.isBanned() && ban.isTemp() && (ban.getTimeOfUnban() < timeNow)) {
                // unban(user);
                // tempbans
            }
            if ((ban.getTimeLoaded() > (timeNow - 60)) && ban.getName().equalsIgnoreCase(username) && ban.isBanned()) {
                reason = "Banned: " + ban.getReason();
            }
            if (ban.getTimeLoaded() < (timeNow - 60)) {
                this.j2.kickbans.bans.remove(ban);
            }
        }
        if (reason == null) {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                conn = this.getConnection();
                final String state = "SELECT name,reason,unbantime,timeofban FROM j2bans WHERE unbanned=0 and name=\"" + username + "\"";
                this.j2.debug("Query: " + state);
                ps = conn.prepareStatement(state);
                rs = ps.executeQuery();
                while (rs.next()) {
                    reason = rs.getString("reason");
                    final Ban ban = new Ban(rs.getString("name"), reason, rs.getLong("unbantime"), timeNow, rs.getLong("timeofban"), false);
                    this.j2.kickbans.bans.add(ban);
                    reason = "Banned: " + reason;
                }
            } catch (final SQLException ex) {
                this.j2.logWarn(ChatColor.RED + "Unable to load j2Bans. You're not going to like this.");
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (rs != null) {
                        rs.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                } catch (final SQLException ex) {
                }
            }
        }
        return reason;
    }

    /**
     * Acquire all bans of a username
     * 
     * @param playerName
     * @param allbans
     * @return
     */
    public ArrayList<Ban> getBans(String playerName, boolean allbans) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        final String username = this.stringClean(playerName);
        final ArrayList<Ban> bans = new ArrayList<Ban>();
        try {
            conn = this.getConnection();
            String notallbans = "";
            if (!allbans) {
                notallbans = " and unbanned=0";
            }
            final String state = "SELECT name,reason,timeofban,unbantime,unbanned FROM j2bans WHERE name=\"" + username + "\"" + notallbans;
            this.j2.debug("Query: " + state);
            ps = conn.prepareStatement(state);
            rs = ps.executeQuery();
            while (rs.next()) {
                bans.add(new Ban(rs.getString("name"), rs.getString("reason"), rs.getLong("unbantime"), 0, rs.getLong("timeofban"), rs.getBoolean("unbanned")));
            }
        } catch (final SQLException ex) {
            this.j2.logWarn(ChatColor.RED + "Unable to load j2Bans. You're not going to like this.");
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (final SQLException ex) {
            }
        }
        return bans;
    }

    /**
     * Unban a user.
     * 
     * @param name
     */
    public void unban(String name) {
        Connection conn = null;
        PreparedStatement ps = null;
        final String clean_name = this.stringClean(name);
        try {
            conn = this.j2.mysql.getConnection();

            for (final Ban ban : this.j2.kickbans.bans) {
                if (ban.getName().equalsIgnoreCase(clean_name)) {
                    ban.unBan();
                }
            }
            final String state = "UPDATE j2bans SET unbanned=1 WHERE name=\"" + clean_name.toLowerCase() + "\"";
            this.j2.debug("Query: " + state);
            ps = conn.prepareStatement(state);
            ps.executeUpdate();
        } catch (final SQLException ex) {

        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (final SQLException ex) {
            }
        }
    }

    /**
     * Loads groups, reports, warps
     */
    public void loadMySQLData() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = this.getConnection();
            final HashMap<String, ArrayList<Flag>> groups = new HashMap<String, ArrayList<Flag>>();
            final String state = "SELECT name,flags FROM j2groups where server=" + this.serverNumber;
            this.j2.debug("Query: " + state);
            ps = conn.prepareStatement(state);
            rs = ps.executeQuery();
            while (rs.next()) {
                final String name = rs.getString("name");

                final String Flags = rs.getString("flags");
                this.j2.log("Group: " + name + " with flags " + Flags);
                final ArrayList<Flag> flags = new ArrayList<Flag>();
                for (int x = 0; x < Flags.length(); x++) {
                    flags.add(Flag.byChar(Flags.charAt(x)));
                }
                groups.put(name, flags);
            }
            this.j2.debug("Loaded " + groups.size() + " groups");
            this.j2.users.setGroups(groups);

            // reports
            final String state2 = "SELECT id,user,x,y,z,pitch,yaw,message,world,time,closed from reports where server=" + this.serverNumber + " and closed=0";
            ps = conn.prepareStatement(state2);
            this.j2.debug("Query: " + state2);
            rs = ps.executeQuery();
            while (rs.next()) {
                final String user = rs.getString("user");
                final Location loc = new Location(this.j2.getServer().getWorld(rs.getString("world")), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"), rs.getFloat("pitch"), rs.getFloat("yaw"));
                this.j2.reports.addReportViaSQL(new Report(rs.getInt("id"), loc, user, rs.getString("message"), rs.getLong("time"), rs.getBoolean("closed")));
                this.j2.debug("Adding new report to list, user " + user);
            }

            // warps

            final String state3 = "SELECT * FROM warps where server=" + this.serverNumber + " and flag!=\"w\"";
            this.j2.debug("Query: " + state3);
            ps = conn.prepareStatement(state3);
            rs = ps.executeQuery();
            int count = 0;
            while (rs.next()) {
                this.j2.warps.addWarpInternal(new Warp(rs.getString("name"), rs.getString("player"), new Location(this.j2.getServer().getWorld(rs.getString("world")), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"), rs.getFloat("pitch"), rs.getFloat("yaw")), Flag.byChar(rs.getString("flag").charAt(0))));
                count++;
            }
            this.j2.debug("Loaded " + count + " warps");

            // jailing

            /*
             * String
             * state4="SELECT user,reason from jail where server="+serverNumber
             * +" and free=0"; ps = conn.prepareStatement(state4);
             * j2.debug("Query: "+state4); rs = ps.executeQuery();
             * HashMap<String,String> tempjail=new HashMap<String,String>();
             * while (rs.next()){ tempjail.put(rs.getString("user"),
             * rs.getString("reason")); }
             * j2.log.info("Loaded "+tempjail.size()+" jailings");
             * j2.users.jailSet(tempjail);
             */

        } catch (final SQLException ex) {
            this.j2.logWarn(ChatColor.RED + "Unable to load base data from MySQL. Oh hell");
            this.j2.maintenance = true;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (final SQLException ex) {
            }
        }
    }

    /**
     * Add a report
     * 
     * @param report
     */
    public void addReport(Report report) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = this.getConnection();
            final Location loc = report.getLocation();
            final long time = report.getTime();
            // String
            // state="INSERT INTO reports (`user`,`message`,`x`,`y`,`z`,`pitch`,`yaw`,`server`,`world`,`time`) VALUES ('?','?',?,?,?,?,?,?,'?',?)";
            final String state = "INSERT INTO `reports` (`user`,`message`,`x`,`y`,`z`,`pitch`,`yaw`,`server`,`world`,`time`) VALUES ('" + this.stringClean(report.getUser()) + "','" + this.stringClean(report.getMessage()) + "'," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getPitch() + "," + loc.getYaw() + "," + this.serverNumber + ",'" + loc.getWorld().getName() + "'," + time + ");";
            this.j2.debug("Query: " + state);
            ps = conn.prepareStatement(state);
            // j2.log.info(report.getUser());
            // String cleanUser=stringClean(report.getUser());
            // j2.log.info(cleanUser);
            // String cleanMessage=stringClean(report.getMessage());
            /*
             * ps.setString(1, cleanUser); ps.setString(2, cleanMessage);
             * ps.setDouble(3, loc.getX()); ps.setDouble(4, loc.getY());
             * ps.setDouble(5, loc.getZ()); ps.setFloat(6, loc.getPitch());
             * ps.setFloat(7, loc.getYaw()); ps.setInt(8, serverNumber);
             * ps.setString(9, loc.getWorld().getName()); ps.setLong(10, time);
             */
            ps.executeUpdate();
            final String state2 = "SELECT id,user,x,y,z,pitch,yaw,message,world,time,closed from reports where server=" + this.serverNumber + " and closed=0 and id>" + this.j2.reports.maxid;
            ps = conn.prepareStatement(state2);
            this.j2.debug("Query: " + state2);
            rs = ps.executeQuery();
            while (rs.next()) {
                final String user = rs.getString("user");
                final Location loc2 = new Location(this.j2.getServer().getWorld(rs.getString("world")), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"), rs.getFloat("pitch"), rs.getFloat("yaw"));
                this.j2.reports.addReportAndAlert(new Report(rs.getInt("id"), loc2, user, rs.getString("message"), rs.getLong("time"), rs.getBoolean("closed")));
                this.j2.debug("Adding new report to list, user " + user);
            }
        } catch (final SQLException ex) {

        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (final SQLException ex) {
            }
        }
    }

    /**
     * Update a report as being closed by admin.
     * 
     * @param id
     * @param admin
     * @param reason
     */
    public void closeReport(int id, String admin, String reason) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = this.getConnection();
            final String state = "UPDATE reports SET closed=1,admin=?,reason=? where id=?";
            this.j2.debug("Query: " + state);
            ps = conn.prepareStatement(state);
            ps.setString(1, admin);
            ps.setString(2, reason);
            ps.setInt(3, id);
            ps.executeUpdate();
            this.j2.debug("Report " + id + " closed by " + admin);
        } catch (final SQLException ex) {

        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (final SQLException ex) {
            }
        }
    }

    /**
     * Get a list of all homes of a player
     * 
     * @param playername
     * @return
     */
    public ArrayList<Warp> getHomes(String playername) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        final ArrayList<Warp> homes = new ArrayList<Warp>();
        try {
            conn = this.getConnection();
            final String state = "SELECT * FROM warps where server=? and flag=? and player=?";
            this.j2.debug("Query: " + state);
            ps = conn.prepareStatement(state);
            ps.setInt(1, this.serverNumber);
            ps.setString(2, String.valueOf(Flag.PLAYER_HOME.getChar()));
            ps.setString(3, playername);
            rs = ps.executeQuery();
            while (rs.next()) {
                homes.add(new Warp(rs.getString("name"), rs.getString("player"), new Location(this.j2.getServer().getWorld(rs.getString("world")), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"), rs.getFloat("pitch"), rs.getFloat("yaw")), Flag.byChar(rs.getString("flag").charAt(0))));
            }
            this.j2.debug("Loaded " + homes.size() + " warps");

        } catch (final SQLException ex) {
            this.j2.logWarn(ChatColor.RED + "Unable to load homes for " + playername + " from MySQL. Oh hell");
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (final SQLException ex) {
            }
        }
        return homes;
    }

    /**
     * Remove a warp
     * 
     * @param warp
     */
    public void removeWarp(Warp warp) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = this.getConnection();
            final String state = "DELETE FROM warps WHERE name=? and player=? and server=? and flag=?";
            this.j2.debug("Query: " + state);
            ps = conn.prepareStatement(state);
            ps.setString(1, warp.getName());
            ps.setString(2, warp.getPlayer());
            ps.setInt(3, this.serverNumber);
            ps.setString(4, String.valueOf(warp.getFlag().getChar()));
            ps.executeUpdate();
        } catch (final SQLException ex) {

        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (final SQLException ex) {
            }
        }
    }

    /**
     * Add a warp
     * 
     * @param warp
     */
    public void addWarp(Warp warp) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = this.getConnection();
            final String state = "INSERT INTO warps (name,player,server,flag,world,x,y,z,pitch,yaw) VALUES (?,?,?,?,?,?,?,?,?,?)";
            this.j2.debug("Query: " + state);
            ps = conn.prepareStatement(state);
            ps.setString(1, this.stringClean(warp.getName()));
            ps.setString(2, this.stringClean(warp.getPlayer()));
            ps.setInt(3, this.serverNumber);
            ps.setString(4, String.valueOf(warp.getFlag().getChar()));
            final Location loc = warp.getLocation();
            ps.setString(5, loc.getWorld().getName());
            ps.setDouble(6, loc.getX());
            ps.setDouble(7, loc.getY());
            ps.setDouble(8, loc.getZ());
            ps.setFloat(9, loc.getPitch());
            ps.setFloat(10, loc.getPitch());
            ps.executeUpdate();
        } catch (final SQLException ex) {

        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (final SQLException ex) {
            }
        }
    }

    /**
     * Update alias db
     * 
     * @param name
     * @param ip
     */
    public void userIP(String name, String ip) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = this.getConnection();
            // String ip=address.getAddress().getHostAddress();
            final String state = "SELECT * FROM " + this.aliasdb + " WHERE Name=\"" + name + "\" and IP=\"" + ip + "\"";
            this.j2.debug("Query: " + state);
            ps = conn.prepareStatement(state);
            rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("Logins");
                count++;
                ps = conn.prepareStatement("UPDATE " + this.aliasdb + " set Logins=" + count + ",Time=now() where Name=\"" + name + "\" and IP=\"" + ip + "\"");
                ps.executeUpdate();
            } else {
                final String state2 = "INSERT INTO " + this.aliasdb + " (`Name`,`IP`,`Time`,`Logins`) values (\"" + name + "\",\"" + ip + "\",now(),1)";
                this.j2.debug("Query: " + state2);
                ps = conn.prepareStatement(state2);
                ps.executeUpdate();
            }
        } catch (final SQLException ex) {
            this.j2.logWarn(ChatColor.RED + "Unable to load user/ip from MySQL. Oh hell");
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (final SQLException ex) {
            }
        }
    }

    /**
     * Get IPs matching username
     * 
     * @param name
     * @return
     */
    public ArrayList<String> IPGetIPs(String name) {
        final ArrayList<String> IPs = new ArrayList<String>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = this.getConnection();
            final String state = "SELECT IP FROM " + this.aliasdb + " WHERE Name=\"" + name + "\"";
            this.j2.debug("Query: " + state);
            ps = conn.prepareStatement(state);
            rs = ps.executeQuery();
            while (rs.next()) {
                IPs.add(rs.getString("IP"));
            }
        } catch (final SQLException ex) {
            this.j2.logWarn(ChatColor.RED + "Unable to load user/ip from MySQL. Oh hell");
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (final SQLException ex) {
            }
        }
        return IPs;
    }

    /**
     * Get names matching IP
     * 
     * @param IP
     * @return
     */
    public ArrayList<String> IPGetNames(String IP) {
        final ArrayList<String> names = new ArrayList<String>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = this.getConnection();
            final String state = "SELECT Name FROM " + this.aliasdb + " WHERE IP=\"" + IP + "\"";
            this.j2.debug("Query: " + state);
            ps = conn.prepareStatement(state);
            rs = ps.executeQuery();
            while (rs.next()) {
                names.add(rs.getString("Name"));
            }
        } catch (final SQLException ex) {
            this.j2.logWarn(ChatColor.RED + "Unable to load user/ip from MySQL. Oh hell");
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (final SQLException ex) {
            }
        }
        return names;
    }

    /**
     * Get names and timestamps matching IP
     * 
     * @param IP
     * @return
     */
    public HashMap<String, Long> IPGetNamesOnIP(String IP) {
        final HashMap<String, Long> nameDates = new HashMap<String, Long>();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = this.getConnection();
            final String state = "SELECT `Name`, `Time` FROM " + this.aliasdb + " WHERE IP=\"" + IP + "\" ORDER BY `Time` DESC LIMIT 5";
            this.j2.debug("Query: " + state);
            ps = conn.prepareStatement(state);
            rs = ps.executeQuery();
            while (rs.next()) {
                nameDates.put(rs.getString("Name"), rs.getTimestamp("Time").getTime());
            }
        } catch (final SQLException ex) {
            this.j2.logWarn(ChatColor.RED + "Unable to load user/ip from MySQL. Oh hell");
            this.j2.debug("Exception: " + ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (final SQLException ex) {
            }
        }
        return nameDates;
    }

    /**
     * Get last IP used by username
     * 
     * @param name
     * @return
     */
    public String IPGetLast(String name) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        name = name.split(" ")[0];
        String result = "";
        try {
            conn = this.getConnection();
            final String state = "SELECT IP FROM " + this.aliasdb + " WHERE Name='" + name + "' order by Time desc limit 1";
            this.j2.debug("Query: " + state);
            ps = conn.prepareStatement(state);
            rs = ps.executeQuery();
            if (rs.next()) {
                result = rs.getString("IP");
            }
        } catch (final SQLException ex) {
            this.j2.logWarn(ChatColor.RED + "Unable to load user/ip from MySQL. Oh hell");
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (final SQLException ex) {
            }
        }
        return result;
    }

    /**
     * Get all permissions
     * 
     * @return
     */
    public HashMap<String, Flag> getPerms() {
        final HashMap<String, Flag> perms = new HashMap<String, Flag>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = this.getConnection();
            final String state = "SELECT permission,flag FROM perms where server=?";
            this.j2.debug("Query: " + state);
            ps = conn.prepareStatement(state);
            ps.setInt(1, this.serverNumber);
            rs = ps.executeQuery();
            while (rs.next()) {
                perms.put(rs.getString("permission"), Flag.byChar(rs.getString("flag").charAt(0)));
            }
            this.j2.debug("Loaded " + perms.size() + " permissions");

        } catch (final SQLException ex) {
            this.j2.logWarn(ChatColor.RED + "Unable to load user/ip from MySQL. Oh hell");
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (final SQLException ex) {
            }
        }
        return perms;
    }

    public HashMap<String, String> getIRCAdmins() {
        final HashMap<String, String> admins = new HashMap<String, String>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = this.getConnection();
            final String state = "SELECT name,hostname FROM j2users where hostname!=''";
            this.j2.debug("Query: " + state);
            ps = conn.prepareStatement(state);
            rs = ps.executeQuery();
            while (rs.next()) {
                admins.put(rs.getString("hostname"), rs.getString("name"));
            }
            this.j2.debug("Loaded " + admins.size() + " irc admins");

        } catch (final SQLException ex) {
            this.j2.logWarn(ChatColor.RED + "Unable to load irc admins from MySQL. Oh hell");
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (final SQLException ex) {
            }
        }
        return admins;
    }

    public void addNote(String sender, String recipient, String message, boolean adminBusiness) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = this.getConnection();
            final String state = "INSERT INTO notes (`from`,`to`,`message`,`time`,`adminBusiness`) VALUES (?,?,?,?,?);";
            this.j2.debug("Query: " + state);
            ps = conn.prepareStatement(state);
            ps.setString(1, this.stringClean(sender));
            ps.setString(2, this.stringClean(recipient));
            ps.setString(3, this.stringClean(message));
            ps.setTimestamp(4, new Timestamp(new Date().getTime()));
            ps.setBoolean(5, adminBusiness);
            ps.executeUpdate();
        } catch (final SQLException ex) {
            this.j2.logWarn("Failure recording a note");
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (final SQLException ex) {
            }
        }
    }

    public ArrayList<Note> getNotes(String name) {
        final ArrayList<Note> notes = new ArrayList<Note>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = this.getConnection();
            final String state = "SELECT * FROM notes where received=0 and `to`=\"" + name + "\"";
            this.j2.debug("Query: " + state);
            ps = conn.prepareStatement(state);
            rs = ps.executeQuery();
            while (rs.next()) {
                notes.add(new Note(rs.getString("from"), rs.getString("message"), new Date(rs.getTimestamp("time").getTime()), rs.getBoolean("adminBusiness")));
            }
            this.j2.debug("Loaded " + notes.size() + " notes");
            ps = conn.prepareStatement("UPDATE notes SET received=1 where `to`=\"" + name + "\"");
            ps.executeUpdate();
        } catch (final SQLException ex) {
            this.j2.logWarn(ChatColor.RED + "Unable to load user notes from MySQL. Oh hell");
            this.j2.debug(ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (final SQLException ex) {
            }
        }
        return notes;
    }

}