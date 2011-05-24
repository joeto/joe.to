package to.joe.util;

import java.util.HashMap;

import to.joe.J2;

public class ChatChannel {
	private String chanName;
	private HashMap<String,Integer> userlist;
	private int id;
	//private J2Plugin j2;
	private boolean isPrivate;
	public ChatChannel(int id,String name, HashMap<String,Integer> userlist, boolean isPrivate, J2 j2){
		this.id=id;
		this.chanName=name;
		this.userlist=userlist;
		//this.j2=j2;
		this.isPrivate=isPrivate;
	}
	public void addPerm(String player,int level){

		if(userlist.containsKey(player)){
			userlist.remove(player);
		}
		userlist.put(player, Integer.valueOf(level));

		//j2.mysql.chanPerm(chanName,player,Integer.valueOf(level));
	}
	public int getID(){
		return this.id;
	}
	public String getName(){
		return chanName;
	}
	public boolean isPrivate(){
		return isPrivate;
	}
	public int getperm(String player){
		Integer lvl=userlist.get(player);
		if(lvl!=null){
			return lvl.intValue();
		}
		else {
			return 0;
		}
	}
	
}
