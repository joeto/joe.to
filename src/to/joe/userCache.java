package to.joe;

import java.util.ArrayList;

import org.bukkit.entity.Player;


public class userCache {
	private J2Plugin j2;
	public userCache(J2Plugin J2){
		j2=J2;
		users=new ArrayList<j2User>();
		groups=new ArrayList<j2Group>();
	}
	public j2User getOnlineUser(String name){
		synchronized (lock){
			for(j2User u:users){
				if(u.getName().equalsIgnoreCase(name))
					return u;
			}
			return null;
		}
	}
	public j2User getOnlineUser(Player player){
		return getOnlineUser(player.getName());
	}
	public j2User getOfflineUser(String name){
		return null;
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
					j2.mysql.setFlags(name, user.getFlags());
				}
			}
		}
	}
	public void dropFlag(String name, Flag flag){
		synchronized (lock){
			for(j2User user:users){
				if(user.getName().equalsIgnoreCase(name)){
					user.dropFlag(flag);
					j2.mysql.setFlags(name, user.getFlags());
				}
			}
		}
	}
	public void setGroups(ArrayList<j2Group> Groups){
		groups=Groups;
	}
	public j2Group getGroup(String groupname){
		for(j2Group g:groups){
			if(g.getName().equalsIgnoreCase(groupname)){
				return g;
			}
		}
		return null;
	}

	private ArrayList<j2User> users;
	private ArrayList<j2Group> groups;
	private Object lock;
}
