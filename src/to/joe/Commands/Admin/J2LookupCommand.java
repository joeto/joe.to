package to.joe.Commands.Admin;

import java.util.ArrayList;
import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.util.Ban;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class J2LookupCommand extends MasterCommand {

    public J2LookupCommand(J2 j2) {
        super(j2);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
        if (isPlayer && this.j2.hasFlag(player, Flag.ADMIN)) {
            if (args.length == 0) {
                player.sendMessage(ChatColor.LIGHT_PURPLE + "/j2lookup player");
                return;
            }
            final String target = args[0];
            this.j2.log(ChatColor.LIGHT_PURPLE + "[j2bans] " + playerName + " looked up " + target);
            String x = "";
            boolean allbans = false;
            if ((args.length > 1) && args[1].equalsIgnoreCase("all")) {
                allbans = true;
            }
            final ArrayList<Ban> bans = this.j2.mysql.getBans(target, allbans);
            final ArrayList<String> messages = new ArrayList<String>();
            boolean banned = false;
            for (final Ban ban : bans) {
                if (ban.isBanned()) {
                    x = ChatColor.DARK_RED + "X";
                    banned = true;
                } else {
                    x = ChatColor.GREEN + "U";
                }
                final String c = ChatColor.DARK_AQUA.toString();
                messages.add(c + "[" + x + c + "] " + this.j2.shortdateformat.format(new Date(ban.getTimeOfBan() * 1000)) + " " + ChatColor.GOLD + ban.getReason());
            }
            String c2 = ChatColor.GREEN.toString();
            if (banned) {
                c2 = ChatColor.RED.toString();
            }
            player.sendMessage(ChatColor.AQUA + "Found " + ChatColor.GOLD + bans.size() + ChatColor.AQUA + " bans for " + c2 + target);
            for (final String message : messages) {
                player.sendMessage(message);
            }
        }
    }
}
