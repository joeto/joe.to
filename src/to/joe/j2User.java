package to.joe;

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

public class j2User {
	public j2User(String Name, ChatColor Color, String Group,  ArrayList<Flag> ExtraFlags){
		name=Name;
		color=Color;
		group=Group;
		extraFlags=ExtraFlags;
	}
	public void setGroup(String Group){
		group=Group;
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
	private ArrayList<Flag> extraFlags;
	private String name;
	private ChatColor color;
	private String group;
}
