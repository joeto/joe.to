package to.joe;

import java.util.ArrayList;

public class reportManager {
	private ArrayList<report> reports;
	public reportManager(){
		reports=new ArrayList<report>();
	}
	public void addReport(report newReport){
		reports.add(newReport);
	}
	public void delReport(int id){
		report r=getReport(id);
		if(r!=null){
			reports.remove(r);
		}
	}
	public report getReport(int id){
		for (report r:reports){
			if(r.getID()==id){
				return r;
			}
		}
		return null;
	}
}
