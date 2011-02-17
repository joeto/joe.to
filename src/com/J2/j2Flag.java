package com.J2;

import java.util.ArrayList;

public class j2Flag {
	public j2Flag(char f, ArrayList<String> p){
		flag=f;
		perms=p;
	}
	public char getFlag(){
		return flag;
	}
	public ArrayList<String> getPerms(){
		return perms;
	}
	//TODO: BUKKIT INTEGRATION
	//Assuming string here for now. fixitfixitfixit
	public boolean hasPerm(String p){
		return perms.contains(p);
	}
	char flag;
	ArrayList<String> perms;
}
