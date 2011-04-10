package to.joe.manager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import to.joe.J2Plugin;

public class WebPage {
	private J2Plugin j2;
	private int servernum;
	public WebPage(J2Plugin j2){
		this.j2=j2;
	}
	public void go(int servnum){
		startUpdateTimer1();
		this.servernum=servnum;
		//startUpdateTimer2();
	}
	private boolean stop;
	public boolean restart = false;
	public void startUpdateTimer1() {
		stop = false;
		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (stop) {
					timer.cancel();
					return;
				}
				update5Second();
			}
		}, 1000, 5000);
	}
	/*public void startUpdateTimer2() {
		stop = false;
		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (stop) {
					timer.cancel();
					return;
				}
				update20();
			}
		}, 1000, 5000);
	}*/
	private void update5Second(){
		ArrayList<Integer> watched=new ArrayList<Integer>(j2.watchlist);
		HashMap<Integer,String> output=new HashMap<Integer,String>();
		for(Integer i:watched){
			output.put(i,"");
		}
		String playerlist="";
		int playercount=0;
		String aliaslist="";
		for(Player p : j2.getServer().getOnlinePlayers()){
			if(p!=null){
				playercount++;
				playerlist+=" "+p.getDisplayName();
				Inventory check=p.getInventory();
				String name=p.getName();
				for(Integer i:watched){
					if(check.contains(i, 10)){
						String temp=new String(output.get(i));
						output.remove(i);
						temp+=" "+name;
						output.put(i, temp);
					}
				}
				String known=j2.ip.getKnown(p.getName());
				if(!known.equals("")){
					aliaslist+=known+"<br>";
				}
			}
		}
		try {
		    BufferedWriter out = new BufferedWriter(new FileWriter("/home/minecraft/public_html/detector/"+servernum+"/current.txt"));
		    out.write("Players("+playercount+"/"+j2.playerLimit+"):"+playerlist+"<br /><br /><br />");
		    out.write("<table>");
		    for(Integer i:watched){
		    	out.write("<tr><td>"+Material.getMaterial(i)+"</td><td>"+output.get(i)+"</td></tr>");
		    }
		    out.write("</table><br />");
		    if(!aliaslist.equals("")){
		    	out.write("<table>");
		    	out.write(aliaslist);
		    	out.write("</table>");
		    }
		    out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
		    BufferedWriter out = new BufferedWriter(new FileWriter("/home/minecraft/public_html/"+servernum+"/current.txt"));
		    out.write("Players("+playercount+"/"+j2.playerLimit+"):"+playerlist+"<br />");
		    out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
