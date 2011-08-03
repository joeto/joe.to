package to.joe.Commands.Admin;

import java.util.Date;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class IPLookupCommand extends MasterCommand {

    public IPLookupCommand(J2 j2) {
        super(j2);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
        if (isPlayer && this.j2.hasFlag(player, Flag.ADMIN)) {
            if (args.length == 1) {
                final String lastIP = this.j2.mysql.IPGetLast(args[0]);
                if (!lastIP.isEmpty()) {
                    player.sendMessage(ChatColor.AQUA + "IPLookup on " + ChatColor.WHITE + args[0] + ChatColor.AQUA + "\'s last IP: " + ChatColor.WHITE + lastIP);
                    final HashMap<String, Long> nameDates = this.j2.mysql.IPGetNamesOnIP(lastIP);
                    if (!nameDates.isEmpty()) {
                        for (final String key : nameDates.keySet()) {
                            if (!key.isEmpty() && (key.toLowerCase() != "null")) {
                                final Long time = nameDates.get(key);
                                final Date date = new Date(time);
                                player.sendMessage(ChatColor.AQUA + key + " : " + ChatColor.BLUE + date);
                            }
                        }
                    }
                } else {
                    player.sendMessage(ChatColor.AQUA + "Could not find any matches.");
                }
            } else {
                player.sendMessage(ChatColor.RED + "Invalid Usage, /iplookup <player_name>");
            }
        }
    }
}
