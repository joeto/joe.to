package to.joe.Commands.Admin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;
import to.joe.util.User;

public class AuthCommand extends MasterCommand {

    public AuthCommand(J2 j2) {
        super(j2);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
        if (isPlayer && this.j2.reallyHasFlag(playerName, Flag.ADMIN)) {
            final User user = this.j2.users.getUser(player);
            if ((user != null) && (args.length == 1)) {
                final String safeword = user.getSafeWord();
                if (!safeword.equalsIgnoreCase("") && safeword.equals(args[0])) {
                    this.j2.users.authenticatedAdmin(playerName);
                    this.j2.sendAdminPlusLog(ChatColor.LIGHT_PURPLE + "[J2AUTH] " + playerName + " authenticated");
                    return;
                }
            }
            if (this.j2.users.isAuthed(playerName)) {
                this.j2.sendAdminPlusLog(ChatColor.LIGHT_PURPLE + "[J2AUTH] " + playerName + " deauthenticated");
            }
            this.j2.users.resetAuthentication(player);
            this.j2.minitrue.vanish.updateInvisible(player);
        }
    }
}
