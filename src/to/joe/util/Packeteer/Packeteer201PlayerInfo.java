package to.joe.util.Packeteer;

import net.minecraft.server.Packet201PlayerInfo;

import org.bukkit.entity.Player;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.packet.listener.PacketListener;
import org.getspout.spoutapi.packet.standard.MCPacket;

public class Packeteer201PlayerInfo implements PacketListener {


    @Override
    public boolean checkPacket(Player player, MCPacket packet) {
        return ((Packet201PlayerInfo) ((MCCraftPacket) packet).getPacket()).a.equals("Nyan");
    }

}
