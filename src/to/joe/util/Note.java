package to.joe.util;

import java.util.Date;

import org.bukkit.ChatColor;

/**
 * User to user notes
 * 
 */
public class Note {
    private final String sender;
    private final String message;
    private final Date time;
    boolean adminBusiness;

    public Note(String sender, String message, Date time, boolean adminBusiness) {
        this.sender = sender;
        this.message = message;
        this.time = time;
        this.adminBusiness = adminBusiness;
    }

    @Override
    public String toString() {
        String from;
        if (this.adminBusiness) {
            from = "ADMIN";
        } else {
            from = this.sender;
        }
        return ChatColor.AQUA + "[" + this.time + "] <" + from + "> " + ChatColor.DARK_AQUA + this.message;
    }
}
