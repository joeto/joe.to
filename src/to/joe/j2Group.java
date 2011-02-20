package to.joe;

import java.util.ArrayList;

/*
 * Group class.
 * For flag definitions
 */

public class j2Group {
	public j2Group(String n, ArrayList<Character> f){
		name=n;
		flags=f;
	}
	public boolean hasFlag(char f){
		for(Character i:flags){
			if(i.equals(Character.valueOf(f)))
				return true;
		}
		return false;
	}
	public String getName(){
		return name;
	}
	public ArrayList<Character> getFlags(){
		return flags;
	}
	public int getImmunity(){
		return immunity;
	}
	int immunity;
	String name;
	ArrayList<Character> flags;
	boolean cake=true; //Thank you Untamed for the suggestion
}
