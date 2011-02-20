package to.joe;

import java.util.ArrayList;

import org.bukkit.entity.Player;


public class userCache {
	private J2Plugin j2;
	public userCache(J2Plugin J2){
		j2=J2;
		users=new ArrayList<j2User>();
	}
	public j2User getOnlineUser(String name){
		synchronized (userlock){
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
		synchronized (userlock){
			j2User user=j2.mysql.getUser(player);
			users.add(user);
		}
	}
	public void delUser(Player player){
		synchronized (userlock){
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
		
	}
	public void dropFlag(String name, Flag flag){
		
	}
	private ArrayList<j2User> users;
	private Class userlock;
}
