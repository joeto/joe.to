package to.joe.util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet29DestroyEntity;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;

import to.joe.manager.Minitrue;
import to.joe.util.Packeteer.*;

/**
 * Vanishing handling
 * 
 */
public class Vanish {

    public int RANGE = 512;
    public int TOTAL_REFRESHES = 10;
    public int REFRESH_TIMER = 2;

    /**
     * List of all invisible players
     */
    private final List<Player> invisible = new ArrayList<Player>();

    private final Object sync = new Object();

    private final Logger log = Logger.getLogger("Minecraft");
    private final Minitrue mini;

    private final Object eidSync = new Object();

    private final ArrayList<Integer> eidVanished;

    public Vanish(Minitrue mini) {
        this.eidVanished = new ArrayList<Integer>();
        this.mini = mini;
        
        
        SpoutManager.getPacketManager().addListener(5, new Packeteer5EntityEquipment(this));
        SpoutManager.getPacketManager().addListener(17, new Packeteer17EntityLocationAction(this));
        SpoutManager.getPacketManager().addListener(18, new Packeteer18ArmAnimation(this));
        SpoutManager.getPacketManager().addListener(19, new Packeteer19EntityAction(this));
        SpoutManager.getPacketManager().addListener(20, new Packeteer20NamedEntitySpawn(this));
        SpoutManager.getPacketManager().addListener(28, new Packeteer28EntityVelocity(this));
        SpoutManager.getPacketManager().addListener(201, new Packeteer201PlayerInfo());
        // SpoutManager.getPacketManager().addListener(29, new
        // Packeteer29DestroyEntity(this));
        SpoutManager.getPacketManager().addListener(30, new Packeteer30Entity(this));
        SpoutManager.getPacketManager().addListener(31, new Packeteer31RelEntityMove(this));
        SpoutManager.getPacketManager().addListener(32, new Packeteer32EntityLook(this));
        SpoutManager.getPacketManager().addListener(33, new Packeteer33RelEntityMoveLook(this));
        SpoutManager.getPacketManager().addListener(34, new Packeteer34EntityTeleport(this));
        SpoutManager.getPacketManager().addListener(38, new Packeteer38EntityStatus(this));
        SpoutManager.getPacketManager().addListener(39, new Packeteer39AttachEntity(this));
    }

    private void addEIDVanished(int id) {
        synchronized (this.eidSync) {
            this.eidVanished.add(id);
        }
    }

    /**
     * Vanish toggle on player
     * 
     * @param player
     */
    public void callVanish(Player player) {
        if (this.invisible.contains(player)) {
            this.callUnVanish(player);
            return;
        }
        this.invisible.add(player);
        final Player[] playerList = this.mini.j2.getServer().getOnlinePlayers();
        for (final Player p : playerList) {
            if ((this.getDistance(player, p) > this.RANGE) || (p.equals(player))) {
                continue;
            }
            this.sendDestroyPacket(player, p);
            if (this.mini.j2.hasFlag(p, Flag.ADMIN)) {
                this.sendSpawnPacket(player, p);
            }
        }
        this.addEIDVanished(((CraftPlayer) player).getEntityId());
        this.log.info(player.getName() + " disappeared.");
        this.mini.j2.users.addFlagLocal(player.getName(), Flag.VANISHED);
        player.sendMessage(ChatColor.RED + "Poof!");
    }

    public int invisibleCount() {
        synchronized (this.sync) {
            return this.invisible.size();
        }
    }

    public boolean isEIDVanished(int id) {
        synchronized (this.eidSync) {
            return this.eidVanished.contains(id);
        }
    }

    public boolean isInvisible(Player player) {
        synchronized (this.sync) {
            return this.invisible.contains(player);
        }
    }

    public void refreshForAdmin(Player admin) {
        for (final Player player : this.mini.j2.getServer().getOnlinePlayers()) {
            if ((player != null) && !player.equals(admin)) {
                this.sendDestroyPacket(player, admin);
                this.sendSpawnPacket(player, admin);
            }
        }
    }

    public void removeEIDVanished(int id) {
        synchronized (this.eidSync) {
            this.eidVanished.remove(id);
        }
    }

    public void removeInvisibility(Player player) {
        synchronized (this.sync) {
            this.invisible.remove(player);
        }
        synchronized (this.eidSync) {
            this.eidVanished.remove(Integer.valueOf(((CraftPlayer) player).getEntityId()));
        }
    }

    public boolean shouldHide(Player from, int eid) {
        if (!this.mini.j2.hasFlag(from, Flag.ADMIN)) {
            return this.isEIDVanished(eid);
        }
        return false;
    }

    private void callUnVanish(Player player) {
        if (!this.isInvisible(player)) {
            return;
        }
        this.removeInvisibility(player);

        final Player[] playerList = this.mini.j2.getServer().getOnlinePlayers();
        for (final Player p : playerList) {
            if ((this.getDistance(player, p) >= this.RANGE) || (p.equals(player))) {
                continue;
            }
            if (this.mini.j2.hasFlag(p, Flag.ADMIN)) {
                this.sendDestroyPacket(player, p);
            }
            this.sendSpawnPacket(player, p);
        }
        this.mini.j2.users.dropFlagLocal(player.getName(), Flag.VANISHED);
        this.log.info(player.getName() + " reappeared.");
        player.sendMessage(ChatColor.RED + "You have reappeared!");
    }

    private double getDistance(Player player1, Player player2) {
        final Location loc1 = player1.getLocation();
        final Location loc2 = player1.getLocation();
        return Math.sqrt(Math.pow(loc1.getX() - loc2.getX(), 2.0D) + Math.pow(loc1.getY() - loc2.getY(), 2.0D) + Math.pow(loc1.getZ() - loc2.getZ(), 2.0D));
    }

    /**
     * Hide player from another
     * 
     * @param hidePlayer
     * @param obliviousPlayer
     * @param force
     *            Force even on admins.
     */
    private void sendDestroyPacket(Player hidePlayer, Player obliviousPlayer) {
        final CraftPlayer hide = (CraftPlayer) hidePlayer;
        final CraftPlayer hideFrom = (CraftPlayer) obliviousPlayer;
        hideFrom.getHandle().netServerHandler.sendPacket(new Packet29DestroyEntity(hide.getEntityId()));
    }

    /**
     * Reveal player to other player
     * 
     * @param unHidingPlayer
     * @param nowAwarePlayer
     */
    private void sendSpawnPacket(Player unHidingPlayer, Player nowAwarePlayer) {
        final CraftPlayer unHide = (CraftPlayer) unHidingPlayer;
        final CraftPlayer unHideFrom = (CraftPlayer) nowAwarePlayer;
        unHideFrom.getHandle().netServerHandler.sendPacket(new Packet20NamedEntitySpawn(unHide.getHandle()));
    }
}