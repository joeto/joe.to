package to.joe.util;

/**
 * j2Ban
 * 
 * Classy bans for the classy gentleman
 */

public class Ban {
    private final String name;
    private String reason;
    private boolean unbanned, temp;
    private final long timeOfUnban, timeLoaded, timeOfBan;

    public Ban(String name, String reason, long timeOfUnban, long timeLoaded, long timeOfBan, boolean unbanned) {
        this.name = name;
        this.reason = reason;
        if ((this.reason == null) || (this.reason == "")) {
            this.reason = "Banned";
        }
        this.timeOfUnban = timeOfUnban;
        this.unbanned = unbanned;
        if (timeOfUnban == 0) {
            this.temp = false;
        } else {
            this.temp = true;
        }
        this.timeLoaded = timeLoaded;
        this.timeOfBan = timeOfBan;
    }

    /**
     * @return Name of banned player
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return Reason for this ban
     */
    public String getReason() {
        return this.reason;
    }

    /**
     * @return time when the user will be unbanned.
     */
    public long getTimeOfUnban() {
        return this.timeOfUnban;
    }

    /**
     * @return time the user was banned
     */
    public long getTimeOfBan() {
        return this.timeOfBan;
    }

    /**
     * @return when the ban was loaded into the system
     */
    public long getTimeLoaded() {
        return this.timeLoaded;
    }

    /**
     * @return if this is a temp ban
     */
    public boolean isTemp() {
        return this.temp;
    }

    /**
     * @return is this still a ban?
     */
    public boolean isBanned() {
        return !this.unbanned;
    }

    /**
     * Unban!
     */
    public void unBan() {
        this.unbanned = true;
    }
}
