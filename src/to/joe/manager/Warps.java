package to.joe.manager;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.util.Flag;
import to.joe.util.Warp;

public class Warps {
	public Warps(J2 J2){
		this.restartManager();
		this.j2=J2;
	}
	public void restartManager(){
		this.warps=new ArrayList<Warp>();
		this.homes=new ArrayList<Warp>();
	}
	public void addWarp(Warp warp){
		j2.mysql.addWarp(warp);
		synchronized(this.lock){
			if(warp.getFlag().equals(Flag.Z_HOME_DESIGNATION)){
				this.homes.add(warp);
			}
			else{
				this.warps.add(warp);
			}
		}
	}
	public void addWarpViaMysql(Warp warp){
		synchronized(this.lock){
			if(warp.getFlag().equals(Flag.Z_HOME_DESIGNATION)){
				this.homes.add(warp);
			}
			else{
				this.warps.add(warp);
			}
		}
	}
	public void killWarp(Warp warp){
		synchronized(this.lock){
			if(warp.getFlag().equals(Flag.Z_HOME_DESIGNATION)){
				this.homes.remove(warp);
			}
			else{
				this.warps.remove(warp);
			}
			this.j2.mysql.removeWarp(warp);
		}
	}
	public void processJoin(String name){
		this.loadPlayer(name);
	}

	public void loadPlayer(String playername){
		ArrayList<Warp> playerhomes=this.j2.mysql.getHomes(playername);
		synchronized(this.lock){
			if(playerhomes!=null)
				this.homes.addAll(playerhomes);
		}
	}

	public void dropPlayer(String playername){
		synchronized(this.lock){
			ArrayList<Warp> toRemove=new ArrayList<Warp>();
			for(Warp home:this.homes){
				if(home.getPlayer().equalsIgnoreCase(playername)){
					toRemove.add(home);
				}
			}
			this.homes.removeAll(toRemove);
		}
	}
	public Warp getUserWarp(String playername,String warpname){
		synchronized(this.lock){
			for(Warp warp:this.homes){

				if(warp.getName().equalsIgnoreCase(warpname)&&warp.getPlayer().equalsIgnoreCase(playername)){
					return warp;
				}
			}
			return null;
		}
	}
	public Warp getPublicWarp(String warpname){
		synchronized(this.lock){
			for(Warp warp:this.warps){
				if(warp.getName().equalsIgnoreCase(warpname)){
					return warp;
				}
			}
			return null;
		}
	}
	public ArrayList<Warp> getUserWarps(String playername){
		synchronized(this.lock){
			ArrayList<Warp> toReturn = new ArrayList<Warp>();

			for(Warp home:this.homes){
				if(home.getPlayer().equalsIgnoreCase(playername)){
					toReturn.add(home);
				}
			}
			return toReturn;
		}
	}
	public ArrayList<Warp> getPublicWarps(){
		synchronized(this.lock){
			return new ArrayList<Warp>(this.warps);
		}
	}

	public String listHomes(String playername){
		synchronized(this.lock){
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
	}

	public String listWarps(Player player){
		synchronized(this.lock){
			ArrayList<Warp> warps_u=this.getPublicWarps();

			String warps_s = "";
			if(warps_u!=null){
				j2.debug("Found "+warps_u.size()+" warps");
				for(Warp warp_i:warps_u){
					Flag flag=warp_i.getFlag();
					j2.debug(warp_i.getName()+" has flag "+warp_i.getFlag().getChar());
					if(warp_i!=null && (j2.hasFlag(player, flag)||flag.equals(Flag.Z_SPAREWARP_DESIGNATION))){
						warps_s+=", "+warp_i.getName();
					}
				}
				if(!warps_s.equalsIgnoreCase("")){
					warps_s=warps_s.substring(2);//remove the first comma/space
				}
			}
			return warps_s;
		}
	}
	public Warp getClosestWarp(Location location){
		ArrayList<Warp> allWarps=this.getPublicWarps();
		double xstart=location.getX();
		double ystart=location.getY();
		double zstart=location.getZ();
		int distance=0;
		Warp solution = null;
		for(Warp warp:allWarps){
			if(warp!=null){
				if(solution!=null){
					Location locwarp=warp.getLocation();
					double xwarp=locwarp.getX();
					double ywarp=locwarp.getY();
					double zwarp=locwarp.getZ();
					int dist=(int)Math.pow((Math.pow(xwarp-xstart, 2)+Math.pow(ywarp-ystart, 2)+Math.pow(zwarp-zstart, 2)), 0.5);
					if(dist<distance){
						distance=dist;
						solution=warp;
					}
				}
				else{
					solution=warp;
					Location locwarp=warp.getLocation();
					double xwarp=locwarp.getX();
					double ywarp=locwarp.getY();
					double zwarp=locwarp.getZ();
					distance=(int)Math.pow((Math.pow(xwarp-xstart, 2)+Math.pow(ywarp-ystart, 2)+Math.pow(zwarp-zstart, 2)), 0.5);
				}
			}
		}
		return solution;
	}

	private ArrayList<Warp> warps;
	private ArrayList<Warp> homes;
	private J2 j2;
	private Object lock=new Object();
}
