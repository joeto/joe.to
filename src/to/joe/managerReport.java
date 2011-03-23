package to.joe;

import java.util.ArrayList;

public class managerReport {
	private ArrayList<Report> reports;
	J2Plugin j2;
	public managerReport(J2Plugin j2){
		this.reports=new ArrayList<Report>();
		this.j2=j2;
	}
	public void addReport(Report newReport){
		this.j2.mysql.addReport(newReport);
		this.reports.add(newReport);
	}
	public void addReportViaSQL(Report newReport){
		this.reports.add(newReport);
	}
	public void reportID(long time,int id){
		for(Report r:this.reports){
			if(r.getTime()==time){
				r.setID(id);
			}
		}
	}
	public void close(int id, String admin, String reason){
		Report r=getReport(id);
		if(r!=null){
			this.j2.mysql.closeReport(id, admin, reason);
			reports.remove(r);
		}
	}
	
	public Report getReport(int id){
		for (Report r:this.reports){
			if(r.getID()==id){
				return r;
			}
		}
		return null;
	}
	public ArrayList<Report> getReports(){
		return this.reports;
	}
}
