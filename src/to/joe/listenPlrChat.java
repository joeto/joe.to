
package to.joe;


import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;



public class listenPlrChat extends PlayerListener {
	private final J2Plugin j2;

	public listenPlrChat(J2Plugin instance) {
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
