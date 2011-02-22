package to.joe;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.entity.Player;


public class userCache {
	private J2Plugin j2;
	public userCache(J2Plugin J2){
		j2=J2;
		users=new ArrayList<j2User>();
		groups=new HashMap<String, ArrayList<Flag>>();
	}
	public j2User getUser(String name){
		synchronized (lock){
			for(j2User u:users){
				if(u.getName().equalsIgnoreCase(name))
					return u;
			}
			return null;
		}
	}
	public j2User getUser(Player player){
		return getUser(player.getName());
	}
	public void addUser(String player){
		synchronized (lock){
			j2User user=j2.mysql.getUser(player);
			users.add(user);
		}
	}
	public void delUser(Player player){
		synchronized (lock){
			j2User toremove=null;
			for(j2User user : users){
				if(user.getName().equalsIgnoreCase(player.getName()))
					toremove=user;
			}
			if(toremove!=null)
				users.remove(toremove);
		}
	}
	public void addFlag(String name, Flag flag){
		synchronized (lock){
			for(j2User user:users){
				if(user.getName().equalsIgnoreCase(name)){
					user.addFlag(flag);
					j2.mysql.setFlags(name, user.getUserFlags());
				}
			}
		}
	}
	public void dropFlag(String name, Flag flag){
		synchronized (lock){
			for(j2User user:users){
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
		j2User user=getUser(player);
		all.addAll(user.getUserFlags());
		all.addAll(getGroupFlags(user.getGroup()));
		return all;
		
	}
	
	private ArrayList<j2User> users;
	private HashMap<String, ArrayList<Flag>> groups;
	private Object lock= new Object();
}
