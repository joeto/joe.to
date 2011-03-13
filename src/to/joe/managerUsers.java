package to.joe;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;


public class managerUsers {
	private J2Plugin j2;
	public managerUsers(J2Plugin J2){
		this.j2=J2;
		this.users=new ArrayList<User>();
		this.groups=new HashMap<String, ArrayList<Flag>>();
	}
	public User getUser(String name){
		synchronized (this.userlock){
			for(User u:this.users){
				if(u.getName().equalsIgnoreCase(name))
					return u;
			}
			return null;
		}
	}
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
	public User getUser(Player player){
		return getUser(player.getName());
	}
	public void addUser(String player){
		synchronized (this.userlock){
			User user=this.j2.mysql.getUser(player);
			this.users.add(user);
		}
	}
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
	public void addFlag(String name, Flag flag){
		synchronized (this.userlock){
			for(User user:this.users){
				if(user.getName().equalsIgnoreCase(name)){
					user.addFlag(flag);
					this.j2.mysql.setFlags(name, user.getUserFlags());
				}
			}
		}
	}
	public void dropFlag(String name, Flag flag){
		synchronized (this.userlock){
			for(User user:this.users){
				if(user.getName().equalsIgnoreCase(name)){
					user.dropFlag(flag);
					this.j2.mysql.setFlags(name, user.getUserFlags());
				}
			}
		}
	}
	public void setGroups(HashMap<String, ArrayList<Flag>> Groups){
		this.groups=Groups;
	}

	public boolean groupHasFlag(String group, Flag flag){
		return (this.groups.get(group) != null ? this.groups.get(group).contains(flag) : false);
	}

	public ArrayList<Flag> getGroupFlags(String groupname){
		return this.groups.get(groupname);
	}

	public ArrayList<Flag> getAllFlags(Player player){
		ArrayList<Flag> all=new ArrayList<Flag>();
		User user=getUser(player);
		all.addAll(user.getUserFlags());
		all.addAll(getGroupFlags(user.getGroup()));
		return all;

	}

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
			jailReasons.put(name, reason);
		}
		//j2.mysql.jail(name,reason,admin);
	}

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
			jailReasons.remove(name);
		}
		//j2.mysql.unJail(name);
	}

	public String getJailReason(String name){
		return jailReasons.get(name);
	}

	public void jailSet(HashMap<String,String> incoming){
		jailReasons=incoming;
	}


	public Location jail;
	private ArrayList<User> users;
	private HashMap<String, ArrayList<Flag>> groups;
	private Object userlock= new Object(),jaillock=new Object();
	private HashMap<String, String> jailReasons;
}