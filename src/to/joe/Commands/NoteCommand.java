package to.joe.Commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.util.Flag;

public class NoteCommand extends MasterCommand {

	public NoteCommand(J2 j2) {
		super(j2);
	}

	@Override
	public void exec(CommandSender sender, String commandName, String[] args, Player player, String playerName, boolean isPlayer) {
		if(isPlayer){
			if(args.length<2){
				player.sendMessage(ChatColor.RED+"/note username message");
				return;
			}
			boolean adminMode=false;
			if(commandName.equals("anote")){
				if(this.j2.hasFlag(playerName, Flag.ADMIN)){
					adminMode=true;
				}
			}
			String targetName=args[0];
			String message=this.j2.combineSplit(1, args, " ");
			List<Player> match=this.j2.getServer().matchPlayer(targetName);
			Player targetPlayer=null;
			if(match.size()>0){
				for(Player p:match){
					if(p!=null&&p.isOnline()&&p.getName().toLowerCase().equals(targetName.toLowerCase())){
						targetPlayer=p;
					}
				}
			}
			if(this.j2.minitrue.invisible(targetPlayer)&&!this.j2.hasFlag(player, Flag.ADMIN)){
				targetPlayer=null;
			}
			String a=ChatColor.AQUA.toString();
			String da=ChatColor.DARK_AQUA.toString();
			if(targetPlayer!=null){
				if(adminMode){
					targetPlayer.sendMessage(a+"HEY "+ChatColor.RED+targetPlayer.getName()+a+": "+message);
					this.j2.sendAdminPlusLog(a+"Priv <"+da+playerName+a+"->"+da+targetName+a+"> "+message);
				}
				else{
					this.j2.chat.handlePrivateMessage(player, targetPlayer, message);
				}
			}
			else{
				this.j2.mysql.addNote(playerName, targetName, message, adminMode);
				player.sendMessage(ChatColor.AQUA+"Note left for "+args[0]);
				String bit=a+"Note <"+da+playerName+a+"->"+da+targetName+a+"> "+message;
				this.j2.log(bit);
				if(adminMode){
					this.j2.chat.messageByFlag(Flag.ADMIN, bit);
				}
				else{
					this.j2.chat.messageByFlag(Flag.NSA, bit);
				}
			}
		}
	}		
}
