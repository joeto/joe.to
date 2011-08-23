package to.joe.util.Packeteer;

import net.minecraft.server.Packet28EntityVelocity;

import org.bukkit.entity.Player;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.packet.listener.PacketListener;
import org.getspout.spoutapi.packet.standard.MCPacket;

import to.joe.util.Vanish;

public class Packeteer28EntityVelocity implements PacketListener {

    private final Vanish vanish;

    public Packeteer28EntityVelocity(Vanish vanish) {
        this.vanish = vanish;
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
        return !this.vanish.shouldHide(player, ((Packet28EntityVelocity) ((MCCraftPacket) packet).getPacket()).a);
    }

}
