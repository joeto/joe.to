package to.joe.listener;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import to.joe.J2;

public class PlayerMovement extends PlayerListener {
    private J2 j2;

    public PlayerMovement(J2 j2) {
        this.j2 = j2;
    }

    @Override
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        if (j2.jail.processAction(player)) {
            event.setCancelled(true);
            return;
        }
        if (j2.damage.allWolf.containsKey(player.getName())) {
            event.setCancelled(true);
        }
        World world = player.getWorld();
        Chunk chunk = world.getChunkAt(event.getTo());
        int chunkx = chunk.getX();
        int chunkz = chunk.getZ();
        world.refreshChunk(chunkx, chunkz);
        this.j2.minitrue.vanish.updateInvisibleOnTimer();
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        // j2.move.move(player);
        if (j2.jail.processAction(player)) {
            event.setCancelled(true);
            return;
        }
        j2.activity.update(player);
    }
}
