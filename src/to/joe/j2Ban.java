package to.joe;

/*
 * j2Ban
 * 
 * Classy bans for the classy gentleman
 */

public class j2Ban {
	private String name, reason;
	private boolean unbanned, temp;
	private long timeOfUnban,timeLoaded;
	public j2Ban(String n, String r, long t,long tl){
		name=n;
		reason=r;
		if(reason==null || reason==""){
			reason="Banned";
		}
		timeOfUnban=t;
		unbanned=false;
		if(t==0)
			temp=false;
		else
			temp=true;
		timeLoaded=tl;
	}
	public String getName(){
		return name;
	}
	public String getReason(){
		return reason;
	}
	public long getTime(){
		return timeOfUnban;
	}
	public long getTimeLoaded(){
		return timeLoaded;
	}
	public boolean isTemp(){
		return temp;
	}
	public boolean isBanned(){
		return !unbanned;
	}
	public void unBan(){
		unbanned=true;
	}
}
