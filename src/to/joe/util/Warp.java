package to.joe.util;

import org.bukkit.Location;

public class Warp {
	public Warp(String Name, String Player, Location Location, Flag Flag){
		this.location=Location;
		this.name=Name;
		this.player=Player;
		this.flag=Flag;
	}
	public String getName(){
		return this.name;
	}
	public String getPlayer(){
		return this.player;
	}
	public Location getLocation(){
		return this.location;
	}
	public Flag getFlag(){
		return this.flag;
	}
	private Location location;
	private String name, player;
	private Flag flag;
}
