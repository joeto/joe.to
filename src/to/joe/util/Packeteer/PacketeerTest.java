package to.joe.util.Packeteer;

import net.minecraft.server.Packet255KickDisconnect;

import org.bukkit.entity.Player;
import org.getspout.spoutapi.packet.listener.PacketListener;
import org.getspout.spoutapi.packet.standard.MCPacket;
import org.getspout.spout.packet.standard.MCCraftPacket;

import to.joe.util.Vanish;

public class PacketeerTest implements PacketListener {

    private final Vanish vanish;

    public PacketeerTest(Vanish vanish) {
        this.vanish = vanish;
    }

    @Override
    public boolean checkPacket(Player player, MCPacket packet) {
        
        System.out.println("Meow " +((Packet255KickDisconnect) ((MCCraftPacket) packet).getPacket()).a);
        return true;
    }

}
