package com.J2;

public class ircAdmin {
	private String username,hostname;
	private int lvl;
	public ircAdmin(String name,String host,int level){
		username=name;
		hostname=host;
		lvl=level;
	}
	public String getUsername(){
		return username;
	}
	public String getHostname(){
		return hostname;
	}
	public int getLevel(){
		return lvl;
	}
}

