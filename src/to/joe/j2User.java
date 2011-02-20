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
	public void setGroup(j2Group g){
		group=g;
	}
	public String getName(){
		return name;
	}
	public String getColorName(){
		return color+name+ChatColor.WHITE;
	}
	public boolean hasResSlot(){
		return extraFlags.contains(Flag.RESSLOT);
	}
	public j2Group getGroup(){
		return group;
	}
	public boolean hasFlag(Flag f){
		if(group.hasFlag(f))
			return true;
		for(Flag i:extraFlags){
			if(i.equals(f))
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
	public void addFlag(Flag f){
		if(!extraFlags.contains(f)){
			extraFlags.add(f);
		}
	}
	public void dropFlag(Flag f){
		if(extraFlags.contains(f)){
			extraFlags.remove(f);
		}
	}
	public boolean isJailed(){
		return extraFlags.contains(Flag.JAILED);
	}
	
	public boolean putInJail(){
		//If was already jailed, return false.
		if(extraFlags.contains(Flag.JAILED)){
			return false;
		}
		addFlag(Flag.JAILED);
		return true;
	}
	private ArrayList<Flag> extraFlags;
	private String name;
	private ChatColor color;
	private j2Group group;
	
}
