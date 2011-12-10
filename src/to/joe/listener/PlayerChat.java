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
    private final String plugins;

    public PlayerChat(J2 instance) {
        this.j2 = instance;
        final StringBuilder pluginList = new StringBuilder();
        final String[] plugins = new String[5];
        plugins[0] = "Bob";
        plugins[1] = "joe.to";
        plugins[2] = "Optimism";
        plugins[3] = "Misery";
        plugins[4] = "Cake";
        for (final String plugin : plugins) {
            if (pluginList.length() > 0) {
                pluginList.append(ChatColor.WHITE);
                pluginList.append(", ");
            }

            pluginList.append(ChatColor.GREEN);
            pluginList.append(plugin);
        }
        this.plugins = pluginList.toString();
    }

    @Override
    public void onPlayerChat(PlayerChatEvent event) {
        final Player player = event.getPlayer();
        final String message = event.getMessage();
        if (this.j2.chat.isSpam(player, message) || !this.j2.panda.chat(player, message)) {
            event.setCancelled(true);
            return;
        }
        this.j2.activity.update(player);
        this.j2.chat.handleChat(player, message, false);
        event.setCancelled(true);
    }

    @Override
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        final Player player = event.getPlayer();
        final String name = player.getName();
        final String message = event.getMessage();
        if(message.equals("/ext")){
            event.setMessage("/extme");
        }
        if (this.j2.chat.isSpam(player, message) || !this.j2.panda.chat(player, message)) {
            event.setCancelled(true);
            return;
        }
        this.j2.activity.update(player);
        final String[] split = message.split(" ");
        final String command = split[0].trim().substring(1).toLowerCase();
        this.j2.log(ChatColor.WHITE + "[J2CMD] " + name + " command " + message);
        if ((command.equals("plugins") || command.equals("pl")) && !this.j2.hasFlag(player, Flag.SRSTAFF)) {
            player.sendMessage("Plugins: " + this.plugins);
            event.setCancelled(true);
            return;
        }
        if ((command.equals("version") || command.equals("ver") || command.equals("about")) && !this.j2.hasFlag(player, Flag.SRSTAFF)) {
            if (split.length == 0) {
                player.sendMessage("This server is running " + ChatColor.GREEN + "CraftBukkit" + ChatColor.WHITE + " version " + ChatColor.GREEN + "joe.to");
            } else {
                player.sendMessage(ChatColor.GREEN + "You should visit the " + ChatColor.WHITE + " J2 Community ");
                player.sendMessage("Website: " + ChatColor.GREEN + "http://forums.joe.to");
            }
            event.setCancelled(true);
            return;
        }
        if (command.equals("reload") || command.equals("rl")) {
            if (this.j2.hasFlag(player, Flag.SRSTAFF)) {
                player.sendMessage(ChatColor.GREEN + "YOU MONSTER");
            }
            event.setCancelled(true);
            event.setMessage(null);
            return;
        }
        if ((command.equals("bb") || command.equals("nocheat") || command.equals("vanish")) && !this.j2.hasFlag(player, Flag.ADMIN)) {
            event.setCancelled(true);
            event.setMessage(null);
            return;
        }
    }
}
