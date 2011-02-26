package to.joe;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.entity.Player;


public class managerUsers {
	private J2Plugin j2;
	public managerUsers(J2Plugin J2){
		this.j2=J2;
		this.users=new ArrayList<User>();
		this.groups=new HashMap<String, ArrayList<Flag>>();
	}
	public User getUser(String name){
		synchronized (this.lock){
			for(User u:this.users){
				if(u.getName().equalsIgnoreCase(name))
					return u;
			}
			return null;
		}
	}
	public boolean isOnline(String playername){
		for(User u:this.users){
			if(u.getName().equalsIgnoreCase(playername)){
				return true;
			}
		}
		return false;
	}
	public User getUser(Player player){
		return getUser(player.getName());
	}
	public void addUser(String player){
		synchronized (this.lock){
			User user=this.j2.mysql.getUser(player);
			this.users.add(user);
		}
	}
	public void delUser(Player player){
		synchronized (this.lock){
			User toremove=null;
			for(User user : this.users){
				if(user.getName().equalsIgnoreCase(player.getName()))
					toremove=user;
			}
			if(toremove!=null)
				this.users.remove(toremove);
		}
	}
	public void addFlag(String name, Flag flag){
		synchronized (this.lock){
			for(User user:this.users){
				if(user.getName().equalsIgnoreCase(name)){
					user.addFlag(flag);
					this.j2.mysql.setFlags(name, user.getUserFlags());
				}
			}
		}
	}
	public void dropFlag(String name, Flag flag){
		synchronized (this.lock){
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
		 return this.groups.get(group).contains(flag);
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
	
	private ArrayList<User> users;
	private HashMap<String, ArrayList<Flag>> groups;
	private Object lock= new Object();
}
