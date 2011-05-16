package to.joe.manager;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import to.joe.util.User;
import to.joe.J2Plugin;

public class MoveTracker {
	private J2Plugin j2;
	public MoveTracker(J2Plugin j2){
		this.j2=j2;
	}
	public void move(Player player){
		User user=j2.users.getUser(player);
		Location location=player.getLocation();
		Block block =location.getBlock();
		if(!user.getBlock().equals(block)){
			/*if(player.getName().equalsIgnoreCase("mbaxter")&&j2.safemode){
				Location target=user.getMidBlock().getLocation();
				Location targetdown=target;
				targetdown.setY(target.getY()-1D);
				World world=j2.getServer().getWorld(target.getWorld().getName());
				if(world.getBlockAt(target).getType().equals(Material.AIR)&&
						!isFlammable(world.getBlockAt(targetdown).getType())){
					world.getBlockAt(target).setType(Material.FIRE);
				}
				if(world.getBlockAt(user.getLastBlock().getLocation()).getType().equals(Material.FIRE)){
					world.getBlockAt(user.getLastBlock().getLocation()).setType(Material.AIR);
				}
			}*/
			user.setCurLoc(block);
		}
	}
	
	public boolean isFlammable(Material material){
		if(material.equals(Material.BOOKSHELF))return true;
		if(material.equals(Material.WOOD))return true;
		if(material.equals(Material.WOOL))return true;
		if(material.equals(Material.NETHERRACK))return true;
		if(material.equals(Material.LOG))return true;
		return false;
	}
}
