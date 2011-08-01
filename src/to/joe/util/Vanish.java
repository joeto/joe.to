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

import to.joe.manager.Minitrue;

/**
 * Vanishing handling
 *
 */
public class Vanish
{

	public int RANGE= 512;
	public int TOTAL_REFRESHES = 10;
	public int REFRESH_TIMER = 2;
	private Timer timer = new Timer();

	/**
	 * List of all invisible players
	 */
	public List<Player> invisible = new ArrayList<Player>();


	private final Logger log = Logger.getLogger("Minecraft");
	private Minitrue mini;
	public Vanish(Minitrue mini){
		this.mini=mini;
	}

	/**
	 * Hide player from another
	 * @param hidePlayer
	 * @param obliviousPlayer
	 * @param force Force even on admins.
	 */
	private void invisible(Player hidePlayer, Player obliviousPlayer, boolean force)
	{
		if (this.mini.j2.hasFlag(obliviousPlayer,Flag.ADMIN)) return;
		CraftPlayer hide = (CraftPlayer)hidePlayer;
		CraftPlayer hideFrom = (CraftPlayer)obliviousPlayer;
		hideFrom.getHandle().netServerHandler.sendPacket(new Packet29DestroyEntity(hide.getEntityId()));
	}

	/** 
	 * Reveal player to other player
	 * @param unHidingPlayer
	 * @param nowAwarePlayer
	 */
	private void uninvisible(Player unHidingPlayer, Player nowAwarePlayer)
	{
		CraftPlayer unHide = (CraftPlayer)unHidingPlayer;
		CraftPlayer unHideFrom = (CraftPlayer)nowAwarePlayer;
		unHideFrom.getHandle().netServerHandler.sendPacket(new Packet20NamedEntitySpawn(unHide.getHandle()));
	}

	/**
	 * Vanish toggle on player
	 * @param player
	 */
	public void callVanish(Player player)
	{
		if(this.invisible.contains(player)){
			this.callUnVanish(player);
			return;
		}
		this.invisible.add(player);
		Player[] playerList = this.mini.j2.getServer().getOnlinePlayers();
		for (Player p : playerList)
		{
			if ((getDistance(player, p) > this.RANGE) || (p.equals(player)) )
				continue;
			invisible(player, p, false);
		}

		this.log.info(player.getName() + " disappeared.");
		player.sendMessage(ChatColor.RED + "Poof!");
	}

	private void callUnVanish(Player player)
	{
		if (!this.invisible.contains(player))
			return;
		this.invisible.remove(player);

		updateInvisibleForPlayer(player, true);
		Player[] playerList = this.mini.j2.getServer().getOnlinePlayers();
		for (Player p : playerList)
		{
			if ((getDistance(player, p) >= this.RANGE) || (p.equals(player)))
				continue;
			uninvisible(player, p);
		}

		this.log.info(player.getName() + " reappeared.");
		player.sendMessage(ChatColor.RED + "You have reappeared!");
	}

	private void updateInvisibleForPlayer(Player player, boolean force)
	{
		Player[] playerList = this.mini.j2.getServer().getOnlinePlayers();
		for (Player p : playerList)
		{
			if ((getDistance(player, p) > this.RANGE) || (p.equals(player)))
				continue;
			invisible(player, p, force);
		}
	}

	private void updateInvisibleForAll()
	{
		Player[] playerList = this.mini.j2.getServer().getOnlinePlayers();
		for (Player invisiblePlayer : this.invisible)
		{
			for (Player p : playerList)
			{
				if ((getDistance(invisiblePlayer, p) > this.RANGE) || (p.equals(invisiblePlayer)))
					continue;
				invisible(invisiblePlayer, p, false);
			}
		}
	}

	private void updateInvisibleForAll(boolean startTimer)
	{
		updateInvisibleForAll();
		if (!startTimer)
			return;
		this.timer.schedule(new UpdateInvisibleTimerTask(true), 60000 * this.REFRESH_TIMER);
	}

	/**
	 * Update invisibility for player
	 * @param player
	 */
	public void updateInvisible(Player player)
	{
		for (Player invisiblePlayer : this.invisible)
		{
			if ((getDistance(invisiblePlayer, player) > this.RANGE) || (player.equals(invisiblePlayer)))
				continue;
			invisible(invisiblePlayer, player, false);
		}
	}

	private double getDistance(Player player1, Player player2)
	{
		Location loc1 = player1.getLocation();
		Location loc2 = player1.getLocation();
		return Math.sqrt(Math.pow(loc1.getX() - loc2.getX(), 2.0D) + Math.pow(loc1.getY() - loc2.getY(), 2.0D) + Math.pow(loc1.getZ() - loc2.getZ(), 2.0D));
	}

	/**
	 * Update invisible timed.
	 */
	public void updateInvisibleOnTimer()
	{
		updateInvisibleForAll();
		Timer timer = new Timer();
		int i = 0;
		while (i < this.TOTAL_REFRESHES)
		{
			++i;
			timer.schedule(new UpdateInvisibleTimerTask(), i * 1000);
		}
	}

	private class UpdateInvisibleTimerTask extends TimerTask
	{
		private boolean startTimer = false;

		public UpdateInvisibleTimerTask()
		{
		}

		public UpdateInvisibleTimerTask(boolean startTimer)
		{
			this.startTimer = startTimer;
		}

		public void run()
		{
			Vanish.this.updateInvisibleForAll(this.startTimer);
		}
	}
}