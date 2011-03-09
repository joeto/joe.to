
package to.joe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;

public class listenPlrCommands extends PlayerListener {

	private final J2Plugin j2;

	public listenPlrCommands(J2Plugin instance) {
		j2 = instance;
	}

	@Override
	public void onPlayerCommand(PlayerChatEvent event) {
		String[] split = event.getMessage().split(" ");
		Player player = event.getPlayer();

		if(j2.hasFlag(player,Flag.JAILED)){
			if(split[0].equalsIgnoreCase("/confess")){
				j2.users.getUser(player).dropFlag(Flag.JAILED);
			}
			event.setCancelled(true);
			return;
		}

		if (split[0].equalsIgnoreCase("/jail") && j2.hasFlag(player, Flag.ADMIN)){
			if(split.length<3){
				player.sendMessage(ChatColor.RED+"Usage: /jail <playername> <reason>");
			}
			else {
				String name=split[1];
				String adminName=player.getName();
				String reason=j2.combineSplit(2, split, " ");
				j2.users.jail(name,reason);
				j2.log.info("Jail: "+adminName+" jailed "+name+": "+reason);
			}
		}

		if (split[0].equalsIgnoreCase("/rules")){
			for(String line : j2.rules){
				player.sendMessage(line);
			}
			event.setCancelled(true);
			return;
		}
		if (split[0].equalsIgnoreCase("/help")){
			for(String line : j2.help){
				player.sendMessage(line);
			}
			event.setCancelled(true);
			return;
		}
		if (split[0].equalsIgnoreCase("/motd")){
			for(String line : j2.motd){
				player.sendMessage(line);
			}
			event.setCancelled(true);
			return;
		}
		if (split[0].equalsIgnoreCase("/blacklist")){
			for(String line : j2.blacklist){
				player.sendMessage(line);
			}
			event.setCancelled(true);
			return;
		}
		if (split[0].equalsIgnoreCase("/intro")){
			for(String line : j2.intro){
				player.sendMessage(line);
			}
			event.setCancelled(true);
			return;
		}
		if(split[0].equalsIgnoreCase("/protectme") && j2.hasFlag(player, Flag.TRUSTED)){
			String playerName = player.getName().toLowerCase();
			if(j2.tpProtect.getBoolean(playerName,false)){
				j2.tpProtect.setBoolean(playerName, false);
				player.sendMessage(ChatColor.RED + "You are now no longer protected from teleportation");
			}
			else{
				j2.tpProtect.setBoolean(playerName, true);
				player.sendMessage(ChatColor.RED + "You are protected from teleportation");
			}
			event.setCancelled(true);
			return;
		}

		if(split[0].equalsIgnoreCase("/tp") && (j2.hasFlag(player, Flag.FUN))){
			List<Player> inquest = j2.getServer().matchPlayer(split[1]);
			if(inquest.size()==1){
				Player inquestion=inquest.get(0);
				if(!j2.hasFlag(player, Flag.ADMIN) && inquestion!=null && (j2.hasFlag(inquestion, Flag.TRUSTED)) && j2.tpProtect.getBoolean(inquestion.getName().toLowerCase(), false)){
					player.sendMessage(ChatColor.RED + "Cannot teleport to protected player.");
				}
				else if(inquestion.getName().equalsIgnoreCase(player.getName())){
					player.sendMessage(ChatColor.RED+"Can't teleport to yourself");
				}
				else {
					player.teleportTo(inquestion.getLocation());
					player.sendMessage("OH GOD I'M FLYING AAAAAAAAH");
					j2.log.info("Teleport: " + player.getName() + " teleported to "+inquestion.getName());
				}
			}
			else{
				player.sendMessage(ChatColor.RED+"No such player, or matches multiple");
			}
			event.setCancelled(true);
			return;
		}

		if(split[0].equalsIgnoreCase("/tphere") && j2.hasFlag(player, Flag.ADMIN)){
			List<Player> inquest = j2.getServer().matchPlayer(split[1]);
			if(inquest.size()==1){
				Player inquestion=inquest.get(0);

				if(inquestion.getName().equalsIgnoreCase(player.getName())){
					player.sendMessage(ChatColor.RED+"Can't teleport yourself to yourself. Derp.");
				}
				else {
					inquestion.teleportTo(player.getLocation());
					inquestion.sendMessage("You've been teleported");
					player.sendMessage("Grabbing "+inquestion.getName());
					j2.log.info("Teleport: " + player.getName() + " pulled "+inquestion.getName()+" to self");
				}
			}
			else{
				player.sendMessage(ChatColor.RED+"No such player, or matches multiple");
			}
			event.setCancelled(true);
			return;
		}

		if(split[0].equalsIgnoreCase("/spawn") && j2.hasFlag(player, Flag.FUN)){
			if(!j2.hasFlag(player, Flag.ADMIN)|| split.length<2){
				player.sendMessage(ChatColor.RED+"WHEEEEEEEEEEEEEEE");
				player.teleportTo(player.getWorld().getSpawnLocation());
			}
			else {
				List<Player> inquest = j2.getServer().matchPlayer(split[1]);
				if(inquest.size()==1){
					Player inquestion=inquest.get(0);
					inquestion.teleportTo(inquestion.getWorld().getSpawnLocation());
					inquestion.sendMessage(ChatColor.RED+"OH GOD I'M BEING PULLED TO SPAWN OH GOD");
				}
				else {
					player.sendMessage(ChatColor.RED+"No such player, or matches multiple");
				}
			}
		}

		if(split[0].equalsIgnoreCase("/msg")){
			if(split.length<3){
				player.sendMessage(ChatColor.RED+"Correct usage: /msg player message");
				event.setCancelled(true);
				return;
			}
			List<Player> inquest = j2.getServer().matchPlayer(split[1]);
			if(inquest.size()==1){
				Player inquestion=inquest.get(0);
				player.sendMessage("(MSG) <"+player.getName()+"> "+j2.combineSplit(2, split, " "));
				inquestion.sendMessage("(MSG) <"+player.getName()+"> "+j2.combineSplit(2, split, " "));
				j2.log.info("Msg to "+inquestion.getName()+": <"+player.getName()+"> "+j2.combineSplit(2, split, " "));
			}
			else{
				player.sendMessage(ChatColor.RED+"Could not find player");
			}
			event.setCancelled(true);
			return;
		}

		if((split[0].equalsIgnoreCase("/item") || split[0].equalsIgnoreCase("/i")) && j2.hasFlag(player, Flag.FUN)){
			if (split.length < 2) {
				player.sendMessage(ChatColor.RED+"Correct usage is: /i [item] (amount)");
				event.setCancelled(true);
				return;
			}
			String item = "0";
			int amount = 1;
			int dataType = -1;
			try {
				if(split[1].contains(":")) {
					String[] data = split[1].split(":");

					try {
						dataType = Integer.valueOf(data[1]);
					} catch (NumberFormatException e) {
						dataType = -1;
					}

					item = data[0];
				} else {
					item = split[1];
				}
				if(split.length>2){
					amount = Integer.valueOf(split[2]);
				}
				else{
					amount = 1;
				}
			} catch(NumberFormatException e) {
				player.sendMessage(ChatColor.RED+"Command fail.");
				return;
			}
			Material toDrop=Material.matchMaterial(item);
			int itemid=0;
			if(toDrop!=null){
				itemid=toDrop.getId();
			}
			else{
				player.sendMessage(ChatColor.RED+"Invalid item.");
				event.setCancelled(true);
				return;
			}

			if(dataType != -1) {
				player.getInventory().addItem(new ItemStack(itemid, amount, ((byte)dataType)));
			} else {
				player.getInventory().addItem(new ItemStack(itemid, amount));
			}
			player.sendMessage(ChatColor.RED+"Here you go!");
			j2.log.info("Giving "+player.getName()+" "+amount+" of "+toDrop.toString());
			event.setCancelled(true);
			return;
		}

		if(split[0].equalsIgnoreCase("/time") && j2.hasFlag(player, Flag.ADMIN)){
			if(split.length!=2){
				player.sendMessage(ChatColor.RED+"Usage: /time day|night");
				event.setCancelled(true);
				return;
			}
			long desired;
			if(split[1].equalsIgnoreCase("day")){
				desired=0;
			}
			else if(split[1].equalsIgnoreCase("night")){
				desired=13000;
			}
			else{
				player.sendMessage(ChatColor.RED+"Usage: /time day|night");
				event.setCancelled(true);
				return;
			}

			long curTime=j2.getServer().getWorlds().get(0).getTime();
			long margin = (desired-curTime) % 24000;
			if (margin < 0) {
				margin += 24000;
			}
			j2.getServer().getWorlds().get(0).setTime(curTime+margin);
			player.sendMessage(ChatColor.RED+"Time changed");
			event.setCancelled(true);
			return;
		}

		if(split[0].equalsIgnoreCase("/who") || split[0].equalsIgnoreCase("/playerlist")){
			Player[] players=j2.getServer().getOnlinePlayers();
			String msg="Players ("+players.length+"):";
			for(Player p: players){
				msg+=" "+p.getName();
			}
			player.sendMessage(msg);
			event.setCancelled(true);
			return;
		}

		if(split[0].equalsIgnoreCase("/a") && j2.hasFlag(player, Flag.ADMIN)){
			if(split.length<2){
				event.getPlayer().sendMessage(ChatColor.RED+"Usage: /a Message");
				event.setCancelled(true);
				return;
			}
			String playerName = player.getName();
			String message=j2.combineSplit(1, split, " ");
			j2.chat.aMsg(playerName,message);
			event.setCancelled(true);
			return;
		}

		if(split[0].equalsIgnoreCase("/report")){
			if(split.length>1){
				String playerName = player.getName();
				String theReport=j2.combineSplit(1, split, " ");
				String message="Report: <§d"+playerName+"§f>"+theReport;
				String ircmessage="Report from "+playerName+": "+theReport;
				j2.chat.msgByFlag(Flag.ADMIN, message);
				j2.irc.ircAdminMsg(ircmessage);
				j2.log.info(ircmessage);
				Report report=new Report(0, player.getLocation(), player.getName(), theReport, (new Date().getTime())/1000);
				j2.reports.addReport(report);
				player.sendMessage(ChatColor.RED+"Report transmitted. Thanks! :)");
			}
			else {
				player.sendMessage(ChatColor.RED+"To report to the admins, say /report MESSAGE");
				player.sendMessage(ChatColor.RED+"Where MESSAGE is what you want to tell them");
			}
			event.setCancelled(true);	
			return;
		}
		if(split[0].equalsIgnoreCase("/r") && j2.hasFlag(player, Flag.ADMIN)){
			ArrayList<Report> reps=j2.reports.getReports();
			int size=reps.size();
			if(size==0){
				player.sendMessage(ChatColor.RED+"No reports. Hurray!");
				event.setCancelled(true);
				return;
			}
			player.sendMessage(ChatColor.DARK_PURPLE+"Found "+size+" reports:");
			for(Report r:reps){
				player.sendMessage(ChatColor.DARK_PURPLE+"["+r.getID()+"]<"
						+ChatColor.WHITE+r.getUser()+ChatColor.DARK_PURPLE+"> "+ChatColor.WHITE
						+r.getMessage());
			}
			event.setCancelled(true);
			return;
		}
		if(split[0].equalsIgnoreCase("/g") && j2.hasFlag(player, Flag.ADMIN)){
			if(split.length<2){
				event.getPlayer().sendMessage(ChatColor.RED+"Usage: /g Message");
				event.setCancelled(true);
				return;
			}
			String playerName = player.getName();
			String text = "";
			text+=j2.combineSplit(1, split, " ");
			j2.chat.gMsg(playerName,text);
			event.setCancelled(true);	
			return;
		}
		if(split[0].equalsIgnoreCase("/ban") && j2.hasFlag(player, Flag.ADMIN)){
			if(split.length < 3){
				player.sendMessage(ChatColor.RED+"Usage: /ban playername reason");
				player.sendMessage(ChatColor.RED+"       reason can have spaces in it");
				event.setCancelled(true);
				return;
			}
			String adminName = player.getName();
			j2.kickbans.callBan(adminName,split,player.getLocation());
			event.setCancelled(true);
			return;
		}
		if(split[0].equalsIgnoreCase("/kick") && j2.hasFlag(player, Flag.ADMIN)){
			if(split.length < 3){
				player.sendMessage(ChatColor.RED+"Usage: /kick playername reason");
				event.setCancelled(true);
				return;
			}
			String adminName = player.getName();
			j2.kickbans.callKick(split[1],adminName,j2.combineSplit(2, split, " "));
			event.setCancelled(true);
			return;
		}
		if(split[0].equalsIgnoreCase("/addban") && j2.hasFlag(player, Flag.ADMIN)){
			if(split.length < 3){
				player.sendMessage(ChatColor.RED+"Usage: /addban playername reason");
				player.sendMessage(ChatColor.RED+"        reason can have spaces in it");
				event.setCancelled(true);
				return;
			}
			String adminName = player.getName();
			j2.kickbans.callAddBan(adminName,split,player.getLocation());
			event.setCancelled(true);
			return;
		}

		if((split[0].equalsIgnoreCase("/unban") || split[0].equalsIgnoreCase("/pardon")) && j2.hasFlag(player, Flag.ADMIN)){
			if(split.length < 2){
				player.sendMessage(ChatColor.RED+"Usage: /unban playername");
				event.setCancelled(true);
				return;
			}
			String name=split[1];
			String adminName=player.getName();
			j2.kickbans.unban(adminName, name);
			event.setCancelled(true);
			return;
		}


		/*if(split[0].equalsIgnoreCase("/trust") && player.canUseCommand("/trust")){
			if(split.length < 2){
				player.sendMessage(ChatColor.RED+"Usage: /trust playername");
				return true;
			}
			String playername=split[1];
			String adminName=player.getName();
			j2.trust(playername);
			j2.log.log(Level.INFO, "Trusting " + playername + " by " + adminName);
            j2.msgByCmd("/trust",ChatColor.RED + "Trusting " + playername + " by " + adminName);
			return true;
		}*/

		if(split[0].equalsIgnoreCase("/getgroup") && j2.hasFlag(player, Flag.ADMIN)){
			if(split.length==1){
				player.sendMessage("/getgroup playername");
				event.setCancelled(true);
				return;
			}
			List<Player> match = j2.getServer().matchPlayer(split[1]);
			if(match.size()!=1 || match.get(0)==null){
				player.sendMessage("Player not found");
				event.setCancelled(true);
				return;
			}
			Player who=match.get(0);
			String message="Player "+match.get(0).getName()+": ";
			for(Flag f: j2.users.getAllFlags(who)){
				message+=f.getDescription()+", ";
			}
			player.sendMessage(message);
			j2.log.info(player.getName()+" looked up "+ who.getName());
			event.setCancelled(true);
			return;
		}

		if (split[0].equalsIgnoreCase("/me") && split.length>1)
		{
			String message = "";
			message+=j2.combineSplit(1, split, " ");
			j2.chat.addChat(player.getName(), message);
			j2.irc.ircMsg("* "+ player.getName()+" "+message);
			//don't cancel this after reading it. 
			//TODO: /ignore code will also be here
		}

		if (split[0].equalsIgnoreCase("/forcekick") && j2.hasFlag(player, Flag.ADMIN)){
			if(split.length==1){
				player.sendMessage(ChatColor.RED+"Usage: /forcekick playername");
				player.sendMessage(ChatColor.RED+"       Requires full name");
				event.setCancelled(true);
				return;
			}
			String name=split[1];
			String reason="";
			String admin=player.getName();
			if(split.length>2)
				reason=j2.combineSplit(2, split, " ");
			j2.kickbans.forceKick(name,reason);
			j2.log.log(Level.INFO, "Kicking " + name + " by " + admin + ": " + reason);
			j2.chat.msgByFlag(Flag.ADMIN,ChatColor.RED + "Kicking " + name + " by " + admin + ": " + reason);
			j2.chat.msgByFlagless(Flag.ADMIN,ChatColor.RED + name+" kicked ("+reason+")");
			event.setCancelled(true);
			return;
		}
		if(split[0].equalsIgnoreCase("/ircrefresh") && j2.hasFlag(player, Flag.SRSTAFF)){
			j2.irc.loadIRCAdmins();
			j2.chat.msgByFlag(Flag.SRSTAFF, ChatColor.RED+"IRC admins reloaded by "+player.getName());
			j2.log.info(player.getName()+ " reloaded irc admins");
			event.setCancelled(true);
			return;
		}

		if(split[0].equalsIgnoreCase("/j2reload") && j2.hasFlag(player, Flag.SRSTAFF)){
			j2.loadData();
			j2.chat.msgByFlag(Flag.SRSTAFF, "j2 data reloaded by "+player.getName());
			j2.log.info("j2 data reloaded by "+player.getName());
			event.setCancelled(true);
			return;
		}

		if(split[0].equalsIgnoreCase("/maintenance") && j2.hasFlag(player, Flag.SRSTAFF)){
			if(!j2.maintenance){
				j2.log.info(player.getName()+" has turned on maintenance mode");
				j2.maintenance=true;
				for (Player p : j2.getServer().getOnlinePlayers()) {
					if (p != null && !j2.hasFlag(player, Flag.ADMIN)) {
						p.sendMessage("Server entering maintenance mode");
						p.kickPlayer("Server entering maintenance mode");
					}
				}
				j2.chat.msgByFlag(Flag.ADMIN, "Mainenance mode on, by "+player.getName());
			}
			else{
				j2.log.info(player.getName()+" has turned off maintenance mode");
				j2.chat.msgByFlag(Flag.ADMIN, "Mainenance mode off, by "+player.getName());
				j2.maintenance=false;
			}
			event.setCancelled(true);
			return;
		}

		if(split[0].equalsIgnoreCase("/1x1") && j2.hasFlag(player, Flag.ADMIN)){
			player.sendMessage("Next block you break (not by stick), everything above it goes byebye");
			j2.log.info(player.getName()+" is gonna break a 1x1 tower");
			j2.OneByOne=player;
			event.setCancelled(true);
			return;
		}

		if(split[0].equalsIgnoreCase("/flags") && j2.hasFlag(player, Flag.SRSTAFF)){
			String action=split[2];
			if(split.length<4 || !(action.equalsIgnoreCase("add") || action.equalsIgnoreCase("drop"))){
				player.sendMessage(ChatColor.RED+"Usage: /flags player add/drop flag");
				event.setCancelled(true);
				return;
			}
			String name=split[1];
			char flag=split[3].charAt(0);
			User user=j2.users.getUser(name);
			if(user==null){
				user=j2.mysql.getUser(name);
			}
			if(action.equalsIgnoreCase("add")){
				user.addFlag(Flag.byChar(flag));
			}
			else {
				user.dropFlag(Flag.byChar(flag));
			}
			String log=ChatColor.RED+player.getName()+" changed flags: "+name + " "+ action +" flag "+ Flag.byChar(flag).getDescription();
			j2.chat.msgByFlag(Flag.ADMIN, log);
			j2.log.info(log);
			event.setCancelled(true);
			return;
		}
		if(split[0].equalsIgnoreCase("/loc")) {
			Location p_loc = event.getPlayer().getLocation();
			event.getPlayer().sendMessage("You are located at X:"+p_loc.getBlockX()+" Y:"+p_loc.getBlockY()+" Z:"+p_loc.getBlockZ());
		}
		if(split[0].equalsIgnoreCase("/warp") && j2.hasFlag(player, Flag.FUN)) {
			if(split.length==1){
				String warps_s=j2.warps.listWarps(player);
				if(!warps_s.equalsIgnoreCase("")){
					player.sendMessage(ChatColor.RED+"Warp locations: "+ChatColor.WHITE+warps_s);
					player.sendMessage(ChatColor.RED+"To go to a warp, say /warp warpname");

				}else{
					player.sendMessage("The are no warps available.");
				}
			}
			else{
				Warp warp=j2.warps.getPublicWarp(split[1]);
				if(warp!=null && (j2.hasFlag(player, warp.getFlag())||warp.getFlag().equals(Flag.Z_SPAREWARP_DESIGNATION))){
					player.sendMessage(ChatColor.RED+"Whoosh!");
					player.teleportTo(warp.getLocation());
				}
				else {
					player.sendMessage(ChatColor.RED+"Warp does not exist. For a list, say /warp");
				}

			}
			event.setCancelled(true);
			return;
		}

		if(split[0].equalsIgnoreCase("/home") && j2.hasFlag(player, Flag.FUN)) {
			if(split.length==1){
				String homes_s=j2.warps.listHomes(player.getName());
				if(!homes_s.equalsIgnoreCase("")){
					player.sendMessage(ChatColor.RED+"Homes: "+ChatColor.WHITE+homes_s);
					player.sendMessage(ChatColor.RED+"To go to a home, say /home homename");

				}else{
					player.sendMessage(ChatColor.RED+"You have no homes available.");
					player.sendMessage(ChatColor.RED+"Use the command /sethome");
				}
			}
			else{
				Warp home=j2.warps.getUserWarp(player.getName(),split[1]);
				if(home!=null){
					player.sendMessage(ChatColor.RED+"Whoosh!");
					player.teleportTo(home.getLocation());
				}
				else {
					player.sendMessage(ChatColor.RED+"That home does not exist. For a list, say /home");
				}

			}
			event.setCancelled(true);
			return;
		}
		if(split[0].equalsIgnoreCase("/setwarp") && j2.hasFlag(player, Flag.ADMIN)){
			if(split.length==1){
				player.sendMessage(ChatColor.RED+"Usage: /setwarp warpname");
				player.sendMessage(ChatColor.RED+"optional: /setwarp warpname flag");
				player.sendMessage(ChatColor.RED+"Admin flag is a, trusted is t");
			}
			else{
				Flag flag=Flag.Z_SPAREWARP_DESIGNATION;
				if(split.length>2){
					flag=Flag.byChar(split[2].charAt(0));
				}
				Warp newWarp=new Warp(split[1], player.getName(), player.getLocation(), flag);
				j2.warps.addWarp(newWarp);
				player.sendMessage(ChatColor.RED+"Warp created");
			}
			event.setCancelled(true);
			return;
		}
		if(split[0].equalsIgnoreCase("/sethome") && j2.hasFlag(player, Flag.FUN)){
			if(split.length==1){
				player.sendMessage(ChatColor.RED+"Usage: /sethome name");
			}
			else{
				Warp newWarp=new Warp(split[1], player.getName(), player.getLocation(), Flag.byChar('0'));
				j2.warps.addWarp(newWarp);
				player.sendMessage(ChatColor.RED+"Home created");
			}
			event.setCancelled(true);
			return;
		}
		if(split[0].equalsIgnoreCase("/removewarp") && j2.hasFlag(player, Flag.ADMIN) && split.length>1){
			String toRemove=split[1];
			player.sendMessage(ChatColor.RED+"Removing warp "+toRemove);
			j2.warps.killWarp(j2.warps.getPublicWarp(toRemove));
			event.setCancelled(true);
			return;
		}
		if(split[0].equalsIgnoreCase("/removehome") && j2.hasFlag(player,Flag.FUN)){
			if(split.length==1){
				player.sendMessage(ChatColor.RED+"Usage: /removehome homename");
				if(j2.hasFlag(player, Flag.ADMIN)){
					player.sendMessage(ChatColor.RED+"Or: /removehome homename playername");
				}
			}
			if(split.length==2){
				String toRemove=split[1];
				player.sendMessage(ChatColor.RED+"Removing home "+toRemove);
				j2.warps.killWarp(j2.warps.getUserWarp(player.getName(), toRemove));
			}
			if(split.length==3 && j2.hasFlag(player, Flag.ADMIN)){
				String toRemove=split[1];
				String plr=split[2];
				player.sendMessage(ChatColor.RED+"Removing home "+toRemove+" of player "+plr);
				j2.warps.killWarp(j2.warps.getUserWarp(plr, toRemove));
			}
			event.setCancelled(true);
			return;
		}
		if((split[0].equalsIgnoreCase("/homeinvasion")||
				split[0].equalsIgnoreCase("/invasion")||
				split[0].equalsIgnoreCase("/hi"))
				&& j2.hasFlag(player,Flag.ADMIN)){
			if(split.length==1){
				player.sendMessage(ChatColor.RED+"Usage: /homeinvasion player");
				player.sendMessage(ChatColor.RED+"      to get a list");
				player.sendMessage(ChatColor.RED+"       /homeinvasion player homename");
				player.sendMessage(ChatColor.RED+"      to visit a specific home");
			}
			if(split.length==2){
				String target=split[1];
				boolean isOnline=j2.users.isOnline(target);
				if(!isOnline){
					j2.warps.loadPlayer(target);
				}
				player.sendMessage(ChatColor.RED+target+" warps: "+ChatColor.WHITE+j2.warps.listHomes(target));
				if(!isOnline){
					j2.warps.dropPlayer(target);
				}
			}
			if(split.length==3){
				String target=split[1];
				boolean isOnline=j2.users.isOnline(target);
				if(!isOnline){
					j2.warps.loadPlayer(target);
				}
				Warp warptarget=j2.warps.getUserWarp(target, split[2]);
				if(warptarget!=null){
					player.sendMessage(ChatColor.RED+"Whooooosh!  *crash*");
					player.teleportTo(warptarget.getLocation());
				}
				else {
					player.sendMessage(ChatColor.RED+"No such home");
				}
				if(!isOnline){
					j2.warps.dropPlayer(target);
				}
			}
			event.setCancelled(true);
			return;
		}
		if(split[0].equalsIgnoreCase("/clearinventory")){
			if(!j2.hasFlag(player, Flag.ADMIN)||split.length==1){
				player.getInventory().clear();
				player.sendMessage(ChatColor.RED+"Inventory emptied");
				j2.log.info(player.getName()+" emptied inventory");
			}
			else {
				List<Player> targets=j2.getServer().matchPlayer(split[1]);
				if(targets.size()==1){
					Player target=targets.get(0);
					target.getInventory().clear();
					target.sendMessage(ChatColor.RED+"Your inventory has been cleared by an admin");
					j2.log.info(player.getName()+" emptied inventory of "+target.getName());
				}
				else {
					player.sendMessage(ChatColor.RED+"Found "+targets.size()+" matches. Try again");
				}
			}
			event.setCancelled(true);
			return;
		}
		if(split[0].equalsIgnoreCase("/mobhere")
				&&j2.hasFlag(player, Flag.SRSTAFF)){
			if(split.length==1){
				player.sendMessage(ChatColor.RED+"/mobhere mobname");
			}
			else {
				player.getLocation().getWorld().spawnCreature(player.getLocation(), CreatureType.valueOf(split[1]));
			}
			event.setCancelled(true);
			return;
		}
		if(split[0].equalsIgnoreCase("/over9000")
				&&j2.hasFlag(player, Flag.ADMIN)){
			String name=player.getName();
			j2.chat.msgAll(ChatColor.RED+"!!! "+ChatColor.DARK_RED+name+" is ON FIRE !!!");
			j2.chat.msgAll(ChatColor.RED+"    Also, "+name+" is an admin. Pay attention to "+name);
			j2.users.getUser(name).tempSetColor(ChatColor.DARK_RED);
			player.getInventory().setHelmet(new ItemStack(51));
			j2.log.info(name+" set mode to SUPERSAIYAN");
			event.setCancelled(true);
			return;
		}
		if(split[0].equalsIgnoreCase("/under9000")
				&&j2.hasFlag(player, Flag.ADMIN)){
			String name=player.getName();
			player.sendMessage(ChatColor.RED+"You fizzle out");
			j2.users.getUser(name).restoreColor();
			player.getInventory().setHelmet(new ItemStack(2));
			j2.log.info(name+" set mode to NOT-SO-SAIYAN");
			event.setCancelled(true);
			return;
		}
		if((split[0].equalsIgnoreCase("/coo")||
				split[0].equalsIgnoreCase("/xyz"))
				&&j2.hasFlag(player, Flag.ADMIN)){
			if(split.length<4){
				player.sendMessage(ChatColor.RED+"You did not specify an X, Y, and Z");
			}
			else {
				player.teleportTo(new Location(player.getWorld(),Double.valueOf(split[1]),Double.valueOf(split[2]),Double.valueOf(split[3]),0,0));
				player.sendMessage(ChatColor.RED+"WHEEEEE I HOPE THIS ISN'T UNDERGROUND");
			}
			event.setCancelled(true);
			return;
		}
		if(split[0].equalsIgnoreCase("/whereis") && j2.hasFlag(player,Flag.ADMIN)){
			if(split.length==1){
				player.sendMessage(ChatColor.RED+"/whereis player");
			}
			else {
				List<Player> possible=j2.getServer().matchPlayer(split[1]);
				if(possible.size()==1){
					Player who=possible.get(0);
					Location loc=who.getLocation();
					player.sendMessage(ChatColor.RED+who.getName()+": "+loc.getX()+" "+loc.getY()+" "+loc.getZ());
				}
				else {
					player.sendMessage(ChatColor.RED+split[1]+" does not work. Either 0 or 2+ matches.");
				}
			}
			event.setCancelled(true);
			return;
		}
	}
}
