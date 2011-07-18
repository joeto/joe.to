package to.joe.util;

/*
 * In-progress user class, for handling all sorts of fun things
 * 
 * To be added:
 * 	Tracking amounts of things done, like distance travelled, blocks broken/made
 *  Perhaps track teleport protection in here
 *  
 *  Or maybe track that elsewhere
 * 
 */

import java.util.ArrayList;
import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;

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
	public void setGroup(String Group){
		group=Group;
	}
	public String getSafeWord(){
		return this.safeWord;
	}
	public void tempSetColor(ChatColor clr){
		this.color=clr;
	}
	public void restoreColor(){
		this.color=backup;
	}
	public String getName(){
		return name;
	}
	public String getColorName(){
		return color+name+ChatColor.WHITE;
	}
	public ChatColor getColor(){
		return color;
	}
	public String getGroup(){
		return group;
	}
	public ArrayList<Flag> getUserFlags(){
		return extraFlags;
	}
	public boolean addFlag(Flag flag){
		if(!extraFlags.contains(flag)){
			extraFlags.add(flag);
			return true;
		}
		return false;
	}
	public boolean dropFlag(Flag flag){
		if(extraFlags.contains(flag)){
			extraFlags.remove(flag);
			return true;
		}
		return false;
	}
	public int getChannels(){
		return channel;
	}
	public boolean canChat(){
		return this.canChat(10000L);
	}
	
	public boolean canChat(long time){
		long cur=(new Date()).getTime();
		if((this.lastChat.get(0)+time)>cur){
			return false;
		}
		this.lastChat.remove((int)0);
		this.lastChat.add(cur);
		return true;
	}
	
	public int isRepeat(String message){
		boolean isIt=message.equals(this.lastMessage);
		if(!isIt){
			this.spamCount=0;
		}
		else{
			this.spamCount++;
		}
		this.lastMessage=message;
		return this.spamCount;
	}
	
	public Block getBlock(){
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
	}

	private ArrayList<Block> blocksTravelled;
	private ArrayList<Flag> extraFlags;
	private int channel;
	private String name;
	private ChatColor color,backup;
	private String group;
	private ArrayList<Long> lastChat;
	private String lastMessage;
	private int spamCount;
	private String safeWord;
}
