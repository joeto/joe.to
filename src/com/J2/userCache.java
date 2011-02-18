package com.J2;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public class userCache {
	public userCache(){
		users=new ArrayList<j2User>();
	}
	public j2User getOnlineUser(String name){
		for(j2User u:users){
			if(u.getName().equalsIgnoreCase(name))
				return u;
		}
		return null;
	}
	public j2User getOnlineUser(Player p){
		return getOnlineUser(p.getName());
	}
	public j2User getOfflineUser(String name){
		return null;
	}
	public void addUser(Player p){
		j2User newuser=PlayerTracking.getUser(p.getName());
		if(newuser!=null)
			users.add(newuser);
		
	}
	public void delUser(Player p){
		j2User toremove=null;
		for(j2User u : users){
			if(u.getName().equalsIgnoreCase(p.getName()))
				toremove=u;
		}
		if(toremove!=null)
			users.remove(toremove);
		
	}
	private ArrayList<j2User> users;
}
