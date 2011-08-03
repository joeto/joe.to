package to.joe.Commands.Fun;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class ItemCommand extends MasterCommand {

    public ItemCommand(J2 j2) {
        super(j2);
        this.initWoolColors();
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
        if (isPlayer && this.j2.hasFlag(player, Flag.FUN)) {
            if (args.length == 0) {
                player.sendMessage(ChatColor.RED + "Correct usage is: /i [item](:damage) (amount)");
                return;
            }
            Player targetPlayer = player;
            Material itemMaterial = null;
            int itemCount = 1;
            String[] idDamageSplit = null;
            Byte itemDamage = null;
            if (args.length > 0) {
                idDamageSplit = args[0].split(":");
                if (idDamageSplit[0].equals("0")) {
                    idDamageSplit[0] = "1";
                }
                itemMaterial = Material.matchMaterial(idDamageSplit[0]);
                if (idDamageSplit.length == 2) {
                    final String damageString = idDamageSplit[1];
                    if (this.matchesWoolColor(damageString)) {
                        itemDamage = this.woolColorToByte(damageString);
                    } else {
                        try {
                            itemDamage = Byte.valueOf(damageString);
                        } catch (final NumberFormatException e) {
                            player.sendMessage("No such damage value. Giving you damage=0");
                        }
                    }
                }
            }
            if (args.length > 1) {
                final String countString = args[1];
                try {
                    itemCount = Integer.parseInt(countString);
                } catch (final NumberFormatException ex) {
                    player.sendMessage(ChatColor.RED + countString + " is not a number");
                    return;
                }
            }
            if ((args.length == 3) && this.j2.hasFlag(playerName, Flag.ADMIN)) {
                final String targetName = args[2];
                targetPlayer = this.j2.getServer().getPlayer(targetName);
                if (targetPlayer == null) {
                    player.sendMessage(ChatColor.RED + targetName + " is not a player");
                    return;
                }
            }
            if (itemMaterial == null) {
                player.sendMessage(ChatColor.RED + "Unknown item");
                return;
            }
            if (!this.j2.hasFlag(player, Flag.ADMIN) && this.j2.isOnSummonlist(itemMaterial.getId())) {
                player.sendMessage(ChatColor.RED + "Can't give that to you right now");
                return;
            }
            if (itemDamage != null) {
                targetPlayer.getInventory().addItem(new ItemStack(itemMaterial, itemCount, (short) 0, itemDamage));
            } else {
                targetPlayer.getInventory().addItem(new ItemStack(itemMaterial, itemCount));
            }
            player.sendMessage("Given " + targetPlayer.getDisplayName() + " " + itemCount + " " + itemMaterial.toString());
            this.j2.log("Giving " + playerName + " " + itemCount + " " + itemMaterial.toString());
            if ((this.j2.isOnWatchlist(itemMaterial.getId())) && ((itemCount > 10) || (itemCount < 1))) {
                this.j2.irc.messageAdmins("Detecting summon of " + itemCount + " " + itemMaterial.toString() + " by " + playerName);
                this.j2.sendAdminPlusLog(ChatColor.LIGHT_PURPLE + "Detecting summon of " + ChatColor.WHITE + itemCount + " " + ChatColor.LIGHT_PURPLE + itemMaterial.toString() + " by " + ChatColor.WHITE + playerName);
            }
            return;
        }
    }

    private boolean matchesWoolColor(String color) {
        return this.woolColors.containsKey(color.toLowerCase());
    }

    private byte woolColorToByte(String color) {
        return this.woolColors.get(color.toLowerCase());
    }

    private HashMap<String, Byte> woolColors;

    private void initWoolColors() {
        this.woolColors = new HashMap<String, Byte>();
        this.woolColors.put("white", (byte) 0);
        this.woolColors.put("orange", (byte) 1);
        this.woolColors.put("magenta", (byte) 2);
        this.woolColors.put("lightblue", (byte) 3);
        this.woolColors.put("yellow", (byte) 4);
        this.woolColors.put("lightgreen", (byte) 5);
        this.woolColors.put("pink", (byte) 6);
        this.woolColors.put("gray", (byte) 7);
        this.woolColors.put("lightgray", (byte) 8);
        this.woolColors.put("cyan", (byte) 9);
        this.woolColors.put("purple", (byte) 10);
        this.woolColors.put("blue", (byte) 11);
        this.woolColors.put("brown", (byte) 12);
        this.woolColors.put("darkgreen", (byte) 13);
        this.woolColors.put("red", (byte) 14);
        this.woolColors.put("black", (byte) 15);
    }
}
