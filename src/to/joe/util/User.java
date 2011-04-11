package to.joe.util;

/*
 * In-progress user class, for handling all sorts of fun things
 * 
 * To be added:
 * 	Tracking amounts of things done, like distance travelled, blocks broken/made
 *  Perhaps track teleport protection in here
 *  
 *  Or maybe track that elsewhere
 * 
 */

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public class User {
	private Material hat=Material.AIR;
	public User(String name, ChatColor color, String group,  ArrayList<Flag> extraFlags/*, int channel*/){
		this.name=name;
		this.color=color;
		this.backup=color;
		this.group=group;
		this.extraFlags=extraFlags;
		//this.channel=channel;
	}
	public void setGroup(String Group){
		group=Group;
	}
	public void tempSetColor(ChatColor clr){
		this.color=clr;
	}
	public void restoreColor(){
		this.color=backup;
	}
	public void tempSetHat(Material mat){
		this.hat=mat;
	}
	public Material whatWasHat(){
		return this.hat;
	}
	public String getName(){
		return name;
	}
	public String getColorName(){
		return color+name+ChatColor.WHITE;
	}
	public String getGroup(){
		return group;
	}
	public ArrayList<Flag> getUserFlags(){
		return extraFlags;
	}
	public boolean addFlag(Flag flag){
		if(!extraFlags.contains(flag)){
			extraFlags.add(flag);
			return true;
		}
		return false;
	}
	public boolean dropFlag(Flag flag){
		if(extraFlags.contains(flag)){
			extraFlags.remove(flag);
			return true;
		}
		return false;
	}
	public int getChannels(){
		return channel;
	}
	private ArrayList<Flag> extraFlags;
	private int channel;
	private String name;
	private ChatColor color,backup;
	private String group;
}
