package to.joe.util.Packeteer;

import java.util.ArrayList;

import net.minecraft.server.Packet39AttachEntity;

import org.bukkit.entity.Player;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.packet.listener.PacketListener;
import org.getspout.spoutapi.packet.standard.MCPacket;

import to.joe.util.Vanish;

public class Packeteer39AttachEntity implements PacketListener {

    private final Vanish vanish;

    public Packeteer39AttachEntity(Vanish vanish) {
        this.eidVanished = new ArrayList<Integer>();
        this.vanish = vanish;
    }

    private final Object eidSync = new Object();
    private final ArrayList<Integer> eidVanished;

    public void addVanished(int id) {
        synchronized (this.eidSync) {
            this.eidVanished.add(id);
        }
    }

    public void removeVanished(int id) {
        synchronized (this.eidSync) {
            this.eidVanished.remove(id);
        }
    }

    /*
     * Return true if it should go through, false to block
     * 
     * Packets tracked: 5 - Equipment change 17 - Beds? Maybe 18 - ArmAnimation
     * 19 - EntityAction 20 - NamedEntitySpawn 28 - EntityVelocity 29 -
     * DestroyEntity 30 - Entity (sent for did not move) 31 -
     * EntityRelativeMovement 32 - EntityLook 33 - RelEntityMoveLook 34 -
     * EntityTeleport 38 - EntityStatus 39 - AttachEntity
     */
    @Override
    public boolean checkPacket(Player player, MCPacket packet) {
        return !this.vanish.shouldHide(player, ((Packet39AttachEntity) ((MCCraftPacket) packet).getPacket()).a);
    }

}
