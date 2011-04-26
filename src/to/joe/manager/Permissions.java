package to.joe.manager;

import java.util.HashMap;
import to.joe.J2Plugin;
import to.joe.util.Flag;

public class Permissions {

	public Permissions(J2Plugin j2){
		this.j2=j2;
		perms=new HashMap<String,Flag>();
		load();
	}
	
	public void load(){
		
	}
	
	public boolean permCheck(String playername,String permission){
		return j2.hasFlag(playername,Flag.SRSTAFF)||j2.hasFlag(playername, perms.get(permission));
	}
	
	private J2Plugin j2;
	private HashMap<String,Flag> perms;
}
