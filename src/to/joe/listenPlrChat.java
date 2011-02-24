
package to.joe;


import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;



public class listenPlrChat extends PlayerListener {
	private final J2Plugin j2;

	public listenPlrChat(J2Plugin instance) {
		j2 = instance;
	}

	@Override
	public void onPlayerChat (PlayerChatEvent event ) {
		String name=event.getPlayer().getName();
		String message=event.getMessage();
		j2.chat.addChat(name, message);
		j2.irc.ircMsg("<"+name+"> "+message);
		if(!j2.randomcolor){
			return;
		}
		String[] colorlist=j2.chat.getColorlist();
		int rand=j2.random.nextInt(13);
		if(rand<12){
			j2.chat.msgAll(ChatColor.WHITE+"<"+colorlist[rand]+name+ChatColor.WHITE+"> "+message);
		}
		else
		{
			
			for(int x=0;x<name.length();x++){
				name+=colorlist[j2.random.nextInt(12)]+name.charAt(x);
			}
			j2.chat.msgAll(ChatColor.WHITE+"<"+name+ChatColor.WHITE+"> "+message);
		}
		event.setCancelled(true);
	}
}
