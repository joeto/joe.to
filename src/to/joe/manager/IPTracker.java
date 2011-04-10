package to.joe.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.ChatColor;

import to.joe.J2Plugin;
import to.joe.util.Flag;

public class IPTracker {
	private J2Plugin j2;
	private HashMap<String,String> known;

	public IPTracker(J2Plugin j2){
		this.j2=j2;
		this.known=new HashMap<String,String>();
	}
	public void incoming(String name_p, String IP){
		String name=name_p.toLowerCase();
		if(j2.debug)
			System.out.println("Checking "+name);
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
		known.remove(name);
		if(names.size()>1){
			String nameslist="";
			boolean ohnoes=false;
			for(String n:names.keySet()){
				if(!n.equals(name)){
					if(j2.mysql.checkBans(n)==null){
						nameslist+=n+" ";
					}
					else{
						nameslist+="<span style='color:red'>"+n+"</span> ";
						ohnoes=true;
					}
				}
			}
			String newknown="<tr><td>"+name+"</td><td>"+nameslist+"<br><a href='../alias/detector.php?name="+name+"'>Map</a></td></tr>";
			known.put(name, newknown);
			if(ohnoes){
				j2.irc.ircAdminMsg("User "+name+" matches banned players. Watch "+name+"");
				j2.chat.msgByFlag(Flag.ADMIN, ChatColor.LIGHT_PURPLE+"User "+ChatColor.WHITE+name+ChatColor.LIGHT_PURPLE+" matches banned players.");
			}
			if(j2.debug)
				System.out.println("Adding to list");
		}
		else{
			if(j2.debug)
				System.out.println("Not enough to add");
		}
	}
	public HashMap<String,Boolean> getIPs(HashMap<String,Boolean> names,HashMap<String,Boolean> ips){
		Set<String> keyset = names.keySet();
		ArrayList<String> newips=new ArrayList<String>();
		ArrayList<String> searched=new ArrayList<String>();
		for(String key:keyset){
			if(!names.get(key)){
				searched.add(key);
				ArrayList<String> tempips=j2.mysql.IPGetIPs(key);
				for(String i:tempips){
					if(!i.equals("")&&!newips.contains(i)&&!keyset.contains(i)){
						newips.add(i);
					}
				}
			}
		}
		for(String s:searched){
			ips.remove(s);
			ips.put(s, true);
		}
		for(String ip:newips){
			if(j2.debug)
				System.out.println("Found IP: "+ip);
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
				ArrayList<String> tempnames=j2.mysql.IPGetNames(key);
				for(String i:tempnames){
					if(!i.equals("")&&!newnames.contains(i)&&!keyset.contains(i)){
						newnames.add(i);
					}
				}
			}
		}
		for(String s:searched){
			ips.remove(s);
			ips.put(s, true);
		}
		for(String name:newnames){
			if(j2.debug)
				System.out.println("Found Name: "+name);
			names.put(name, false);
		}
		return names;
	}

	public String getKnown(String name){
		if(known.containsKey(name)){
			return known.get(name);
		}
		return "";
	}
}
