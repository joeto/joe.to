package to.joe.util;

/**
 * j2Ban
 * 
 * Classy bans for the classy gentleman
 */

public class Ban {
    private String name, reason;
    private boolean unbanned, temp;
    private long timeOfUnban, timeLoaded, timeOfBan;

    public Ban(String name, String reason, long timeOfUnban, long timeLoaded, long timeOfBan, boolean unbanned) {
        this.name = name;
        this.reason = reason;
        if (this.reason == null || this.reason == "") {
            this.reason = "Banned";
        }
        this.timeOfUnban = timeOfUnban;
        this.unbanned = unbanned;
        if (timeOfUnban == 0)
            this.temp = false;
        else
            this.temp = true;
        this.timeLoaded = timeLoaded;
        this.timeOfBan = timeOfBan;
    }

    /**
     * @return Name of banned player
     */
    public String getName() {
        return name;
    }

    /**
     * @return Reason for this ban
     */
    public String getReason() {
        return reason;
    }

    /**
     * @return time when the user will be unbanned.
     */
    public long getTimeOfUnban() {
        return timeOfUnban;
    }

    /**
     * @return time the user was banned
     */
    public long getTimeOfBan() {
        return timeOfBan;
    }

    /**
     * @return when the ban was loaded into the system
     */
    public long getTimeLoaded() {
        return timeLoaded;
    }

    /**
     * @return if this is a temp ban
     */
    public boolean isTemp() {
        return temp;
    }

    /**
     * @return is this still a ban?
     */
    public boolean isBanned() {
        return !unbanned;
    }

    /**
     * Unban!
     */
    public void unBan() {
        unbanned = true;
    }
}
