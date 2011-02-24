package to.joe;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.entity.Player;


public class managerUsers {
	private J2Plugin j2;
	public managerUsers(J2Plugin J2){
		j2=J2;
		users=new ArrayList<User>();
		groups=new HashMap<String, ArrayList<Flag>>();
	}
	public User getUser(String name){
		synchronized (lock){
			for(User u:users){
				if(u.getName().equalsIgnoreCase(name))
					return u;
			}
			return null;
		}
	}
	public boolean isOnline(String playername){
		for(User u:users){
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
		synchronized (lock){
			User user=j2.mysql.getUser(player);
			users.add(user);
		}
	}
	public void delUser(Player player){
		synchronized (lock){
			User toremove=null;
			for(User user : users){
				if(user.getName().equalsIgnoreCase(player.getName()))
					toremove=user;
			}
			if(toremove!=null)
				users.remove(toremove);
		}
	}
	public void addFlag(String name, Flag flag){
		synchronized (lock){
			for(User user:users){
				if(user.getName().equalsIgnoreCase(name)){
					user.addFlag(flag);
					j2.mysql.setFlags(name, user.getUserFlags());
				}
			}
		}
	}
	public void dropFlag(String name, Flag flag){
		synchronized (lock){
			for(User user:users){
				if(user.getName().equalsIgnoreCase(name)){
					user.dropFlag(flag);
					j2.mysql.setFlags(name, user.getUserFlags());
				}
			}
		}
	}
	public void setGroups(HashMap<String, ArrayList<Flag>> Groups){
		groups=Groups;
	}
	
	public boolean groupHasFlag(String group, Flag flag){
		 return groups.get(group).contains(flag);
	}
	
	public ArrayList<Flag> getGroupFlags(String groupname){
		return groups.get(groupname);
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
