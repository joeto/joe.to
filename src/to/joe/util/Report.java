package to.joe.util;

import org.bukkit.Location;

public class Report {
	private int id;
	private String message, user;
	private Location location;
	private long time;
	public Report(int ID, Location loc, String User, String Message,long time){
		this.id=ID;
		this.location=loc;
		this.user=User;
		this.message=Message;
		this.time=time;
	}
	public int getID(){
		return this.id;
	}
	public Location getLocation(){
		return this.location;
	}
	public String getUser(){
		return this.user;
	}
	public long getTime(){
		return this.time;
	}
	public void setID(int id){
		this.id=id;
	}
	public String getMessage(){
		return this.message;
	}
}
