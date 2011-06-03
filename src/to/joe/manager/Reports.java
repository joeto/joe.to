package to.joe.manager;

import java.util.ArrayList;

import to.joe.J2;
import to.joe.util.Report;

public class Reports {
	private ArrayList<Report> reports;
	J2 j2;
	public int maxid;
	private Object sync=new Object();
	public Reports(J2 j2){
		this.j2=j2;
		this.restartManager();
		maxid=-1;
	}
	public void restartManager(){
		synchronized(sync){
			this.reports=new ArrayList<Report>();
		}
	}
	public void addReport(Report newReport){
		this.j2.mysql.addReport(newReport);
	}
	public void addReportViaSQL(Report newReport){
		this.reports.add(newReport);
		if(newReport.getID()>maxid){
			maxid=newReport.getID();
		}
	}
	public void close(int id, String admin, String reason){
		synchronized(sync){
			Report r=getReport(id);
			if(r!=null){
				this.j2.debug("Closing report "+id);
				this.j2.mysql.closeReport(id, admin, reason);
				reports.remove(r);
			}
		}
	}

	public Report getReport(int id){
		synchronized(sync){
			for (Report r:this.reports){
				if(r.getID()==id){
					return r;
				}
			}
			return null;
		}
	}
	public ArrayList<Report> getReports(){
		return new ArrayList<Report>(this.reports);
	}
}
