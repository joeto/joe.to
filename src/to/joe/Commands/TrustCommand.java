package to.joe.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.util.Flag;

public class TrustCommand extends MasterCommand {

    public TrustCommand(J2 j2) {
        super(j2);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
        if (isPlayer) {
            if (this.j2.hasFlag(player, Flag.ADMIN)) {
                if (args.length < 2 || !(args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("drop"))) {
                    player.sendMessage(ChatColor.RED + "Usage: /trust add/drop player");
                    return;
                }
                String name = args[1];
                if (args[0].equalsIgnoreCase("add")) {
                    this.j2.users.addFlag(name, Flag.TRUSTED);
                } else {
                    this.j2.users.dropFlag(name, Flag.TRUSTED);
                }
                String tolog = ChatColor.RED + player.getName() + " changed flags: " + name + " " + args[0] + " flag " + Flag.TRUSTED.getDescription();
                this.j2.sendAdminPlusLog(tolog);
            } else {
                player.sendMessage(ChatColor.AQUA + "Trusted status gives special privileges");
                player.sendMessage(ChatColor.AQUA + "You want it? Visit our forums Minecraft section");
                player.sendMessage(ChatColor.AQUA + "http://forums.joe.to");
            }
        }
    }
}
