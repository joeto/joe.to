package to.joe;

import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;

public class listenEntity extends EntityListener {
	private J2Plugin j2;
	public listenEntity(J2Plugin j2){
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
		if(j2.safemode){
			event.setCancelled(true);
		}
	}
}
