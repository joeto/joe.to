package to.joe.util.Packeteer;

import net.minecraft.server.Packet17;

import org.bukkit.entity.Player;
import org.getspout.spoutapi.packet.listener.PacketListener;
import org.getspout.spoutapi.packet.standard.MCPacket;
import org.getspout.spout.packet.standard.MCCraftPacket;

import to.joe.util.Vanish;

public class Packeteer17 implements PacketListener {

    private final Vanish vanish;

    public Packeteer17(Vanish vanish) {
        this.vanish = vanish;
    }

    @Override
    public boolean checkPacket(Player player, MCPacket packet) {

        return !this.vanish.shouldHide(player, ((Packet17) ((MCCraftPacket) packet).getPacket()).a);
    }

}
