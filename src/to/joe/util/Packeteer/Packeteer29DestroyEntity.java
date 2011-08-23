package to.joe.util.Packeteer;

import net.minecraft.server.Packet29DestroyEntity;

import org.bukkit.entity.Player;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.packet.listener.PacketListener;
import org.getspout.spoutapi.packet.standard.MCPacket;

import to.joe.util.Vanish;

public class Packeteer29DestroyEntity implements PacketListener {

    private final Vanish vanish;

    public Packeteer29DestroyEntity(Vanish vanish) {
        this.vanish = vanish;
    }

    @Override
    public boolean checkPacket(Player player, MCPacket packet) {
        return !this.vanish.shouldHide(player, ((Packet29DestroyEntity) ((MCCraftPacket) packet).getPacket()).a);
    }

}
