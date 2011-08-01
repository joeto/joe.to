package to.joe.util;

import java.util.ArrayList;
import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * A j2 user. Special tracking for special people.
 * @author matt
 *
 */
public class User {

	public User(String name, ChatColor color, String group,  ArrayList<Flag> extraFlags, World world, String safeWord/*, int channel*/){
		this.name=name;
		this.color=color;
		this.backup=color;
		this.group=group;
		this.extraFlags=extraFlags;
		this.lastChat=new ArrayList<Long>();
		this.lastChat.add(0L);
		this.lastChat.add(0L);
		this.lastChat.add(0L);
		this.lastChat.add(0L);
		this.lastChat.add(0L);
		this.lastMessage="";
		this.spamCount=0;
		this.blocksTravelled=new ArrayList<Block>();
		this.blocksTravelled.add(world.getBlockAt(0,1,0));
		this.blocksTravelled.add(world.getBlockAt(0,1,0));
		this.blocksTravelled.add(world.getBlockAt(0,1,0));
		this.blocksTravelled.add(world.getBlockAt(0,1,0));
		this.blocksTravelled.add(world.getBlockAt(0,1,0));
		this.blocksTravelled.add(world.getBlockAt(0,1,0));
		this.blocksTravelled.add(world.getBlockAt(0,1,0));
		this.blocksTravelled.add(world.getBlockAt(0,1,0));
		this.blocksTravelled.add(world.getBlockAt(0,1,0));
		this.blocksTravelled.add(world.getBlockAt(0,1,0));
		this.safeWord=safeWord;
		if(safeWord==null)
			this.safeWord="";
		//this.channel=channel;
	}
	/**
	 * Set the user's group
	 * @param Group
	 */
	public void setGroup(String Group){
		group=Group;
	}
	/**
	 * Set admin safeword
	 * @return
	 */
	public String getSafeWord(){
		return this.safeWord;
	}
	/**
	 * Back up their color
	 * @param clr
	 */
	public void tempSetColor(ChatColor clr){
		this.color=clr;
	}
	/**
	 * Bring back color from backups
	 */
	public void restoreColor(){
		this.color=backup;
	}
	/**
	 * @return user's name
	 */
	public String getName(){
		return name;
	}
	/**
	 * @return user's name with coloring
	 */
	public String getColorName(){
		return color+name+ChatColor.WHITE;
	}
	/**
	 * @return user's color
	 */
	public ChatColor getColor(){
		return color;
	}
	/**
	 * @return user's group
	 */
	public String getGroup(){
		return group;
	}
	/**
	 * @return list of user's flags
	 */
	public ArrayList<Flag> getUserFlags(){
		return extraFlags;
	}
	/**
	 * Add flag to user
	 * @param flag
	 * @return if it was freshly added
	 */
	public boolean addFlag(Flag flag){
		if(!extraFlags.contains(flag)){
			extraFlags.add(flag);
			return true;
		}
		return false;
	}
	/**
	 * Drop flag
	 * @param flag
	 * @return if it was freshly removed
	 */
	public boolean dropFlag(Flag flag){
		if(extraFlags.contains(flag)){
			extraFlags.remove(flag);
			return true;
		}
		return false;
	}
	
	
	/**
	 * @param time
	 * @return if speed is too quick
	 */
	private boolean chatSpeed(long time){
		long cur=(new Date()).getTime();
		if((this.lastChat.get(0)+time)>cur){
			return true;
		}
		this.lastChat.remove((int)0);
		this.lastChat.add(cur);
		return false;
	}
	
	/**
	 * Spam check. Adjust spam count as necessary.
	 * @param message
	 * @return if message is a repeat
	 */
	public int spamCheck(String message){
		boolean isIt=this.chatSpeed(10000L);
		if(!isIt&&!(message.startsWith("/")&&!(message.startsWith("/report")||message.startsWith("/note")||message.startsWith("/anote")||message.startsWith("/msg")))){
			isIt=message.equals(this.lastMessage);
		}
		if(!isIt){
			this.spamCount=0;
		}
		else{
			this.spamCount++;
		}
		this.lastMessage=message;
		return this.spamCount;
	}
	
	/**
	 * Replace user flags
	 * @param flags
	 */
	public void setFlags(ArrayList<Flag> flags){
		this.extraFlags=flags;
	}
	
	/*public Block getBlock(){
		return this.blocksTravelled.get(9);
	}
	
	public Block getMidBlock(){
		return this.blocksTravelled.get(4);
	}
	
	public Block getLastBlock(){
		return this.blocksTravelled.get(0);
	}
	
	public void setCurLoc(Block block){
		this.blocksTravelled.remove(0);
		this.blocksTravelled.add(block);
	}*/

	private ArrayList<Block> blocksTravelled;
	private ArrayList<Flag> extraFlags;
	private String name;
	private ChatColor color,backup;
	private String group;
	private ArrayList<Long> lastChat;
	private String lastMessage;
	private int spamCount;
	private String safeWord;
}
