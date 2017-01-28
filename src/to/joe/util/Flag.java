package to.joe.util;

import java.util.HashMap;

/**
 * Flag
 * 
 */
public enum Flag {
    ADMIN('a', "Base Admin"), // standard commands
    CONTRIBUTOR('c', "Important MC Community Member"), // bukkit team, etc
    DONOR('d', "Donor"), // moneh! and other stuff
    FUN('f', "Fun commands"), // standard toolset for mc2. currently unused
    GODMODE('g', "Godmode"), // just for tracking, not 100% used yet
    JAILED('j', "Jailed"), // no get out of jail free card,
    SHUT_OUT_WORLD('l', "Shutting out the world"), //Temporary - /shush
    MODWORLD('m', "Can Modify"), // standard ability to edit blocks
    NEW('n', "New"), // new player. currently unused
    JOINED('J', "Joined since last reset"), //Indicates user is has joined
    NSA('N', "Listening In"), // admin listening to msg
    BARRED_MC1('p', "Banned from mc1"), // Cannot join mc1 no matter what.
    SILENT_JOIN('q', "Silent but deadly"), // invisadmin
    TRUSTREQ('r', "Accepting trustreq"), //Accept /trustreq (trusted only)
    SRSTAFF('s', "Senior Staff"), // senior staff
    TRUSTED('t', "Trusted"), // trusted
    TOOLS('T', "Tool mode"), // admin tool mode
    WATCH('W', "Suspicious player"), //Watch flag for redalert system
    AUTOWATCH('w', "Joined during redalert"), //Given to player if joined during redalert
    VANISHED('V',"Currently invisible"),//Invisabull
    THOR('x', "Thor Powers"), // admin lightning powers
    MUTED('X', "Muted"), // player muted
    QUIET_IRC('y', "Minimal IRC alerts"), //Stop spamming IRC!
    CUSTOM3('z', "Custom3"),
    MAYBETHREAT('H',"Other Server Bans"),
    // Numbers are reserved for internal programming.
    PLAYER_HOME('0', "Home designation (internal)"), //homes
    PLAYER_WARP_PUBLIC('1', "Unflagged warp designation (internal)");//warps

    private char flag;
    private String description;
    private final static HashMap<Character, Flag> flags = new HashMap<Character, Flag>();

    private Flag(char f, String desc) {
        this.flag = f;
        this.description = desc;
    }

    /**
     * Get character by which this flag is represented
     * 
     * @return
     */
    public char getChar() {
        return this.flag;
    }

    /**
     * Get the longer string describing this flag
     * 
     * @return
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Get a flag by it's representing char
     * 
     * @param Char
     * @return
     */
    public static Flag byChar(final char Char) {
        return Flag.flags.get(Char);
    }

    /**
     * Does this character represent a flag?
     * 
     * @param Char
     * @return
     */
    public static boolean isFlagChar(char Char) {
        return Flag.flags.containsKey(Char);
    }

    static {
        for (final Flag f : Flag.values()) {
            Flag.flags.put(f.getChar(), f);
        }
    }
}
