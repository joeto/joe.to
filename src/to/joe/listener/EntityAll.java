package to.joe.listener;

import org.bukkit.Material;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import to.joe.J2Plugin;
import to.joe.util.Flag;

public class EntityAll extends EntityListener {
	private J2Plugin j2;

	public EntityAll(J2Plugin j2){
		this.j2=j2;
	}
	@Override
	public void onEntityExplode(EntityExplodeEvent event) {
		if(j2.safemode){
			event.setCancelled(true);
		}
	}
	@Override
	public void onEntityDamage(EntityDamageEvent event) {
		Entity smacked=event.getEntity();
		DamageCause smacker=event.getCause();

	//	if(event.getEntity() instanceof Player && event instanceof EntityDamageByEntityEvent 
	//			&& ((EntityDamageByEntityEvent)event).getDamager() instanceof Player
	//			&& ((Player)((EntityDamageByEntityEvent)event).getDamager()).getInventory().getItemInHand().getType().equals(Material.IRON_AXE)
	//			/*&& PERMISSIONSCHECK*/){
	//		/*KICKCODE; 
	//		 * Such as*/
	//		((Player)event.getEntity()).kickPlayer("IN DA FACE");
	//		
	//	}

		if(smacked instanceof Player){ //player has been hit!
			Player player=(Player)smacked;
			if(event instanceof EntityDamageByEntityEvent){//kickaxe time
				EntityDamageByEntityEvent ev=(EntityDamageByEntityEvent)event;
				Entity damager=ev.getDamager();
				if(damager instanceof Player){
					Player pd=(Player)damager;
					if(j2.hasFlag(pd, Flag.SRSTAFF)){
						if(pd.getInventory().getItemInHand().getType().equals(Material.IRON_AXE)){
							j2.kickbans.callKick(player.getName(), pd.getName(), "IN DA FACE",true);
						}
					}
				}
			}
			if(smacker.equals(DamageCause.ENTITY_ATTACK)){//pvp
				if(j2.damage.PvPsafe.contains(player.getName())){
					event.setCancelled(true);
				}
			}
			else{//pve
				if(j2.damage.PvEsafe.contains(player.getName())){
					event.setCancelled(true);
				}
			}
		}
	}

	@Override
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if(event.getCreatureType().equals(CreatureType.WOLF)&&j2.ihatewolves){
			event.setCancelled(true);
		}
	}

	@Override
	public void onEntityDeath(EntityDeathEvent event) {
		Entity died=event.getEntity();
		if(died instanceof Player){
			j2.damage.arf(((Player)died).getName());
		}
	}
}