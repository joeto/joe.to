package to.joe;

/*
 * In-progress user class, for handling all sorts of fun things
 * 
 * To be added:
 * 	Tracking amounts of things done, like distance travelled, blocks broken/made
 *  Perhaps track teleport protection in here
 *  
 * 
 */

import java.util.ArrayList;

import org.bukkit.ChatColor;

public class j2User {
	public j2User(String Name, ChatColor Color, boolean Whitelist, boolean ReserveList, j2Group Group, boolean Jailed, ArrayList<Character> ExtraFlags){
		name=Name;
		color=Color;
		whitelist=Whitelist;
		reservelist=ReserveList;
		group=Group;
		jailed=Jailed;
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
	public boolean isWhitelisted(){
		return whitelist;
	}
	public boolean hasResSlot(){
		return reservelist;
	}
	public j2Group getGroup(){
		return group;
	}
	public boolean hasFlag(char f){
		if(group.hasFlag(f))
			return true;
		for(Character i:extraFlags){
			if(i.equals(Character.valueOf(f)))
				return true;
		}
		return false;
	}
	public boolean isJailed(){
		return jailed;
	}
	public boolean putInJail(){
		//If was already jailed, return false.
		if(jailed){
			return false;
		}
		jailed=true;
		return true;
	}
	private ArrayList<Character> extraFlags;
	private String name;
	private ChatColor color;
	private boolean whitelist,reservelist,jailed;
	private j2Group group;
	
}
