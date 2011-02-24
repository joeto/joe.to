package to.joe;

import java.util.ArrayList;

public class managerReport {
	private ArrayList<Report> reports;
	public managerReport(){
		reports=new ArrayList<Report>();
	}
	public void addReport(Report newReport){
		reports.add(newReport);
	}
	public void reportID(long time,int id){
		for(Report r:reports){
			if(r.getTime()==time){
				r.setID(id);
			}
		}
	}
	public void delReport(int id){
		Report r=getReport(id);
		if(r!=null){
			reports.remove(r);
		}
	}
	public Report getReport(int id){
		for (Report r:reports){
			if(r.getID()==id){
				return r;
			}
		}
		return null;
	}
}
