package to.joe;

import java.util.HashMap;

public enum Flag {
	ADMIN('a',"Base Admin"),
	DONOR('d',"Donor"),
	FUN('f',"Fun commands"),
	JAILED('j', "Jailed"),
	MODWORLD('m',"Can Modify"),
	NEW('n',"New"),
	SRSTAFF('s', "Senior Staff"),
	TRUSTED('t', "Trusted");
	private char flag;
	private String description;
	 private final static HashMap<Character, Flag> flags = new HashMap<Character, Flag>();
	private Flag(char f, String desc){
		this.flag=f;
		this.description=desc;
	}
	public char getChar(){
		return this.flag;
	}
	public String getDescription(){
		return this.description;
	}
	public static Flag byChar(final char Char) {
		return flags.get(Char);
	}

	static {
		for (Flag f : Flag.values()) {
			flags.put(f.getChar(), f);
		}
	}
}
