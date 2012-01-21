package to.joe.Commands.Fun;

import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class GameModeToggleCommand extends MasterCommand {

    public GameModeToggleCommand(J2 j2) {
        super(j2);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
        if (isPlayer && this.j2.hasFlag(playerName, Flag.ADMIN)) {
            if(player.getGameMode().equals(GameMode.SURVIVAL)){
                player.setGameMode(GameMode.CREATIVE);
            } else{
                player.setGameMode(GameMode.SURVIVAL);
            }
            this.j2.log(player.getName()+" changed to "+player.getGameMode().toString());
        }
    }
}
