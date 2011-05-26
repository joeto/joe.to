
package to.joe.listener;


import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerListener;

import to.joe.J2;
import to.joe.util.Flag;



public class PlayerChat extends PlayerListener {
	private final J2 j2;
	private String plugins;
	
	public PlayerChat(J2 instance) {
		j2 = instance;
		StringBuilder pluginList = new StringBuilder();
		String[] plugins=new String[5];
		plugins[0]="Bob";
		plugins[1]="joe.to";
		plugins[2]="Optimism";
		plugins[3]="Misery";
		plugins[4]="Cake";
		for (String plugin : plugins) {
			if (pluginList.length() > 0) {
				pluginList.append(ChatColor.WHITE);
				pluginList.append(", ");
			}

			pluginList.append(ChatColor.GREEN);
			pluginList.append(plugin);
		}
		this.plugins= pluginList.toString();
	}

	@Override
	public void onPlayerChat (PlayerChatEvent event ) {
		Player player=event.getPlayer();
		String message=event.getMessage();
		if(!this.j2.panda.chat(player, message)){
    		event.setCancelled(true);
    		return;
    	}
		j2.activity.update(player.getName());
		j2.chat.handleChat(player, message,false);
		event.setCancelled(true);
	}
	
	@Override
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		Player player=event.getPlayer();
		String name=player.getName();
		String message=event.getMessage();
		if(!this.j2.panda.chat(player, message)){
    		event.setCancelled(true);
    		return;
    	}
		j2.activity.update(name);
		String[] split=message.split(" ");
		String command=split[0].trim().substring(1).toLowerCase();
		j2.log.info("[J2CMD] "+name+" command "+message);
		if((command.equals("plugins")||command.equals("pl"))&&!j2.hasFlag(player, Flag.SRSTAFF)){
			player.sendMessage("Plugins: " + this.plugins);
			event.setCancelled(true);
			return;
		}
		if((command.equals("version")||command.equals("ver")||command.equals("about"))&&!j2.hasFlag(player, Flag.SRSTAFF)){
			if (split.length == 0) {
				player.sendMessage("This server is running " + ChatColor.GREEN
						+ "CraftBukkit" + ChatColor.WHITE + " version " + ChatColor.GREEN + "joe.to");
			} else {
				player.sendMessage(ChatColor.GREEN + "You should visit the " + ChatColor.WHITE + " J2 Community ");
				player.sendMessage("Website: " + ChatColor.GREEN + "http://forums.joe.to");
			}
			event.setCancelled(true);
			return;
		}
		if(command.equals("reload")||command.equals("rl")){
			if(j2.hasFlag(player, Flag.SRSTAFF)){
				player.sendMessage(ChatColor.GREEN+"YOU MONSTER");
			}
			event.setCancelled(true);
			event.setMessage(null);
			return;
		}
		if((command.equals("bb")||command.equals("nocheat")||command.equals("vanish"))&&!j2.hasFlag(player, Flag.ADMIN)){
			event.setCancelled(true);
			event.setMessage(null);
			return;
		}
	}
}
