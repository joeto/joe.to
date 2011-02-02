package com.J2;

import org.bukkit.entity.Player;

public class J2PlugPermissions {
	private J2Plugin j2;
	
	public J2PlugPermissions(J2Plugin j2p){
		j2=j2p;
	}
	
	public boolean isAtOrAbove(int l, Player player){
		if(playerLevel(player)>l-1){
			return true;
		}
		return false;
	}

	public int playerLevel(Player player){
		return 1;
	}
	
	public int commandLevel(String command){
		return 1;
	}
	
	public boolean canUseCommand(Player player, String command){
		if(playerLevel(player)>=commandLevel(command)){
			return true;
		}
		return false;
	}
}
