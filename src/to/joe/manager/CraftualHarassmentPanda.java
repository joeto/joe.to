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
import to.joe.util.Flag;

public class CraftualHarassmentPanda {
	private J2 j2;
	private ArrayList<String> harassees;
	private Object sync=new Object();
	String[] pandaLines;
	public CraftualHarassmentPanda(J2 j2){
		this.j2=j2;
		this.harassees=new ArrayList<String>();
		this.pandaLines=j2.readDaFile("panda.txt");
	}
	
	public boolean blockHurt(Player player,Location location){
		if(!this.panda(player)){
			return true;
		}
		player.sendBlockChange(location,Material.SPONGE,(byte)0);
		return false;
	}
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
	public boolean chat(Player player,String message){
		if(!this.panda(player)){
			return true;
		}
		this.j2.chat.msgByFlag(Flag.ADMIN, ChatColor.LIGHT_PURPLE+"Squawked<"+player.getName()+">"+message);
		if(j2.users.getUser(player).canChat()){
			String squawk=this.pandaLines[this.j2.random.nextInt(this.pandaLines.length)];
			this.j2.chat.msgByFlagless(Flag.ADMIN, ChatColor.WHITE+"<"+j2.users.getUser(player).getColorName()+ChatColor.WHITE+"> "+squawk);
			this.j2.irc.ircMsg("<"+player.getName()+"> "+squawk);
		}
		return false;
	}
	public void harass(Player p){
		synchronized(sync){
			this.harassees.add(p.getName().toLowerCase());
		}
	}
	public boolean panda(Player player){
		synchronized(sync){
			return this.harassees.contains(player.getName().toLowerCase());
		}
	}
}
