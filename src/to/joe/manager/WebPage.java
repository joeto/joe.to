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
	J2Plugin j2;
	public WebPage(J2Plugin j2){
		this.j2=j2;
		
	}
	public void go(){
		if(j2.mysql.servnum()==2)
			this.startUpdateTimer();
	}
	private boolean stop;
	public boolean restart = false;
	public void startUpdateTimer() {
		stop = false;
		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (stop) {
					timer.cancel();
					return;
				}
				update();
			}
		}, 1000, 5000);
	}
	private void update(){
		ArrayList<Integer> watched=new ArrayList<Integer>(j2.watchlist);
		HashMap<Integer,String> output=new HashMap<Integer,String>();
		for(Integer i:watched){
			output.put(i,"");
		}
		String playerlist="";
		int playercount=0;
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
			}
		}
		try {
		    BufferedWriter out = new BufferedWriter(new FileWriter("/home/minecraft/public_html/detector/current.txt"));
		    out.write("Players("+playercount+"/"+j2.playerLimit+"):"+playerlist+"<br /><br /><br />");
		    out.write("<table>");
		    for(Integer i:watched){
		    	out.write("<tr><td>"+Material.getMaterial(i)+"</td><td>"+output.get(i)+"</td></tr>");
		    }
		    out.write("</table><br />");
		    out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
		    BufferedWriter out = new BufferedWriter(new FileWriter("/home/minecraft/public_html/current.txt"));
		    out.write("Players("+playercount+"/"+j2.playerLimit+"):"+playerlist+"<br />");
		    out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
