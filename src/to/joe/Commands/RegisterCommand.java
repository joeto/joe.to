package to.joe.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;

public class RegisterCommand extends MasterCommand {

    public RegisterCommand(J2 j2) {
        super(j2);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
        if (isPlayer){
            if(args.length!=1 || args[0].length()!=5){
                player.sendMessage(ChatColor.DARK_AQUA + "Usage: /register key");
                player.sendMessage(ChatColor.DARK_AQUA + "Get your key: http://forums.joe.to/trust");
            }
            else{
                if(this.j2.mysql.isRegistered(args[0]) || !this.j2.mysql.authCorrect(args[0])){
                    if(this.j2.mysql.isRegistered(args[0])){
                        player.sendMessage(ChatColor.DARK_AQUA + "Your account is already linked!");
                    }
                    if(!this.j2.mysql.authCorrect(args[0])){
                        player.sendMessage(ChatColor.DARK_AQUA + "Incorrect key!");
                    }
                }
                else{
                    this.j2.mysql.addLink(playerName, args[0]);
                    player.sendMessage(ChatColor.AQUA + "Success! Thank you for linking your account");
                }
            }
        }
    }
}