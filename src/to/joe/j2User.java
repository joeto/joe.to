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
	public j2User(String Name, ChatColor Color, j2Group Group,  ArrayList<Flag> ExtraFlags){
		name=Name;
		color=Color;
		group=Group;
		extraFlags=ExtraFlags;
	}
	public void setGroup(j2Group Group){
		group=Group;
	}
	public String getName(){
		return name;
	}
	public String getColorName(){
		return color+name+ChatColor.WHITE;
	}
	public j2Group getGroup(){
		return group;
	}
	public boolean hasFlag(Flag flag){
		if(group.hasFlag(flag))
			return true;
		for(Flag i:extraFlags){
			if(i.equals(flag))
				return true;
		}
		return false;
	}
	public ArrayList<Flag> getFlags(){
		ArrayList<Flag> allFlags = new ArrayList<Flag>();
		allFlags.addAll(group.getFlags());
		allFlags.addAll(extraFlags);
		return allFlags;
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
	private j2Group group;
	
}
