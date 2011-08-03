package to.joe.util.Runnables;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.World;

import to.joe.J2;

/**
 * Autosaving mechanism
 * 
 */
public class AutoSave implements Runnable {

    private final J2 j2;

    public AutoSave(J2 j2) {
        this.j2 = j2;
    }

    @Override
    public void run() {
        final Server server = this.j2.getServer();
        this.j2.log(ChatColor.AQUA + "Saving players");
        server.savePlayers();
        this.j2.log(ChatColor.AQUA + "Saving worlds");
        for (final World world : server.getWorlds()) {
            this.j2.log(ChatColor.AQUA + "Saved world " + world.getName());
            world.save();
        }
    }

}
