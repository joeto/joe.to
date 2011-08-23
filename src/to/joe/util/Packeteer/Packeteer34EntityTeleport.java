package to.joe.util.Packeteer;

import net.minecraft.server.Packet34EntityTeleport;

import org.bukkit.entity.Player;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.packet.listener.PacketListener;
import org.getspout.spoutapi.packet.standard.MCPacket;

import to.joe.util.Vanish;

public class Packeteer34EntityTeleport implements PacketListener {

    private final Vanish vanish;

    public Packeteer34EntityTeleport(Vanish vanish) {
        this.vanish = vanish;
    }

    @Override
    public boolean checkPacket(Player player, MCPacket packet) {
        return !this.vanish.shouldHide(player, ((Packet34EntityTeleport) ((MCCraftPacket) packet).getPacket()).a);
    }

}
