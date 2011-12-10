package to.joe.listener;

/*import java.io.File;
 import java.io.FileNotFoundException;
 import java.io.IOException;

 import javax.sound.midi.InvalidMidiDataException;
 import javax.sound.midi.MidiUnavailableException;
 import com.sk89q.jinglenote.MidiJingleSequencer;
 */

import java.util.ArrayList;

import net.minecraft.server.Packet201PlayerInfo;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import to.joe.J2;
import to.joe.util.Flag;
import to.joe.util.User;

public class PlayerJoinQuit extends PlayerListener {

    private final J2 j2;

    // private ArrayList<String> theList;

    public PlayerJoinQuit(J2 instance) {
        this.j2 = instance;
        // theList=new ArrayList<String>();

    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        final Player player = event.getPlayer();
        this.j2.users.processJoin(player, false);

        /*
         * if(j2.hasFlag(player,Flag.JAILED)){ player.teleportTo(j2.users.jail);
         * player
         * .sendMessage(ChatColor.RED+"You are in "+ChatColor.DARK_RED+"JAIL");
         * player.sendMessage(ChatColor.RED+"To get out, talk to the jailer");
         * player.sendMessage(ChatColor.RED+"You need to punch him"); }
         */
        ((CraftPlayer)player).getHandle().netServerHandler.sendPacket(new Packet201PlayerInfo("Nyan",true,1));
    }

    ArrayList<String> kicked = new ArrayList<String>();

    @Override
    public void onPlayerKick(PlayerKickEvent event) {
        // if(theList.contains(event.getPlayer().getName())){
        // Player player=event.getPlayer();
        // j2.mysql.userIP(player.getName(), player.getAddress());
        // theList.remove(player.getName());
        // }
        final String name = event.getPlayer().getName();
        this.kicked.add(name);
        this.j2.damage.arf(event.getPlayer().getName());
        event.setLeaveMessage(null);
    }

    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final String name = player.getName();

        if (!this.kicked.contains(name)) {
            this.j2.minitrue.processLeave(player);
        } else {
            this.kicked.remove(name);
        }
        if (this.j2.users.getUser(player) != null) {
            this.j2.users.delUser(name);
            this.j2.warps.dropPlayer(name);
            this.j2.irc.processLeave(name);
        }
        event.setQuitMessage(null);
        this.j2.damage.arf(name);
        this.j2.users.dropAuthentication(name);
        this.j2.minitrue.vanish.removeInvisibility(player);
        this.j2.banCoop.disconnect(name);
    }

    @Override
    public void onPlayerPreLogin(PlayerPreLoginEvent event) {
        final String name = event.getName();
        final String ip = event.getAddress().getHostAddress();
        // System.out.println("IP: \""+ip+"\"");
        this.j2.debug("Incoming player: " + name + " on " + ip);
        String reason = null;
        try {
            reason = this.j2.mysql.checkBans(name);
        } catch (final Exception e) {
            reason = "Try again. Ban system didn't like you.";
        }
        // j2.mysql.userIP(name,player.getAddress().getHostName());
        // if(event.getResult().equals(Result.ALLOWED)){
        this.j2.debug("Ban system check complete.");
        this.j2.ip.incoming(name, ip);
        // }
        final User user = this.j2.mysql.getUser(name);
        this.j2.debug("Acquired user");
        final boolean isAdmin = (user.getUserFlags().contains(Flag.ADMIN) || this.j2.users.groupHasFlag(user.getGroup(), Flag.ADMIN));
        final boolean isDonor = (user.getUserFlags().contains(Flag.DONOR) || this.j2.users.groupHasFlag(user.getGroup(), Flag.DONOR));
        final boolean isContributor = (user.getUserFlags().contains(Flag.CONTRIBUTOR) || this.j2.users.groupHasFlag(user.getGroup(), Flag.CONTRIBUTOR));
        final boolean isTrusted = (user.getUserFlags().contains(Flag.TRUSTED) || this.j2.users.groupHasFlag(user.getGroup(), Flag.TRUSTED));
        final boolean isPrivBlocked = user.getUserFlags().contains(Flag.BARRED_MC1);
        boolean incoming = true;
        if (reason != null) {
            if (!reason.equals("Try again. Ban system didn't like you.")) {
                reason = "Visit http://www.joe.to/unban/ for unban";
            }
            event.setKickMessage(reason);
            event.disallow(PlayerPreLoginEvent.Result.KICK_BANNED, reason);
            incoming = false;
        }
        if (this.j2.config.maintenance_enable && !isAdmin) {
            reason = this.j2.config.maintenance_message;
            event.setKickMessage(reason);
            event.disallow(PlayerPreLoginEvent.Result.KICK_OTHER, reason);
            // j2.users.delUser(name);
            incoming = false;
        }
        if (this.j2.config.access_block_nontrusted && (!isTrusted || isPrivBlocked)) {
            reason = "Trusted only. http://forums.joe.to";
            if (isPrivBlocked) {
                reason = "You are barred from joining this server";
            }
            event.setKickMessage(reason);
            event.disallow(PlayerPreLoginEvent.Result.KICK_OTHER, reason);
            incoming = false;
        }
        if (this.j2.users.getUser(name) != null) {
            event.setKickMessage("Already logged in. If not, wait a minute and try again.");
            event.disallow(PlayerPreLoginEvent.Result.KICK_OTHER, "Already logged in.If not, wait a minute and try again.");
            // j2.kickbans.callKick(player.getName(), "CONSOLE",
            // "Logged in on another Minecraft");
            incoming = false;
        }
        if (!isAdmin && !isDonor && !isContributor && (this.j2.getServer().getOnlinePlayers().length >= this.j2.config.access_max_players)) {
            event.setKickMessage("Server Full");
            event.disallow(PlayerPreLoginEvent.Result.KICK_FULL, "Server full");
            // j2.users.delUser(name);
            incoming = false;
        }
        if (!incoming) {
            return;
        }
        this.j2.users.addUser(name);
        event.allow();
        this.j2.debug("Player " + name + " allowed in");
    }
}
