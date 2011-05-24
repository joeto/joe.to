package to.joe.manager;

import java.util.Date;
import java.util.HashMap;

import to.joe.J2;

public class ActivityTracker {
	//private J2Plugin j2;
	private HashMap<String,Long> activity;
	private Object sync=new Object();
	
	public ActivityTracker(J2 j2){
	//	this.j2=j2;
		this.activity=new HashMap<String,Long>();
	}
	
	public void update(String name){
		long time=(new Date()).getTime();
		synchronized(sync){
			this.activity.put(name, time);
		}
	}
	
	public long getLast(String name){
		synchronized(sync){
			return this.activity.get(name);
		}
	}
	
	public void delete(String name){
		synchronized(sync){
			this.activity.remove(name);
		}
	}
}
