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

import org.bukkit.ChatColor;

public class j2User {
	public j2User(String n, ChatColor c, boolean w, boolean r, int a){
		name=n;
		color=c;
		whitelist=w;
		reservelist=r;
		access=a;
	}
	public void setAccess(int a){
		access=a;
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
	public int getAccess(){
		return access;
	}
	private String name;
	private ChatColor color;
	private boolean whitelist,reservelist;
	private int access;
}
