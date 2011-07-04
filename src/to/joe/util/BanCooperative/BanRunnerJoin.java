package to.joe.util.BanCooperative;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.manager.BanCooperative;
import to.joe.util.Flag;

public class BanRunnerJoin extends BanRunner{

	private Player player;
	
	public BanRunnerJoin(J2 j2, BanCooperative coop, String name, Player player) {
		super(j2, coop, name);
		this.player=player;
	}
	
	public void run(){
		HashMap<String,String> mcbans=mcbans_user_connect(name);
		this.dox(name);
		String mcbans_disputes=mcbans.get("disputes");
		String is_mcbans_mod=mcbans.get("is_mcbans_mod");
		BanCoopDossier dox=this.coop.record.get(name);
		if(dox.totalBans()>0){
			j2.chat.msgByFlag(Flag.ADMIN, ChatColor.LIGHT_PURPLE+"Player "+ChatColor.WHITE+name+ChatColor.LIGHT_PURPLE+" has "+ChatColor.WHITE+dox.totalBans()+ChatColor.LIGHT_PURPLE+" bans. MCBans rep "+ChatColor.WHITE+dox.getMCBansRep()+ChatColor.LIGHT_PURPLE+"/10");
			j2.chat.msgByFlag(Flag.ADMIN, ChatColor.LIGHT_PURPLE+"To see the bans: /lookup "+ChatColor.WHITE+name);
			if(!j2.hasFlag(name, Flag.QUIETERJOIN)&&dox.sigBans()>0){
				j2.irc.ircAdminMsg("[BANS] "+name+": Bans: "+dox.totalBans()+". MCBans Rep "+dox.getMCBansRep()+"/10");
			}
		}
		if(is_mcbans_mod.equals("y")){
			j2.chat.msgByFlag(Flag.ADMIN, ChatColor.RED+"Note to admins: "+name+" is an mcbans.com staffer");
			j2.irc.ircAdminMsg("[MCBANS] "+name+" is an mcbans.com staffer");
			j2.log(ChatColor.RED+"[MCBANS] "+name+" is an mcbans.com staffer");
			player.sendMessage(ChatColor.GREEN+"MCBANS staff: You have "+ChatColor.AQUA+mcbans_disputes+ChatColor.GREEN+" disputes");
		}
		else if(j2.hasFlag(name,Flag.SRSTAFF)&&(Integer.parseInt(mcbans_disputes)>0)){
			player.sendMessage(ChatColor.GREEN+"[MCBANS] We have "+ChatColor.AQUA+mcbans_disputes+ChatColor.GREEN+" active disputes");
		}
	}

}
