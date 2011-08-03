package to.joe.Commands;

import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.util.Flag;
import to.joe.util.Report;

public class ReportCommand extends MasterCommand {

    public ReportCommand(J2 j2) {
        super(j2);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
        if (isPlayer) {
            if (args.length > 0) {
                final String theReport = this.j2.combineSplit(0, args, " ");
                if (!this.j2.hasFlag(player, Flag.ADMIN)) {
                    final Report report = new Report(0, player.getLocation(), player.getName(), theReport, (new Date().getTime()) / 1000, false);
                    this.j2.reports.addReport(report);
                    player.sendMessage(ChatColor.RED + "Report received. Thanks! :)");
                    player.sendMessage(ChatColor.RED + "Assuming you gave a description, we will handle it");
                } else {
                    final String message = ChatColor.LIGHT_PURPLE + "Report from the field: <" + ChatColor.RED + playerName + ChatColor.LIGHT_PURPLE + "> " + ChatColor.WHITE + theReport;
                    this.j2.sendAdminPlusLog(message);
                    this.j2.irc.messageAdmins(ChatColor.stripColor(message));
                    player.sendMessage(ChatColor.RED + "Report transmitted. Thank you soldier.");
                }
            } else {
                player.sendMessage(ChatColor.RED + "To report to the admins, say /report MESSAGE");
                player.sendMessage(ChatColor.RED + "Where MESSAGE is what you want to tell them");
            }
        }
    }
}
