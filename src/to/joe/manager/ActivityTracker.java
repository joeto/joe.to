package to.joe.manager;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.util.Flag;

/**
 * Activity tracking system.
 * To be used for AFK system
 * @author matt
 *
 */
public class ActivityTracker {
	private J2 j2;

	private HashMap<String,Long> activity;
	private Object sync=new Object();
	
	public ActivityTracker(J2 j2){
		this.j2=j2;
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
	 * @param name
	 * @return seconds since name has done anything
	 */
	public int secondsSince(String name){
		synchronized(sync){
			long diff=(new Date()).getTime()-this.activity.get(name);
			return (int)diff/1000;
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
	
	public HashSet<String> getNames(){
		synchronized(sync){
			return new HashSet<String>(this.activity.keySet());
		}
	}
	/**
	 * Restart the tracking system. Wipes list.
	 */
	public void restartManager(){
		this.activity=new HashMap<String,Long>();
		this.j2.getServer().getScheduler().scheduleAsyncRepeatingTask(j2, new activityCheck(j2), 1, 1200);
	}
	
	private class activityCheck implements Runnable{
		private J2 j2;
		
		public activityCheck(J2 j2){
			this.j2=j2;
		}
		@Override
		public void run() {
			HashSet<String> names=this.j2.activity.getNames();
			for(String name:names){
				Player player=this.j2.getServer().getPlayer(name);
				if(player!=null&&player.isOnline()&&!this.j2.hasFlag(player, Flag.ADMIN)){
					if(this.j2.activity.secondsSince(name)>200){
						this.j2.minitrue.processLeave(player);
						player.kickPlayer("You have been idle. Rejoin :)");
						this.j2.log(ChatColor.RED+"Removed "+name+" for idling");
					}
				}
				else{
					this.j2.activity.delete(name);
				}
			}
		}
	}
}
