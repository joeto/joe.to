package to.joe.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import to.joe.J2;
import to.joe.util.Flag;
import to.joe.util.Note;
import to.joe.util.User;
import to.joe.util.Runnables.PremiumCheck;


/**
 * User manager
 *
 */
public class Users {
	private J2 j2;
	public Users(J2 J2){
		this.j2=J2;
		this.startTimer();
		this.restartManager();
	}

	/**
	 * Blank stored users, groups, etc.
	 */
	public void restartManager(){
		this.users=new ArrayList<User>();
		this.groups=new HashMap<String, ArrayList<Flag>>();
		this.authedAdmins=new ArrayList<String>();
	}
	/**
	 * Reset groups.
	 */
	public void restartGroups(){
		this.groups=new HashMap<String, ArrayList<Flag>>();
	}

	/**
	 * Is user authenticated.
	 * @param name
	 * @return
	 */
	public boolean isAuthed(String name){
		synchronized(authlock){
			return this.authedAdmins.contains(name);
		}			
	}

	/**
	 * Admin has authed
	 * @param name
	 */
	public void authenticatedAdmin(String name){
		synchronized(authlock){
			this.authedAdmins.add(name);
		}	
	}
	/**
	 * Deauth admin
	 * @param name
	 */
	public void resetAuthentication(Player player){
		String name=player.getName();
		if(this.j2.hasFlag(name,Flag.GODMODE)){
			
		}
		this.getUser(name).setFlags(this.j2.mysql.getUser(name).getUserFlags());
		if(this.j2.minitrue.invisible(player)){
			this.j2.minitrue.vanish(player);
		}
		this.dropAuthentication(name);
	}

	/**
	 * Remove name from auth list
	 * @param name
	 */
	public void dropAuthentication(String name){
		synchronized(authlock){
			this.authedAdmins.remove(name);
		}
	}

