package to.joe.util.BanCooperative;

public enum BanCoopType {
	MCBANS("MCBans"),
	MCBOUNCER("MCBouncer");
	
	private String name;
	private BanCoopType(String name){
		this.name=name;
	}
	
	public String toString(){
		return this.name;
	}
}
