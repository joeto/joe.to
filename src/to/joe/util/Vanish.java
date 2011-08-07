package to.joe.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
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
    private final Timer timer = new Timer();

    /**
     * List of all invisible players
     */
    private List<Player> invisible = new ArrayList<Player>();

    private Object sync = new Object();

    private final Logger log = Logger.getLogger("Minecraft");
    private final Minitrue mini;

    public Vanish(Minitrue mini) {
        this.eidVanished=new ArrayList<Integer>();
        this.mini = mini;
        SpoutManager.getPacketManager().addListener(17, new Packeteer17(this));
        SpoutManager.getPacketManager().addListener(18, new Packeteer18ArmAnimation(this));
        SpoutManager.getPacketManager().addListener(19, new Packeteer19EntityAction(this));
        SpoutManager.getPacketManager().addListener(20, new Packeteer20NamedEntitySpawn(this));
        SpoutManager.getPacketManager().addListener(28, new Packeteer28EntityVelocity(this));
        SpoutManager.getPacketManager().addListener(29, new Packeteer29DestroyEntity(this));
        SpoutManager.getPacketManager().addListener(30, new Packeteer30Entity(this));
        SpoutManager.getPacketManager().addListener(31, new Packeteer30Entity(this));
        SpoutManager.getPacketManager().addListener(32, new Packeteer30Entity(this));
        SpoutManager.getPacketManager().addListener(33, new Packeteer30Entity(this));
        SpoutManager.getPacketManager().addListener(34, new Packeteer30Entity(this));
        SpoutManager.getPacketManager().addListener(38, new Packeteer30Entity(this));
        SpoutManager.getPacketManager().addListener(39, new Packeteer30Entity(this));
    }

    public boolean isInvisible(Player player) {
        synchronized (sync) {
            return this.invisible.contains(player);
        }
    }

    public void removeInvisibility(Player player) {
        synchronized (sync) {
            this.invisible.remove(player);
        }
        synchronized(this.eidSync){
            this.eidVanished.remove(((CraftPlayer)player).getEntityId());
        }
    }

    public int invisibleCount() {
        synchronized (sync) {
            return this.invisible.size();
        }
    }

    /**
     * Hide player from another
     * 
     * @param hidePlayer
     * @param obliviousPlayer
     * @param force
     *            Force even on admins.
     */
    private void invisible(Player hidePlayer, Player obliviousPlayer, boolean force) {
        if (this.mini.j2.hasFlag(obliviousPlayer, Flag.ADMIN)) {
            return;
        }
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
    private void uninvisible(Player unHidingPlayer, Player nowAwarePlayer) {
        final CraftPlayer unHide = (CraftPlayer) unHidingPlayer;
        final CraftPlayer unHideFrom = (CraftPlayer) nowAwarePlayer;
        unHideFrom.getHandle().netServerHandler.sendPacket(new Packet20NamedEntitySpawn(unHide.getHandle()));
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
        synchronized(this.eidSync){
            this.eidVanished.add(((CraftPlayer)player).getEntityId());
        }
        final Player[] playerList = this.mini.j2.getServer().getOnlinePlayers();
        for (final Player p : playerList) {
            if ((this.getDistance(player, p) > this.RANGE) || (p.equals(player))) {
                continue;
            }
            this.invisible(player, p, false);
        }

        this.log.info(player.getName() + " disappeared.");
        player.sendMessage(ChatColor.RED + "Poof!");
    }

    private void callUnVanish(Player player) {
        if (!this.isInvisible(player)) {
            return;
        }
        this.removeInvisibility(player);

        this.updateInvisibleForPlayer(player, true);
        final Player[] playerList = this.mini.j2.getServer().getOnlinePlayers();
        for (final Player p : playerList) {
            if ((this.getDistance(player, p) >= this.RANGE) || (p.equals(player))) {
                continue;
            }
            this.uninvisible(player, p);
        }

        this.log.info(player.getName() + " reappeared.");
        player.sendMessage(ChatColor.RED + "You have reappeared!");
    }

    private void updateInvisibleForPlayer(Player player, boolean force) {
        final Player[] playerList = this.mini.j2.getServer().getOnlinePlayers();
        for (final Player p : playerList) {
            if ((this.getDistance(player, p) > this.RANGE) || (p.equals(player))) {
                continue;
            }
            this.invisible(player, p, force);
        }
    }

    private void updateInvisibleForAll() {
        final Player[] playerList = this.mini.j2.getServer().getOnlinePlayers();
        synchronized (sync) {
            for (final Player invisiblePlayer : this.invisible) {
                for (final Player p : playerList) {
                    if ((this.getDistance(invisiblePlayer, p) > this.RANGE) || (p.equals(invisiblePlayer))) {
                        continue;
                    }
                    this.invisible(invisiblePlayer, p, false);
                }
            }
        }
    }

    private void updateInvisibleForAll(boolean startTimer) {
        this.updateInvisibleForAll();
        if (!startTimer) {
            return;
        }
        this.timer.schedule(new UpdateInvisibleTimerTask(true), 60000 * this.REFRESH_TIMER);
    }

    /**
     * Update invisibility for player
     * 
     * @param player
     */
    public void updateInvisible(Player player) {
        synchronized (sync) {
            for (final Player invisiblePlayer : this.invisible) {
                if ((this.getDistance(invisiblePlayer, player) > this.RANGE) || (player.equals(invisiblePlayer))) {
                    continue;
                }
                this.invisible(invisiblePlayer, player, false);
            }
        }
    }

    private double getDistance(Player player1, Player player2) {
        final Location loc1 = player1.getLocation();
        final Location loc2 = player1.getLocation();
        return Math.sqrt(Math.pow(loc1.getX() - loc2.getX(), 2.0D) + Math.pow(loc1.getY() - loc2.getY(), 2.0D) + Math.pow(loc1.getZ() - loc2.getZ(), 2.0D));
    }

    /**
     * Update invisible timed.
     */
    public void updateInvisibleOnTimer() {
        this.updateInvisibleForAll();
        final Timer timer = new Timer();
        int i = 0;
        while (i < this.TOTAL_REFRESHES) {
            ++i;
            timer.schedule(new UpdateInvisibleTimerTask(), i * 1000);
        }
    }

    private class UpdateInvisibleTimerTask extends TimerTask {
        private boolean startTimer = false;

        public UpdateInvisibleTimerTask() {
        }

        public UpdateInvisibleTimerTask(boolean startTimer) {
            this.startTimer = startTimer;
        }

        @Override
        public void run() {
            Vanish.this.updateInvisibleForAll(this.startTimer);
        }
    }
    
    private Object eidSync = new Object();
    private ArrayList<Integer> eidVanished;

    public void addEIDVanished(int id) {
        synchronized (eidSync) {
            this.eidVanished.add(id);
        }
    }
    
    public void removeEIDVanished(int id){
        synchronized(eidSync){
            this.eidVanished.remove(id);
        }
    }
    
    public boolean isEIDVanished(int id){
        synchronized (eidSync) {
            return this.eidVanished.contains(id);
        }
    }
    
    public boolean shouldHide(Player from,int eid){
        if(!this.mini.j2.hasFlag(from, Flag.ADMIN)){
            return this.isEIDVanished(eid);
        }
        return false;
    }
}