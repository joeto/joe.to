package to.joe.manager;

import java.util.HashMap;
import to.joe.J2;
import to.joe.util.Flag;

/**
 * Permissions interactions
 *
 */
public class Permissions {

	public Permissions(J2 j2){
		this.j2=j2;
		this.restartManager();
	}
	
	/**
	 * Restart manager
	 */
	public void restartManager(){
		perms=new HashMap<String,Flag>();
	}
	
	/**
	 * Load permissions
	 */
	public void load(){
		perms=j2.mysql.getPerms();
	}
	
	/**
	 * Check if player has named permission
	 * @param playername
	 * @param permission
	 * @return
	 */
	public boolean permCheck(String playername,String permission){
		if(j2.hasFlag(playername,Flag.SRSTAFF)){
			return true;
		}
		if(permission.startsWith("nocheat")||permission.startsWith("afkbooter")){
			if(j2.reallyHasFlag(playername, Flag.ADMIN)){
				return true;
			}
		}
		
		if(perms.containsKey(permission)&&j2.hasFlag(playername, perms.get(permission))){
			return true;
		}
		String[] split=permission.split("\\.");
		String setting = "";
        String node = "";

        for (String next : split) {
            setting += next + ".";
            node = setting + "*";
            if (perms.containsKey(node)&&j2.hasFlag(playername, perms.get(node))) {
                return true;
            }
            else {
                continue;
            }
        }
		return false;
		
	}
	
	/**
	 * Return if player is in named group
	 * @param name
	 * @param group
	 * @return
	 */
	public boolean inGroup(String name,String group){
		if(Flag.isFlagChar(group.charAt(0))){
			return j2.hasFlag(name, Flag.byChar(group.charAt(0)));
		}
		else{
			return false;
		}
	}
	
	private J2 j2;
	private HashMap<String,Flag> perms;
}
