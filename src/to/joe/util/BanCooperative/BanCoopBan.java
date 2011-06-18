package to.joe.util.BanCooperative;

public abstract class BanCoopBan {
	
	private String reason;
	private String server;
		
	public BanCoopBan(String server, String reason){
		this.server=server;
		this.reason=reason;
	}
	
	public String getReason(){
		return this.reason;
	}
	
	public String getServer(){
		return this.server;
	}
	
	public abstract String toString();
	
}
