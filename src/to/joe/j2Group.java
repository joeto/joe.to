package to.joe;

import java.util.ArrayList;

/*
 * Group class.
 * For flag definitions
 */

public class j2Group {
	public j2Group(String n, ArrayList<Flag> f){
		name=n;
		flags=f;
	}
	public boolean hasFlag(Flag f){
		for(Flag i:flags){
			if(i.equals(f))
				return true;
		}
		return false;
	}
	public String getName(){
		return name;
	}
	public ArrayList<Flag> getFlags(){
		return flags;
	}
	public int getImmunity(){
		return immunity;
	}
	int immunity;
	String name;
	ArrayList<Flag> flags;
	boolean cake=true; //Thank you Untamed for the suggestion
}
