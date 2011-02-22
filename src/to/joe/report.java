package to.joe;

import org.bukkit.Location;

public class report {
	private int id;
	private String message, user;
	private Location location;
	public report(int ID, Location loc, String User, String Message){
		this.id=ID;
		this.location=loc;
		this.user=User;
		this.message=Message;
	}
	public int getID(){
		return id;
	}
	public Location getLocation(){
		return location;
	}
	public String getUser(){
		return user;
	}
	public String getMessage(){
		return message;
	}
}
