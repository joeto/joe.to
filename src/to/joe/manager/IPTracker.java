package to.joe.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.ChatColor;

import to.joe.J2;
import to.joe.util.Flag;

/**
 * System for determining if player matches any other players Alerts admins if
 * player matches banned users
 * 
 */
public class IPTracker {
    private final J2 j2;
    private HashMap<String, String> html, nameslist;
    private HashMap<String, Integer> totalcount, bannedcount;
    public ArrayList<String> badlist;

    public IPTracker(J2 j2) {
        this.j2 = j2;
        this.restartManager();
    }

    /**
     * Reset system
     */
    public void restartManager() {
        this.html = new HashMap<String, String>();
        this.nameslist = new HashMap<String, String>();
        this.totalcount = new HashMap<String, Integer>();
        this.bannedcount = new HashMap<String, Integer>();
        this.badlist = new ArrayList<String>();
    }

    /**
     * Incoming player. Adds player to list if they match anyone.
     * 
     * @param name
     * @param IP
     */
    public void incoming(String name, String IP) {
        this.j2.debug("Checking " + name);
        this.j2.mysql.userIP(name, IP);
        HashMap<String, Boolean> names = new HashMap<String, Boolean>();
        HashMap<String, Boolean> ips = new HashMap<String, Boolean>();
        names.put(name, false);
        ips = this.getIPs(names, ips);
        names = this.getNames(names, ips);
        ips = this.getIPs(names, ips);
        names = this.getNames(names, ips);
        ips = this.getIPs(names, ips);
        names = this.getNames(names, ips);
        ips = this.getIPs(names, ips);
        names = this.getNames(names, ips);
        this.html.remove(name);
        this.totalcount.remove(name);
        this.bannedcount.remove(name);
        if (names.size() > 1) {
            String nameslist_s = "";
            int ohnoes = 0;
            for (final String n : names.keySet()) {
                if (!n.equalsIgnoreCase(name)) {
                    if (this.j2.mysql.checkBans(n) == null) {
                        nameslist_s += n + " ";
                    } else {
                        nameslist_s += "<span style='color:red'>" + n + "</span> ";
                        ohnoes++;
                    }
                }
            }
            final String newknown = "<tr><td><a href='../alias/detector.php?name=" + name + "'>" + name + "</a></td><td>" + nameslist_s + "</td></tr>";
            this.html.put(name, newknown);
            this.nameslist.put(name, nameslist_s);
            this.totalcount.put(name, names.size());
            this.bannedcount.put(name, ohnoes);
            if (ohnoes > 0) {
                this.badlist.add(name);
            }
            this.j2.debug("Adding to list");
        } else {
            this.j2.debug("Not enough to add");
        }
    }

    /**
     * Get IPs matching currently collected names. Append to list
     * 
     * @param names
     * @param ips
     * @return
     */
    public HashMap<String, Boolean> getIPs(HashMap<String, Boolean> names, HashMap<String, Boolean> ips) {
        final Set<String> keyset = names.keySet();
        final ArrayList<String> newips = new ArrayList<String>();
        final ArrayList<String> searched = new ArrayList<String>();
        for (final String key : keyset) {
            if (!names.get(key)) {
                searched.add(key);
                // System.out.println("Searching "+key);
                final ArrayList<String> tempips = this.j2.mysql.IPGetIPs(key);
                for (final String i : tempips) {
                    if (!i.equals("") && !newips.contains(i) && !keyset.contains(i)) {
                        newips.add(i);
                        // System.out.println("Found: "+i);
                    }
                }
            }
        }
        for (final String s : searched) {
            ips.remove(s);
            ips.put(s, true);
        }
        for (final String ip : newips) {
            this.j2.debug("Found IP: " + ip);
            ips.put(ip, false);
        }
        return ips;
    }

    /**
     * Get Names matching currently collected IPs Append to list.
     * 
     * @param names
     * @param ips
     * @return
     */
    public HashMap<String, Boolean> getNames(HashMap<String, Boolean> names, HashMap<String, Boolean> ips) {
        final Set<String> keyset = ips.keySet();
        final ArrayList<String> newnames = new ArrayList<String>();
        final ArrayList<String> searched = new ArrayList<String>();
        for (final String key : keyset) {
            if (!ips.get(key)) {
                searched.add(key);
                // System.out.println("Searching "+key);
                final ArrayList<String> tempnames = this.j2.mysql.IPGetNames(key);
                for (final String i : tempnames) {
                    if (!i.equals("") && !newnames.contains(i) && !keyset.contains(i)) {
                        newnames.add(i);
                        // System.out.println("Found: "+i);
                    }
                }
            }
        }
        for (final String s : searched) {
            ips.remove(s);
            ips.put(s, true);
        }
        for (final String name : newnames) {
            this.j2.debug("Found Name: " + name);
            names.put(name, false);
        }
        return names;
    }

    /**
     * Get list of known matches, formatted for webpage
     * 
     * @param name
     * @return
     */
    public String getKnown(String name) {
        if (this.html.containsKey(name)) {
            return this.html.get(name);
        }
        return "";
    }

    /**
     * Get total count of matches
     * 
     * @param name
     * @return
     */
    public int getTotal(String name) {
        if (this.totalcount.containsKey(name)) {
            return this.totalcount.get(name);
        }
        return 0;
    }

    /**
     * Get total count of matching banned players
     * 
     * @param name
     * @return
     */
    public int getBanned(String name) {
        if (this.bannedcount.containsKey(name)) {
            return this.bannedcount.get(name);
        }
        return 0;
    }

    /**
     * When player actually joins Alert admins if matches banned players and
     * isn't set to quietly join
     * 
     * @param name
     */
    public void processJoin(String name) {
        if (this.badlist.contains(name)) {
            final int total = this.getTotal(name) - 1;
            final int banned = this.getBanned(name);
            if (!this.j2.hasFlag(name, Flag.QUIET_IRC)&&!this.j2.hasFlag(name, Flag.TRUSTED)) {
                this.j2.irc.messageAdmins("[J2BANS] " + name + " matches " + total + " others: " + banned + " banned");
            }
            this.j2.chat.messageByFlag(Flag.ADMIN, ChatColor.LIGHT_PURPLE + "[J2BANS] " + ChatColor.WHITE + name + ChatColor.LIGHT_PURPLE + " matches " + total + " others: " + banned + " banned");
        }
    }
}
