package to.joe.util;

/*
 * j2Ban
 * 
 * Classy bans for the classy gentleman
 */

public class Ban {
	private String name, reason;
	private boolean unbanned, temp;
	private long timeOfUnban,timeLoaded,timeOfBan;
	public Ban(String name, String reason, long timeOfUnban,long timeLoaded, long timeOfBan, boolean unbanned){
		this.name=name;
		this.reason=reason;
		if(this.reason==null || this.reason==""){
			this.reason="Banned";
		}
		this.timeOfUnban=timeOfUnban;
		this.unbanned=unbanned;
		if(timeOfUnban==0)
			this.temp=false;
		else
			this.temp=true;
		this.timeLoaded=timeLoaded;
		this.timeOfBan=timeOfBan;
	}
	public String getName(){
		return name;
	}
	public String getReason(){
		return reason;
	}
	public long getTimeOfUnban(){
		return timeOfUnban;
	}
	public long getTimeOfBan(){
		return timeOfBan;
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
