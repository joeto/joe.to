package to.joe.util.BanCooperative;

import org.bukkit.ChatColor;

public class BanCoopBanMCBans extends BanCoopBan{

	private mcbType type;
	
	public BanCoopBanMCBans(String mishmash, String type) {
		super(split(mishmash)[0],split(mishmash)[1]);
		if(type.equals("g")){
			this.type=mcbType.GLOBAL;
		}
		else if(type.equals("l")){
			this.type=mcbType.LOCAL;
		}
		else {
			this.type=mcbType.UNKNOWN;
		}
	}
	
	private static String[] split(String mishmash){
		String[] split=mishmash.split(" .:. ");
		String[] result=new String[2];
		if(split.length>1){
			result[0]=split[0];
			result[1]=split[1];
		}
		else{
			result[0]="";
			result[1]="";
		}
		return result;
	}

	@Override
	public String toString() {
		return ChatColor.AQUA+"[MCBANS] "+ChatColor.GREEN+"["+type+"]<"+this.getServer()+"> "+ChatColor.AQUA+this.getReason();
	}
	
	private enum mcbType{
		GLOBAL("G"),
		LOCAL("L"),
		UNKNOWN("?");
		private String type;
		private mcbType(String type){
			this.type=type;
		}
		public String toString(){
			return this.type;
		}
	}

}
