package to.joe.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.ChatColor;

import to.joe.J2;
import to.joe.util.Flag;


public class IPTracker {
	private J2 j2;
	private HashMap<String,String> html,nameslist;
	private HashMap<String,Integer> totalcount,bannedcount;
	public ArrayList<String> badlist;

	public IPTracker(J2 j2){
		this.j2=j2;
		this.restartManager();
	}
	public void restartManager(){
		this.html=new HashMap<String,String>();
		this.nameslist=new HashMap<String,String>();
		this.totalcount=new HashMap<String,Integer>();
		this.bannedcount=new HashMap<String,Integer>();
		this.badlist=new ArrayList<String>();
	}
	public void incoming(String name, String IP){
		j2.debug("Checking "+name);
		j2.mysql.userIP(name,IP);
		HashMap<String,Boolean> names=new HashMap<String,Boolean>();
		HashMap<String,Boolean> ips=new HashMap<String,Boolean>();
		names.put(name, false);
		ips=getIPs(names,ips);
		names=getNames(names,ips);
		ips=getIPs(names,ips);
		names=getNames(names,ips);
		ips=getIPs(names,ips);
		names=getNames(names,ips);
		ips=getIPs(names,ips);
		names=getNames(names,ips);
		html.remove(name);
		totalcount.remove(name);
		bannedcount.remove(name);
		if(names.size()>1){
			String nameslist_s="";
			int ohnoes=0;
			for(String n:names.keySet()){
				if(!n.equalsIgnoreCase(name)){
					if(j2.mysql.checkBans(n)==null){
						nameslist_s+=n+" ";
					}
					else{
						nameslist_s+="<span style='color:red'>"+n+"</span> ";
						ohnoes++;
					}
				}
			}
			String newknown="<tr><td><a href='../alias/detector.php?name="+name+"'>"+name+"</a></td><td>"+nameslist_s+"</td></tr>";
			html.put(name, newknown);
			this.nameslist.put(name, nameslist_s);
			totalcount.put(name, names.size());
			bannedcount.put(name, ohnoes);
			if(ohnoes>0){
				badlist.add(name);
			}
			j2.debug("Adding to list");
		}
		else{
			j2.debug("Not enough to add");
		}
	}
	public HashMap<String,Boolean> getIPs(HashMap<String,Boolean> names,HashMap<String,Boolean> ips){
		Set<String> keyset = names.keySet();
		ArrayList<String> newips=new ArrayList<String>();
		ArrayList<String> searched=new ArrayList<String>();
		for(String key:keyset){
			if(!names.get(key)){
				searched.add(key);
				//System.out.println("Searching "+key);
				ArrayList<String> tempips=j2.mysql.IPGetIPs(key);
				for(String i:tempips){
					if(!i.equals("")&&!newips.contains(i)&&!keyset.contains(i)){
						newips.add(i);
						//System.out.println("Found: "+i);
					}
				}
			}
		}
		for(String s:searched){
			ips.remove(s);
			ips.put(s, true);
		}
		for(String ip:newips){
			j2.debug("Found IP: "+ip);
			ips.put(ip, false);
		}
		return ips;
	}

	public HashMap<String,Boolean> getNames(HashMap<String,Boolean> names,HashMap<String,Boolean> ips){
		Set<String> keyset = ips.keySet();
		ArrayList<String> newnames=new ArrayList<String>();
		ArrayList<String> searched=new ArrayList<String>();
		for(String key:keyset){
			if(!ips.get(key)){
				searched.add(key);
				//System.out.println("Searching "+key);
				ArrayList<String> tempnames=j2.mysql.IPGetNames(key);
				for(String i:tempnames){
					if(!i.equals("")&&!newnames.contains(i)&&!keyset.contains(i)){
						newnames.add(i);
						//System.out.println("Found: "+i);
					}
				}
			}
		}
		for(String s:searched){
			ips.remove(s);
			ips.put(s, true);
		}
		for(String name:newnames){
			j2.debug("Found Name: "+name);
			names.put(name, false);
		}
		return names;
	}

	public String getKnown(String name){
		if(html.containsKey(name)){
			return html.get(name);
		}
		return "";
	}
	public int getTotal(String name){
		if(totalcount.containsKey(name)){
			return totalcount.get(name);
		}
		return 0;
	}
	public int getBanned(String name){
		if(bannedcount.containsKey(name)){
			return bannedcount.get(name);
		}
		return 0;
	}
	public void processJoin(String name){
		if(badlist.contains(name)){
			int total=this.getTotal(name)-1;
			int banned=this.getBanned(name);
			if(!j2.hasFlag(name, Flag.QUIETERJOIN)){
				j2.irc.ircAdminMsg("[J2BANS] "+name+" matches "+total+" others: "+banned+" banned");
			}
			j2.chat.msgByFlag(Flag.ADMIN, ChatColor.LIGHT_PURPLE+"[J2BANS] "+ChatColor.WHITE+name+ChatColor.LIGHT_PURPLE+" matches "+total+" others: "+banned+" banned");
		}
	}
}
