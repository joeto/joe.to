package to.joe.manager;

import java.util.ArrayList;
import java.util.HashSet;

import net.minecraft.server.Block;
import net.minecraft.server.EntityChicken;
import net.minecraft.server.Packet24MobSpawn;
import net.minecraft.server.Packet60Explosion;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import to.joe.J2;
import to.joe.util.User;

/**
 * System for harassing griefers
 * @author matt
 *
 */
public class CraftualHarassmentPanda {
	private J2 j2;
	private ArrayList<String> harassees;
	private Object sync=new Object();
	String[] pandaLines;
	public CraftualHarassmentPanda(J2 j2){
		this.j2=j2;
		this.restartManager();
	}
	
	/**
	 * Reset system.
	 */
	public void restartManager(){
		this.harassees=new ArrayList<String>();
		try{
			this.pandaLines=j2.readDaFile("panda.txt");
		}
		catch (Exception e){
			this.pandaLines=new String[1];
			this.pandaLines[0]="ololol imma griefer ban me plz";
		}
	}
	
	/**
	 * Triggers on block damage.
	 * If player is being harassed, tell their client it's now sponge.
	 * @param player Player
	 * @param location Location of the block
	 * @return true if it's safe to allow the block break, false if harassed
	 */
	public boolean blockHurt(Player player,Location location){
		if(!this.panda(player)){
			return true;
		}
		player.sendBlockChange(location,Material.SPONGE,(byte)0);
		return false;
	}
	/**
	 * Triggers on block placement. 
	 * If player is being harassed, tell their client it's now a chicken and explode.
	 * @param player Player
	 * @param location Location of new block
	 * @return true if safe to allow, false if harassed
	 */
	public boolean blockPlace(Player player, Location location){
		if(!this.panda(player)){
			return true;
		}
		Inventory i=player.getInventory();
		i.remove(player.getItemInHand().getType());
		EntityChicken bawk=new EntityChicken(((CraftWorld) player.getWorld()).getHandle());
		bawk.setLocation(location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw());
		Packet24MobSpawn pack1=new Packet24MobSpawn(bawk);
		Packet60Explosion pack2=new Packet60Explosion(location.getX(), location.getY(), location.getZ(),10,new HashSet<Block>());
		((CraftPlayer)player).getHandle().netServerHandler.sendPacket(pack1);
		((CraftPlayer)player).getHandle().netServerHandler.sendPacket(pack2);
		return false;
	}
	/**
	 * Triggers on chat and commands.
	 * If player is being harassed, replace with random string from list.
	 * Sends message to admins anyway
	 * Has low-tolerance anti-flood.
	 * @param player
	 * @param message
	 * @return true if safe to allow chat, false if harassed
	 */
	public boolean chat(Player player,String message){
		if(!this.panda(player)){
			return true;
		}
		this.j2.sendAdminPlusLog( ChatColor.DARK_AQUA+"[HARASS]BLOCKED: "+player.getName()+ChatColor.WHITE+": "+message);
		User user=j2.users.getUser(player);
		String name=player.getName();
		String chatlc=message.toLowerCase();
		
		int spamCount=user.isRepeat(chatlc);
		if(spamCount>0){
			switch(spamCount){
			case 2:
				player.sendMessage(ChatColor.RED+"Do you really need to repeat that message?");
				this.j2.sendAdminPlusLog(ChatColor.LIGHT_PURPLE+"Warned "+name+" for chat spam. Kicking if continues.");
				this.j2.debug("User "+name+" warned for chatspam");
				break;
			case 3:
				this.j2.sendAdminPlusLog(ChatColor.LIGHT_PURPLE+"Kicked "+name+" for spamming");
				this.j2.irc.ircAdminMsg("Kicked "+name+" for spamming. Message in next line");
				this.j2.irc.ircAdminMsg(message);
				this.j2.debug("User "+name+" kicked for chatspam");
				break;
			default:
				this.j2.debug("User "+name+" is spamming chat - "+spamCount);
				break;
			}
			return false;
		}
		if(j2.users.getUser(player).canChat(20000)){
			String squawk=this.pandaLines[this.j2.random.nextInt(this.pandaLines.length)];
			this.j2.chat.messageAll( ChatColor.WHITE+"<"+j2.users.getUser(player).getColorName()+ChatColor.WHITE+"> "+squawk);
			this.j2.irc.ircMsg("<"+player.getName()+"> "+squawk);
		}
		return false;
	}
	/** 
	 * Adds player to harassment list
	 * @param player
	 */
	public void harass(String name){
		synchronized(sync){
			this.harassees.add(name.toLowerCase());
		}
	}
	/**
	 * LEAVE BRITTANY ALONE!
	 * Er, uh, removes player from harassment list
	 * @param name
	 */
	public void remove(String name){
		synchronized(sync){
			this.harassees.remove(name.toLowerCase());
		}
	}
	/**
	 * Query for if the player is being harassed
	 * @param player
	 * @return true if on harassment list
	 */
	public boolean panda(Player player){
		synchronized(sync){
			return this.harassees.contains(player.getName().toLowerCase());
		}
	}
}
