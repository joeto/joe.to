package to.joe.util.Packeteer;

import net.minecraft.server.Packet17EntityLocationAction;

import org.bukkit.entity.Player;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.packet.listener.PacketListener;
import org.getspout.spoutapi.packet.standard.MCPacket;

import to.joe.util.Vanish;

public class Packeteer17EntityLocationAction implements PacketListener {

    private final Vanish vanish;

    public Packeteer17EntityLocationAction(Vanish vanish) {
        this.vanish = vanish;
    }

    @Override
    public boolean checkPacket(Player player, MCPacket packet) {

        return !this.vanish.shouldHide(player, ((Packet17EntityLocationAction) ((MCCraftPacket) packet).getPacket()).a);
    }

}
