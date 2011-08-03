package to.joe.Commands.Fun;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class ClearInventoryCommand extends MasterCommand {

    public ClearInventoryCommand(J2 j2) {
        super(j2);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
        if (!isPlayer || this.j2.hasFlag(player, Flag.FUN)) {
            Player target = null;
            if (isPlayer && (args.length == 0)) {
                target = player;
                player.sendMessage(ChatColor.RED + "Inventory emptied");
                this.j2.log(ChatColor.RED + player.getName() + " emptied inventory");
            } else if ((args.length == 1) && (!isPlayer || this.j2.hasFlag(player, Flag.ADMIN))) {
                final List<Player> targets = this.j2.minitrue.matchPlayer(args[0], true);
                if (targets.size() == 1) {
                    target = targets.get(0);
                    target.sendMessage(ChatColor.RED + "Your inventory has been cleared by an admin");
                    this.j2.sendAdminPlusLog(ChatColor.RED + playerName + " emptied inventory of " + target.getName());
                } else {
                    sender.sendMessage(ChatColor.RED + "Found " + targets.size() + " matches. Try again");
                }
            }
            if (target != null) {
                final PlayerInventory targetInventory = target.getInventory();
                targetInventory.clear(36);
                targetInventory.clear(37);
                targetInventory.clear(38);
                targetInventory.clear(39);
                targetInventory.clear();
            }
        }
    }
}
