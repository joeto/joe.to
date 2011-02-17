package com.J2;

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
	public j2User(String n, ChatColor c, boolean w, boolean r, j2Group g, boolean j, ArrayList<j2Flag> ep){
		name=n;
		color=c;
		whitelist=w;
		reservelist=r;
		group=g;
		jailed=j;
		extraFlag=ep;
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
		for(j2Flag i:extraFlag){
			if(i.getFlag()==f)
				return true;
		}
		return false;
	}
	public boolean hasPriv(String p){
		if(group.hasPerm(p))
			return true;
		for( j2Flag f : extraFlag){
			if(f.hasPerm(p))
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
	private ArrayList<j2Flag> extraFlag;
	private String name;
	private ChatColor color;
	private boolean whitelist,reservelist,jailed;
	private j2Group group;
	
}
