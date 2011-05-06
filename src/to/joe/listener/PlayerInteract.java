package to.joe.listener;


import org.bukkit.ChatColor;
//import org.bukkit.Location;
import org.bukkit.Material;
//import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
//import org.bukkit.inventory.ItemStack;

import to.joe.J2Plugin;
import to.joe.util.Flag;


public class PlayerInteract extends PlayerListener {
	private final J2Plugin j2;

	public PlayerInteract(J2Plugin instance) {
		j2 = instance;
	}

	@Override
	public void onPlayerInteract(PlayerInteractEvent event){
		Player player = event.getPlayer();
		Material material=event.getMaterial();
		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
				||event.getAction().equals(Action.RIGHT_CLICK_AIR)){	
			int type=material.getId();
			if(type==0)
				return;
			if(!j2.hasFlag(player, Flag.MODWORLD)){
				player.sendMessage("You don't have permission to do that");
				event.setCancelled(true);
				return;
			}
			if(j2.hasFlag(player, Flag.TRUSTED) && !j2.hasFlag(player,Flag.ADMIN) && j2.isOnSuperBlacklist(type)){
				player.sendMessage(ChatColor.RED+"Even trusted have limits. Can't do that.");
				event.setCancelled(true);
				return;
			}
			if(!j2.hasFlag(player, Flag.TRUSTED) && (j2.isOnRegularBlacklist(type)||j2.isOnSuperBlacklist(type))){
				player.sendMessage(ChatColor.RED+"You need to be trusted or higher to do that.");
				event.setCancelled(true);
				return;
			}
		}
		if(material.equals(Material.STICK)&&j2.hasFlag(player,Flag.TOOLS)){
			if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
				if(j2.debug)j2.log.info(player.getName()+" used a stick");
				event.setCancelled(true);
				//		managerBlockLog.bqueue.offer(new BlockRow(player.getDisplayName(),event.getBlock().getTypeId(),0,event.getBlock().getX(),event.getBlock().getY(),event.getBlock().getZ(),(System.currentTimeMillis()/1000L),null));
				event.getClickedBlock().setTypeId(0);
			}
			if(event.getAction().equals(Action.LEFT_CLICK_BLOCK)){
				Block b=event.getClickedBlock();
				event.getPlayer().sendMessage("Boom");
	            int x=b.getX();
	            int z=b.getZ();
	            int y=b.getY()+1;
	            j2.log.info("1X1 by "+player.getName()+" at "+x+" "+y+" "+z);
	            while(y<128){
	            	b.getWorld().getBlockAt(x, y, z).setTypeId(0);
	            	y++;
	            }
			}
		}
		/*if(material.equals(Material.PORK)&&j2.hasFlag(player, Flag.TOOLS)&&
				(event.getAction().equals(Action.LEFT_CLICK_AIR)||event.getAction().equals(Action.LEFT_CLICK_BLOCK))){
			Block targetb=player.getTargetBlock(null, 50);
			if(targetb!=null){
				World world=targetb.getWorld();
				Location location=targetb.getLocation();
				for(int x=0;x<5;x++)
					world.dropItemNaturally(location, new ItemStack(Material.PORK,1));
			}
		}*/
		if(j2.hasFlag(player, Flag.CUSTOM_THOR)&&event.hasItem()&&material.equals(Material.IRON_SWORD)){
			boolean weather=player.getWorld().isThundering();
			if(event.getAction().equals(Action.LEFT_CLICK_BLOCK)){
				player.getWorld().strikeLightning(event.getClickedBlock().getLocation());
				player.getWorld().setStorm(weather);
			}
			if(event.getAction().equals(Action.LEFT_CLICK_AIR)){
				Block target=player.getTargetBlock(null, 50);
				if(target!=null){
					player.getWorld().strikeLightning(target.getLocation());
					player.getWorld().setStorm(weather);
				}
			}
		}
	}

	/*@Override
	public void onPlayerItem(PlayerItemEvent event){
		Player player = event.getPlayer();
		int type=event.getMaterial().getId();
		if(!j2.hasFlag(player, Flag.MODWORLD)){
			player.sendMessage("You don't have permission to do that");
			event.setCancelled(true);
			return;
		}
		if(j2.hasFlag(player, Flag.TRUSTED) && !j2.hasFlag(player,Flag.ADMIN) && j2.isOnSuperBlacklist(type)){
			player.sendMessage(ChatColor.RED+"Even trusted have limits. Can't do that.");
			event.setCancelled(true);
			return;
		}
		if(!j2.hasFlag(player, Flag.TRUSTED) && (j2.isOnRegularBlacklist(type)||j2.isOnSuperBlacklist(type))){
			player.sendMessage(ChatColor.RED+"You need to be trusted or higher to do that.");
			event.setCancelled(true);
			return;
		}

	 */



	/*if(type==259||type==291||type==292||type==293||type==294||type==295||type==321||type==324||type==325
				||type==326||type==327||type==330||type==331||type==354||type==355||type==356){
			BlockRow changed;
			Block clicked = event.getBlockClicked();
			BlockFace face=event.getBlockFace();
			int x=clicked.getX()+face.getModX();
			int y=clicked.getY()+face.getModY();
			int z=clicked.getZ()+face.getModZ();
			changed = new BlockRow(player.getDisplayName(),0,type,x,y,z,(System.currentTimeMillis()/1000L),null);
			managerBlockLog.bqueue.offer(changed);
		}*/
	//}

	//public void onBlockRightClick(BlockRightClickEvent event)
	//{
	//	Player player = event.getPlayer();

	/*if(event.getItemInHand().getTypeId() == 284 && j2.hasFlag(player, Flag.ADMIN))
		{
			if(j2.debug)j2.log.info(player.getName()+ " used gold shovel");
			this.j2.blogger.showBlockHistory(event.getPlayer(), event.getBlock());
		}*/
	//	if(event.getItemInHand().getTypeId() == 280 && j2.hasFlag(player, Flag.ADMIN)){

	//		if(j2.debug)j2.log.info(player.getName()+" used a stick");
	//		managerBlockLog.bqueue.offer(new BlockRow(player.getDisplayName(),event.getBlock().getTypeId(),0,event.getBlock().getX(),event.getBlock().getY(),event.getBlock().getZ(),(System.currentTimeMillis()/1000L),null));
	//		event.getBlock().setTypeId(0);
	//	}
	//System.out.println("Item type id ="+event.getItemInHand().getTypeId() );

	//}
}
