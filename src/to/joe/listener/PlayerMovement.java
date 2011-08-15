package to.joe.listener;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import to.joe.J2;

public class PlayerMovement extends PlayerListener {
    private final J2 j2;

    public PlayerMovement(J2 j2) {
        this.j2 = j2;
    }

    @Override
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        final Player player = event.getPlayer();

        if (this.j2.jail.processAction(player)) {
            event.setCancelled(true);
            return;
        }
        if (this.j2.damage.allWolf.containsKey(player.getName())) {
            event.setCancelled(true);
        }
        final World world = player.getWorld();
        final Chunk chunk = world.getChunkAt(event.getTo());
        final int chunkx = chunk.getX();
        final int chunkz = chunk.getZ();
        world.refreshChunk(chunkx, chunkz);
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        // j2.move.move(player);
        if (this.j2.jail.processAction(player)) {
            event.setCancelled(true);
            return;
        }
        this.j2.activity.update(player);
    }
}