	/**
	 * Get named user
	 * @param name
	 * @return
	 */
	public User getUser(String name){
		synchronized (this.userlock){
			for(User u:this.users){
				if(u.getName().equalsIgnoreCase(name))
					return u;
			}
			return null;
		}
	}
	/**
	 * Is player online according to user manager
	 * @param playername
	 * @return
	 */
	public boolean isOnline(String playername){
		synchronized (this.userlock){
			for(User u:this.users){
				if(u.getName().equalsIgnoreCase(playername)){
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * Get user by Player
	 * @param player
	 * @return
	 */
	public User getUser(Player player){
		return getUser(player.getName());
	}
	/**
	 * Add user to system by name
	 * @param playerName
	 */
	public void addUser(String playerName){
		synchronized (this.userlock){
			User user=this.j2.mysql.getUser(playerName);
			if(user!=null)
				this.users.add(user);
			else{
				this.j2.logWarn("Tried to add user \""+playerName+"\" and got null");
			}
		}
	}
	/**
	 * Remove user from system by name.
	 * @param name
	 */
	public void delUser(String name){
		synchronized (this.userlock){
			User toremove=null;
			for(User user : this.users){
				if(user.getName().equalsIgnoreCase(name))
					toremove=user;
			}
			if(toremove!=null)
				this.users.remove(toremove);
		}
	}
	/**
	 * Add flag to user.
	 * @param name
	 * @param flag
	 */
	public void addFlag(String name, Flag flag){
		User user=getUser(name);
		if(user==null){
			user=this.j2.mysql.getUser(name);
		}
		user.addFlag(flag);
		j2.debug("Adding flag "+flag.getChar()+" for "+name);
		this.j2.mysql.setFlags(name, user.getUserFlags());
	}
	/**
	 * Add flag to user for this session
	 * @param name
	 * @param flag
	 */
	public void addFlagLocal(String name, Flag flag){
		User user=getUser(name);
		if(user!=null){
			user.addFlag(flag);
		}
	}
	/**
	 * Drop flag from user
	 * @param name
	 * @param flag
	 */
	public void dropFlag(String name, Flag flag){
		User user=getUser(name);
		if(user==null){
			user=this.j2.mysql.getUser(name);
		}
		user.dropFlag(flag);
		j2.debug("Dropping flag "+flag.getChar()+" for "+name);
		this.j2.mysql.setFlags(name, user.getUserFlags());
	}
	/**
	 * Drop flag from user for this session
	 * @param name
	 * @param flag
	 */
	public void dropFlagLocal(String name, Flag flag){
		User user=getUser(name);
		if(user!=null){
			user.dropFlag(flag);
		}
	}
	/**
	 * Set groups
	 * @param Groups
	 */
	public void setGroups(HashMap<String, ArrayList<Flag>> Groups){
		this.groups=Groups;
	}

	/**
	 * Check if group has named flag.
	 * @param group
	 * @param flag
	 * @return
	 */
	public boolean groupHasFlag(String group, Flag flag){
		return (this.groups.get(group) != null ? this.groups.get(group).contains(flag) : false);
	}

	/**
	 * Get list of flags a group has
	 * @param groupname
	 * @return
	 */
	public ArrayList<Flag> getGroupFlags(String groupname){
		return this.groups.get(groupname);
	}

	/**
	 * Get all flags a user has.
	 * @param player
	 * @return
	 */
	public ArrayList<Flag> getAllFlags(Player player){
		ArrayList<Flag> all=new ArrayList<Flag>();
		User user=getUser(player);
		all.addAll(user.getUserFlags());
		all.addAll(getGroupFlags(user.getGroup()));
		return all;

	}

	/**
	 * Jail a user
	 * @param name
	 * @param reason
	 * @param admin
	 */
	public void jail(String name, String reason, String admin){
		if(isOnline(name)){
			addFlag(name,Flag.JAILED);
		}
		else {
			j2.mysql.getUser(name);
			addFlag(name,Flag.JAILED);
			delUser(name);
		}
		synchronized(jaillock){
			this.jailReasons.put(name, reason);
		}
		//j2.mysql.jail(name,reason,admin);
	}

	/**
	 * Save a user from jail
	 * @param name
	 */
	public void unJail(String name){
		if(isOnline(name)){
			dropFlag(name,Flag.JAILED);
		}
		else {
			j2.mysql.getUser(name);
			dropFlag(name,Flag.JAILED);
			delUser(name);
		}
		synchronized(jaillock){
			this.jailReasons.remove(name);
		}
		//j2.mysql.unJail(name);
	}

	/**
	 * Get reason a user is in jail
	 * @param name
	 * @return
	 */
	public String getJailReason(String name){
		return this.jailReasons.get(name);
	}

	/**
	 * Set list of users jailed with reasons
	 * @param incoming
	 */
	public void jailSet(HashMap<String,String> incoming){
		this.jailReasons=incoming;
	}

	private boolean stop=true;
	private void startTimer() {
		stop = false;
		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (stop) {
					timer.cancel();
					return;
				}
				checkOnline();
			}
		}, 10000, 10000);
	}

	private void checkOnline(){
		synchronized (this.userlock){
			for(User u:new ArrayList<User>(this.users)){
				Player p=j2.getServer().getPlayer(u.getName());
				if(p==null||!p.isOnline()){
					this.users.remove(u);
					if(p!=null){
						p.kickPlayer("You have glitched. Rejoin");
					}
				}
			}
		}


	}

	/**
	 * Process user joining.
	 * @param player
	 */
	public void processJoin(Player player,boolean quiet){
		//Last chance to check
		if(!player.isOnline()){
			return;
		}
		boolean stealthy=this.j2.hasFlag(player, Flag.SILENT_JOIN);
		String name=player.getName();
		this.j2.getServer().getScheduler().scheduleAsyncDelayedTask(this.j2, new PremiumCheck(name,this.j2));
		j2.irc.processJoin(name);
		j2.ip.processJoin(name);
		j2.warps.processJoin(name);
		j2.damage.processJoin(name);
		j2.jail.processJoin(player);
		if(player.getInventory().getHelmet().getTypeId()==Material.FIRE.getId()){
			player.getInventory().setHelmet(new ItemStack(Material.GRASS));
			player.sendMessage(ChatColor.RED+"You fizzle out");
		}
		if(j2.maintenance){
			player.sendMessage(ChatColor.YELLOW+"We are in maintenance mode");
		}
		if(!quiet){
			j2.banCoop.processJoin(player);
		}
		for(String line : j2.motd){
			player.sendMessage(line);
		}
		if(j2.reallyHasFlag(name, Flag.ADMIN)){
			int count=this.j2.reports.numReports();
			player.sendMessage(ChatColor.LIGHT_PURPLE+"There are "+count+" reports. ");
		}
		j2.minitrue.processJoin(player,stealthy);
		if(stealthy){
			this.j2.chat.messageByFlag(Flag.ADMIN, ChatColor.YELLOW+"Stealthy join by "+name);
			this.dropFlag(name, Flag.SILENT_JOIN);
		}
		if(j2.hasFlag(player, Flag.CONTRIBUTOR)){
			player.sendMessage(ChatColor.LIGHT_PURPLE+"We think you're an "+ChatColor.GOLD+"AMAZING CONTRIBUTOR");
			player.sendMessage(ChatColor.LIGHT_PURPLE+"to the minecraft community as a whole! "+ChatColor.RED+"<3");
		}
		ArrayList<Note> notes=this.j2.mysql.getNotes(name);
		if(notes.size()>0){
			player.sendMessage(ChatColor.DARK_AQUA+"You have notes!");
			for(Note note:notes){
				if(note!=null){
					player.sendMessage(note.toString());
				}
			}
		}
	}


	public Location jail;
	private ArrayList<User> users;
	private HashMap<String, ArrayList<Flag>> groups;
	private Object userlock= new Object(),jaillock=new Object(),authlock=new Object();
	private HashMap<String, String> jailReasons;
	private ArrayList<String> authedAdmins;
}