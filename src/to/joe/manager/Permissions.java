package to.joe.manager;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.util.Flag;

/**
 * Permissions interactions
 * 
 */
public class Permissions {

    public Permissions(J2 j2) {
        this.j2 = j2;
        this.restartManager();
    }

    /**
     * Restart manager
     */
    public void restartManager() {
        this.perms = new HashMap<String, Flag>();
    }

    /**
     * Load permissions
     */
    public void load() {
        this.perms = this.j2.mysql.getPerms();
    }

    /**
     * Check if player has named permission
     * 
     * @param playername
     * @param permission
     * @return
     */
    public boolean permCheck(String playername, String permission) {
        if (this.j2.hasFlag(playername, Flag.SRSTAFF)) {
            return true;
        }
        if (permission.startsWith("nocheat")) {
            if (this.j2.reallyHasFlag(playername, Flag.SRSTAFF) || this.j2.hasFlag(playername, Flag.VANISHED)) {
                return true;
            }
        }

        if (this.perms.containsKey(permission) && this.j2.hasFlag(playername, this.perms.get(permission))) {
            return true;
        }
        final String[] split = permission.split("\\.");
        String setting = "";
        String node = "";

        for (final String next : split) {
            setting += next + ".";
            node = setting + "*";
            if (this.perms.containsKey(node) && this.j2.hasFlag(playername, this.perms.get(node))) {
                return true;
            } else {
                continue;
            }
        }
        return false;
    }

    public void setPerms(Player player){
        HashMap<String, Flag> permissions= new HashMap<String, Flag>();
        permissions.putAll(this.perms);
        ArrayList<Flag> flags=new ArrayList<Flag>(this.j2.users.getAllFlags(player));
        if(!this.j2.users.isAuthed(player.getName())){
            flags.remove(Flag.ADMIN);
            flags.remove(Flag.SRSTAFF);
        }
        for(String perm:permissions.keySet()){
            if(flags.contains(permissions.get(perm))){
                player.addAttachment(j2, perm, true);
                this.j2.debug("Giving "+player.getName()+ " perm "+perm);
            }
            else{
                player.addAttachment(j2, perm, false);
            }
        }
    }
    
    /**
     * Return if player is in named group
     * 
     * @param name
     * @param group
     * @return
     */
    public boolean inGroup(String name, String group) {
        if (Flag.isFlagChar(group.charAt(0))) {
            return this.j2.hasFlag(name, Flag.byChar(group.charAt(0)));
        } else {
            return false;
        }
    }

    private final J2 j2;
    private HashMap<String, Flag> perms;
}
