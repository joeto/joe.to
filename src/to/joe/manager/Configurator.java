package to.joe.manager;

import java.util.HashMap;

import to.joe.J2;
@SuppressWarnings("unused")
public class Configurator {
	
	private J2 j2;
	private HashMap<String,Object> configObjects;
	public Configurator(J2 j2){
		this.j2=j2;
		this.configObjects=new HashMap<String,Object>();
	}
	public void load(){
		
	}
	
}
