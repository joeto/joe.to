package to.joe.manager;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.util.Flag;

public class Jailer {
	private J2 j2;
	private Location jailLocation;
	public Jailer(J2 j2){
		this.j2=j2;
	}
	public void jailSet(String[] jail){
		this.jailLocation = new Location(j2.getServer().getWorld("world"),Double.valueOf(jail[0]).doubleValue(),Double.valueOf(jail[1]).doubleValue(),Double.valueOf(jail[2]).doubleValue(),Float.valueOf(jail[3]).floatValue(),Float.valueOf(jail[4]).floatValue());
	}
	public void processJoin(Player player){
		if(!this.isJailed(player)){
			return;
		}
		player.teleport(this.jailLocation);
	}
	public void jailMsg(Player player){
		player.sendMessage(ChatColor.RED+"You are in JAIL.");
		//more message here
	}
	public boolean isJailed(Player player){
		return this.isJailed(player.getName());
	}
	public boolean isJailed(String player){
		return j2.hasFlag(player, Flag.JAILED);
	}
	public boolean processAction(Player player){
		if(!this.isJailed(player)){
			return false;
		}
		this.jailMsg(player);
		return true;
	}
}
