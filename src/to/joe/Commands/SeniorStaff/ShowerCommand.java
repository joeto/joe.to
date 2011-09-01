package to.joe.Commands.SeniorStaff;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import to.joe.J2;
import to.joe.Commands.MasterCommand;
import to.joe.util.Flag;

public class ShowerCommand extends MasterCommand {

    public ShowerCommand(J2 j2) {
        super(j2);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
        if (isPlayer && this.j2.hasFlag(player, Flag.SRSTAFF)) {
            final Location target = player.getTargetBlock(null, 50).getLocation();
            for(int x=0;x<30;x++){
                final Location tar = new Location(target.getWorld(), target.getX(), target.getY(), target.getZ());
                this.j2.getServer().getScheduler().scheduleAsyncDelayedTask(this.j2, new singleFireball(tar.add((this.j2.random.nextInt(6)-3)*5,40,((this.j2.random.nextInt(6)-3)*5)-20)), this.j2.random.nextInt(60));
            }
        }
    }
    private class singleFireball implements Runnable{
        private final Location target;
        public singleFireball(Location target){
            this.target=target;
        }
        @Override
        public void run() {
            final Fireball fireball=(this.target.getWorld()).spawn(this.target, Fireball.class);
            fireball.setVelocity(new Vector(0, -4, 1));
            fireball.setIsIncendiary(true);
            fireball.setYield(3);
        }
    }
}
