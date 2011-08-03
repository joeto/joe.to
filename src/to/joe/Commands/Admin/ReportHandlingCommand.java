package to.joe.Commands.Admin;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;
import to.joe.util.Report;

public class ReportHandlingCommand extends MasterCommand {

    public ReportHandlingCommand(J2 j2) {
        super(j2);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
        if (isPlayer && this.j2.hasFlag(player, Flag.ADMIN)) {
            if (args.length == 0) {
                ArrayList<Report> reps = this.j2.reports.getReports();
                int size = reps.size();
                if (size == 0) {
                    player.sendMessage(ChatColor.RED + "No reports. Hurray!");
                    return;
                }
                player.sendMessage(ChatColor.DARK_PURPLE + "Found " + size + " reports:");
                for (Report r : reps) {
                    if (!r.closed()) {
                        Location location = r.getLocation();
                        String x = ChatColor.GOLD.toString() + location.getBlockX() + ChatColor.DARK_PURPLE + ",";
                        String y = ChatColor.GOLD.toString() + location.getBlockY() + ChatColor.DARK_PURPLE + ",";
                        String z = ChatColor.GOLD.toString() + location.getBlockZ() + ChatColor.DARK_PURPLE;
                        player.sendMessage(ChatColor.DARK_PURPLE + "[" + r.getID() + "][" + x + y + z + "]<" + ChatColor.GOLD + r.getUser() + ChatColor.DARK_PURPLE + "> " + ChatColor.WHITE + r.getMessage());
                    }
                }
            } else {
                String action = args[0].toLowerCase();
                if (action.equals("close")) {
                    if (args.length > 2) {
                        int id = Integer.parseInt(args[1]);
                        if (id != 0) {
                            this.j2.reports.close(id, playerName, this.j2.combineSplit(2, args, " "));
                            player.sendMessage(ChatColor.DARK_PURPLE + "Report closed");
                        }
                    } else {
                        player.sendMessage(ChatColor.DARK_PURPLE + "/r close ID reason");
                    }
                }
                if (action.equals("tp")) {
                    if (args.length > 1) {
                        Report report = this.j2.reports.getReport(Integer.valueOf(args[1]));
                        if (report != null) {
                            this.j2.safePort(player, report.getLocation());
                            player.sendMessage(ChatColor.DARK_PURPLE + "Wheeeeeeeee");
                        } else {
                            player.sendMessage(ChatColor.DARK_PURPLE + "Report not found");
                        }
                    } else {
                        player.sendMessage(ChatColor.DARK_PURPLE + "/r tp ID");
                    }
                }
            }
        }
    }
}
