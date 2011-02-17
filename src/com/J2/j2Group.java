package com.J2;

import java.util.ArrayList;

/*
 * Group class.
 * For flag definitions
 */

public class j2Group {
	public j2Group(String n, ArrayList<j2Flag> f){
		name=n;
		flags=f;
	}
	public boolean hasFlag(char f){
		for(j2Flag i:flags){
			if(i.getFlag()==f)
				return true;
		}
		return false;
	}
	//TODO: BUKKIT INTEGRATION
	public boolean hasPerm(String p){
		for( j2Flag f : flags){
			if(f.hasPerm(p))
				return true;
		}
		return false;
	}
	public String getName(){
		return name;
	}
	public ArrayList<j2Flag> getFlags(){
		return flags;
	}
	public int getImmunity(){
		return immunity;
	}
	int immunity;
	String name;
	ArrayList<j2Flag> flags;
	boolean cake=true; //Thank you Untamed for the suggestion
}
