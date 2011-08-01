package to.joe.util;

import org.bukkit.Location;

/**
 * A single report.
 *
 */
public class Report {
	private int id;
	private String message, username;
	private Location location;
	private long time;
	private boolean closed;
	public Report(int ID, Location loc, String User, String Message,long time,boolean closed){
		this.id=ID;
		this.location=loc;
		this.username=User;
		this.message=Message;
		this.time=time;
		this.closed=closed;
	}
	/**
	 * @return report's id
	 */
	public int getID(){
		return this.id;
	}
	/**
	 * @return is the report closed?
	 */
	public boolean closed(){
		return this.closed;
	}
	/**
	 * @return location of the report
	 */
	public Location getLocation(){
		return this.location;
	}
	/**
	 * @return the username who reported
	 */
	public String getUser(){
		return this.username;
	}
	/**
	 * @return time of report
	 */
	public long getTime(){
		return this.time;
	}
	/**
	 * Update the ID. DO NOT TOUCH.
	 * @param id
	 */
	public void setID(int id){
		this.id=id;
	}
	/**
	 * @return the report's contents
	 */
	public String getMessage(){
		return this.message;
	}
}
