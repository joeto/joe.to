package to.joe;

import java.util.ArrayList;

public class warpManager {
	public warpManager(J2Plugin J2){
		this.warps=new ArrayList<warp>();
		this.homes=new ArrayList<warp>();
		this.j2=J2;
	}
	public void addWarp(warp warp){
		
	}
	public void killWarp(warp warp){
		
	}
	public void loadPlayer(String playername){
		
	}
	public void dropPlayer(String playername){
		for(warp home:this.homes){
			if(home.getPlayer().equalsIgnoreCase(playername)){
				homes.remove(home);
			}
		}
	}
	public warp getUserWarp(String playername,String warpname){
		for(warp warp:this.homes){
			if(warp.getName().equalsIgnoreCase(warpname)&&warp.getPlayer().equalsIgnoreCase(playername)){
				return warp;
			}
		}
		return null;
	}
	public warp getPublicWarp(String warpname){
		for(warp warp:this.warps){
			if(warp.getName().equalsIgnoreCase(warpname)){
				return warp;
			}
		}
		return null;
	}
	public ArrayList<warp> getUserWarps(String playername){
		ArrayList<warp> toReturn = new ArrayList<warp>();
		for(warp home:this.homes){
			if(home.getPlayer().equalsIgnoreCase(playername)){
				toReturn.add(home);
			}
		}
		return toReturn;
	}
	public ArrayList<warp> getPublicWarps(){
		return this.warps;
	}
	
	private ArrayList<warp> warps;
	private ArrayList<warp> homes;
	private J2Plugin j2;
}
