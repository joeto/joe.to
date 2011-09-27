package to.joe.Commands.Fun;

import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;
import to.joe.util.Warp;

public class SpawnCommand extends MasterCommand {

    public SpawnCommand(J2 j2) {
        super(j2);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
        if (!isPlayer || this.j2.hasFlag(player, Flag.FUN)) {
            if (isPlayer && (!this.j2.hasFlag(player, Flag.ADMIN) || (args.length < 1))) {
                player.sendMessage(ChatColor.RED + "WHEEEEEEEEEEEEEEE");
                Random rand = new Random();
                int randint = rand.nextInt((12+1) - 1) + 1;
                String randWarp =  Integer.toString(randint);
                final Warp warp = this.j2.warps.getPublicWarp(randWarp);
                this.j2.safePort(player, warp.getLocation());

            } else if (args.length == 1) {
                final List<Player> inquest = this.j2.getServer().matchPlayer(args[0]);
                if (inquest.size() == 1) {
                    final Player inquestion = inquest.get(0);
                    Random rand = new Random();
                    int randint = rand.nextInt((12+1) - 1) + 1;
                    String randWarp =  Integer.toString(randint);
                    final Warp warp = this.j2.warps.getPublicWarp(randWarp);
                    this.j2.safePort(player, warp.getLocation());
                    inquestion.sendMessage(ChatColor.RED + "OH GOD I'M BEING PULLED TO SPAWN OH GOD");
                    this.j2.sendAdminPlusLog(ChatColor.RED + playerName + " pulled " + inquestion.getName() + " to spawn");
                } else {
                    sender.sendMessage(ChatColor.RED + "No such player, or matches multiple");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: /spawn playername");
            }
        }
    }

}
