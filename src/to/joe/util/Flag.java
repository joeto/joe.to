package to.joe.util;

import java.util.HashMap;

/**
 * Flag
 * @author matt
 *
 */
public enum Flag {
	ADMIN('a',"Base Admin"),//standard commands
	CONTRIBUTOR('c',"Important MC Community Member"),//bukkit team, etc
	DONOR('d',"Donor"),//moneh! and other stuff
	FUN('f',"Fun commands"),//standard toolset for mc2. currently unused
	GODMODE('g',"Godmode"),//just for tracking, not 100% used yet
	JAILED('j', "Jailed"),//no get out of jail free card,
	LALALA('l', "Shutting out the world"),
	MODWORLD('m',"Can Modify"),//standard ability to edit blocks
	NEW('n',"New"),//new player. currently unused
	NSA('N',"Listening In"),//admin listening to msg
	NEVER_AGAIN('p',"Banned from mc1"),// Cannot join mc1 no matter what.
	SILENT_BUT_DEADLY('q',"Silent but deadly"),//invisadmin
	SRSTAFF('s', "Senior Staff"),//senior staff
	TRUSTED('t', "Trusted"),//trusted
	TOOLS('T',"Tool mode"),//admin tool mode
	THOR('x',"Thor Powers"),//admin lightning powers
	MUTED('X',"Muted"),//player muted
	QUIETERJOIN('y',"No join alerts on IRC"),
	CUSTOM3('z',"Custom3"),
	//Numbers are reserved for internal programming.
	Z_HOME_DESIGNATION('0',"DO NOT USE"),
	Z_SPAREWARP_DESIGNATION('1',"DO NOT USE");
	
	private char flag;
	private String description;
	private final static HashMap<Character, Flag> flags = new HashMap<Character, Flag>();
	private Flag(char f, String desc){
		this.flag=f;
		this.description=desc;
	}
	/**
	 * Get character by which this flag is represented
	 * @return
	 */
	public char getChar(){
		return this.flag;
	}
	/**
	 * Get the longer string describing this flag
	 * @return
	 */
	public String getDescription(){
		return this.description;
	}
	/**
	 * Get a flag by it's representing char
	 * @param Char
	 * @return
	 */
	public static Flag byChar(final char Char) {
		return flags.get(Char);
	}

	/**
	 * Does this character represent a flag?
	 * @param Char
	 * @return
	 */
	public static boolean isFlagChar(char Char){
		return flags.containsKey(Char);
	}

	static {
		for (Flag f : Flag.values()) {
			flags.put(f.getChar(), f);
		}
	}
}
