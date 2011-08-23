package to.joe.util.Packeteer;

import net.minecraft.server.Packet31RelEntityMove;

import org.bukkit.entity.Player;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.packet.listener.PacketListener;
import org.getspout.spoutapi.packet.standard.MCPacket;

import to.joe.util.Vanish;

public class Packeteer31RelEntityMove implements PacketListener {

    private final Vanish vanish;

    public Packeteer31RelEntityMove(Vanish vanish) {
        this.vanish = vanish;
    }

    @Override
    public boolean checkPacket(Player player, MCPacket packet) {
        return !this.vanish.shouldHide(player, ((Packet31RelEntityMove) ((MCCraftPacket) packet).getPacket()).a);
    }

}
