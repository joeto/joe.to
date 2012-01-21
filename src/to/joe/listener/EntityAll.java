package to.joe.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import to.joe.J2;
import to.joe.util.Flag;

public class EntityAll extends EntityListener {
    private final J2 j2;

    public EntityAll(J2 j2) {
        this.j2 = j2;
    }

    @Override
    public void onEntityExplode(EntityExplodeEvent event) {
        if (!this.j2.config.world_allow_explosions) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onEntityDamage(EntityDamageEvent event) {
        final Entity smacked = event.getEntity();
        final DamageCause smacker = event.getCause();

        if (smacked instanceof Player) { // player has been hit!
            final Player player = (Player) smacked;
            if (this.j2.minitrue.invisible(player)) {
                event.setCancelled(true);
                return;
            }
            if (event instanceof EntityDamageByEntityEvent) {// kickaxe time
                final EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) event;
                final Entity damager = ev.getDamager();
                if (damager instanceof Player) {
                    final Player pd = (Player) damager;
                    if (this.j2.hasFlag(pd, Flag.SRSTAFF)) {
                        if (pd.getInventory().getItemInHand().getType().equals(Material.IRON_AXE)) {
                            this.j2.kickbans.callKick(player.getName(), pd.getName(), "IN DA FACE", true);
                            return;
                        }
                    }
                }
            }
            if (smacker.equals(DamageCause.ENTITY_ATTACK)) {// pvp
                if (this.j2.damage.PvPsafe.contains(player.getName())) {
                    event.setCancelled(true);
                    return;
                }
            } else {// pve
                if (this.j2.damage.PvEsafe.contains(player.getName())) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
        if (event instanceof EntityDamageByEntityEvent) {
            final EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) event;
            final Entity damager = ev.getDamager();
            if (damager instanceof Player) {
                final Player player = (Player) damager;
                if (this.j2.minitrue.invisible(player)) {
                    event.setCancelled(true);
                    return;
                }
                if(this.j2.config.general_server_number==3 && smacked instanceof Wolf){
                    Wolf woof=(Wolf)smacked;
                    if(woof.isTamed()){
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
        if (smacked instanceof Wolf) {
            if (event instanceof EntityDamageByEntityEvent) {
                final EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) event;
                final Entity damager = ev.getDamager();
                if ((damager instanceof Player) && this.j2.config.general_website_enable) {// YOU MONSTER
                    final Location loc = damager.getLocation();
                    final int x = (int) loc.getX();
                    final int y = (int) loc.getY();
                    final int z = (int) loc.getZ();
                    this.j2.dogLog.add("[WOOF] " + ((Player) damager).getName() + " smacked a wolf for " + event.getDamage() + " damage at " + x + " " + y + " " + z);
                }
            }
        }
    }

    @Override
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getCreatureType().equals(CreatureType.WOLF) && this.j2.config.world_disable_wolves) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onEntityDeath(EntityDeathEvent event) {
        final Entity died = event.getEntity();
        if (died instanceof Player) {
            this.j2.damage.arf(((Player) died).getName());
        }
    }

    @Override
    public void onItemSpawn(ItemSpawnEvent event) {
        if (this.j2.config.general_server_number == 2) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onEntityTarget(EntityTargetEvent event) {
        if ((event.getTarget() instanceof Player) && this.j2.minitrue.invisible((Player) event.getTarget())) {
            event.setCancelled(true);
        }
    }

}
