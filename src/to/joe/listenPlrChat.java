
package to.joe;


import org.bukkit.ChatColor;
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
		String name=player.getName();
		String message=event.getMessage();
		
		j2.chat.addChat(name, message);
		j2.irc.ircMsg("<"+name+"> "+message);
		j2.log.info("<"+name+"> "+message);
		/*if(!j2.randomcolor){
			return;
		}
		String[] colorlist=j2.chat.getColorlist();
		int size=colorlist.length;
		int rand=j2.random.nextInt(size+1);
		if(rand<size){
			j2.chat.msgAll(ChatColor.WHITE+"<"+colorlist[rand]+name+ChatColor.WHITE+"> "+message);
		}
		else
		{
			for(int x=0;x<name.length();x++){
				name+=colorlist[j2.random.nextInt(size)]+name.charAt(x);
			}
			j2.chat.msgAll(ChatColor.WHITE+"<"+name+ChatColor.WHITE+"> "+message);
		}*/
		if(player.getName().equalsIgnoreCase("mbaxter")){
			String[] colorlist=j2.chat.getColorlist();
			String dname="";
			int size=colorlist.length;
			for(int x=0;x<7;x++){
				dname+=colorlist[j2.random.nextInt(size)]+name.charAt(x);
			}
			j2.chat.msgAll(ChatColor.WHITE+"<"+dname+ChatColor.WHITE+"> "+message);
			event.setCancelled(true);
			return;
		}
		
		j2.chat.msgAll(ChatColor.WHITE+"<"+j2.users.getUser(player).getColorName()+ChatColor.WHITE+"> "+message);
		event.setCancelled(true);
	}
}
