package to.joe;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public class managerWarps {
	public managerWarps(J2Plugin J2){
		this.warps=new ArrayList<Warp>();
		this.homes=new ArrayList<Warp>();
		this.j2=J2;
	}
	public void addWarp(Warp warp){
		j2.mysql.addWarp(warp);
		if(warp.getFlag().equals(Flag.Z_HOME_DESIGNATION)){
			this.homes.add(warp);
		}
		else{
			this.warps.add(warp);
		}
	}
	public void killWarp(Warp warp){
		if(warp.getFlag().equals(Flag.Z_HOME_DESIGNATION)){
			this.homes.remove(warp);
		}
		else{
			this.warps.remove(warp);
		}
		this.j2.mysql.removeWarp(warp);
	}
	public void loadPlayer(String playername){
		ArrayList<Warp> playerhomes=this.j2.mysql.getHomes(playername);
		if(playerhomes!=null)
			this.homes.addAll(playerhomes);
	}

	public void dropPlayer(String playername){
		for(Warp home:this.homes){
			if(home.getPlayer().equalsIgnoreCase(playername)){
				this.homes.remove(home);
			}
		}
	}
	public Warp getUserWarp(String playername,String warpname){
		for(Warp warp:this.homes){
			if(warp.getName().equalsIgnoreCase(warpname)&&warp.getPlayer().equalsIgnoreCase(playername)){
				return warp;
			}
		}
		return null;
	}
	public Warp getPublicWarp(String warpname){
		for(Warp warp:this.warps){
			if(warp.getName().equalsIgnoreCase(warpname)){
				return warp;
			}
		}
		return null;
	}
	public ArrayList<Warp> getUserWarps(String playername){
		ArrayList<Warp> toReturn = new ArrayList<Warp>();
		for(Warp home:this.homes){
			if(home.getPlayer().equalsIgnoreCase(playername)){
				toReturn.add(home);
			}
		}
		return toReturn;
	}
	public ArrayList<Warp> getPublicWarps(){
		return this.warps;
	}

	public String listHomes(String playername){
		ArrayList<Warp> homes_u=getUserWarps(playername);
		String homes_s = "";
		if(homes_u!=null){

			for(Warp home:homes_u){
				if(home!=null){
					homes_s+=", "+home.getName();
				}
			}
			if(!homes_s.equalsIgnoreCase("")){
				homes_s=homes_s.substring(2);//remove the first comma/space
			}
		}
		return homes_s;
	}

	public String listWarps(Player player){
		ArrayList<Warp> warps_u=getUserWarps(player.getName());
		String warps_s = "";
		if(warps_u!=null){

			for(Warp warp_i:warps_u){
				if(warp_i!=null && j2.hasFlag(player, warp_i.getFlag())){
					warps_s+=", "+warp_i.getName();
				}
			}
			if(!warps_s.equalsIgnoreCase("")){
				warps_s=warps_s.substring(2);//remove the first comma/space
			}
		}
		return warps_s;
	}

	private ArrayList<Warp> warps;
	private ArrayList<Warp> homes;
	private J2Plugin j2;
}
