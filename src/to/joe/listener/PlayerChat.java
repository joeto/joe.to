
package to.joe.listener;


import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

import to.joe.J2Plugin;



public class PlayerChat extends PlayerListener {
	private final J2Plugin j2;

	public PlayerChat(J2Plugin instance) {
		j2 = instance;
	}

	@Override
	public void onPlayerChat (PlayerChatEvent event ) {
		Player player=event.getPlayer();
		String message=event.getMessage();
		j2.chat.handleChat(player, message);
		event.setCancelled(true);
	}
}
