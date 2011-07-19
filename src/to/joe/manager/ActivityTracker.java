package to.joe.manager;

import java.util.Date;
import java.util.HashMap;

import org.bukkit.entity.Player;

import to.joe.J2;

/**
 * Activity tracking system.
 * To be used for AFK system
 * @author matt
 *
 */
public class ActivityTracker {
	//private J2Plugin j2;

	private HashMap<String,Long> activity;
	private Object sync=new Object();
	
	public ActivityTracker(J2 j2){
		//this.j2=j2;
		this.restartManager();
	}
	
	/**
	 * Update system to last time a player moved
	 * @param player Player being updated
	 */
	public void update(Player player){
		long time=(new Date()).getTime();
		synchronized(sync){
			this.activity.put(player.getName(), time);
		}
	}
	
	/**
	 * When is the last time the player did anything?
	 * @param name 
	 * @return When the player last interacted with the server
	 */
	public long getLast(String name){
		synchronized(sync){
			return this.activity.get(name);
		}
	}
	
	/**
	 * Remove a player from tracking
	 * @param name
	 */
	public void delete(String name){
		synchronized(sync){
			this.activity.remove(name);
		}
	}
	/**
	 * Restart the tracking system. Wipes list.
	 */
	public void restartManager(){
		this.activity=new HashMap<String,Long>();
	}
}
