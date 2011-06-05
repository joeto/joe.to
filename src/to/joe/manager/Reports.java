package to.joe.manager;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;

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
	public void addReportAndAlert(Report report){
		this.j2.irc.ircAdminMsg(ChatColor.stripColor(addReportViaSQL(report)));
	}
	public String addReportViaSQL(Report report){
		this.reports.add(report);
		if(report.getID()>maxid){
			maxid=report.getID();
		}
		Location location=report.getLocation();
		String pc=ChatColor.DARK_PURPLE.toString();
		String gc=ChatColor.GOLD.toString();
		String wc=ChatColor.WHITE.toString();
		String x=gc+location.getBlockX()+pc+",";
		String y=gc+location.getBlockY()+pc+",";
		String z=gc+location.getBlockZ()+pc;
		String message=pc+"["+wc+"NEW REPORT"+pc+"]["+report.getID()+"]["+x+y+z+"]<"
				+gc+report.getUser()+pc+"> "+wc+report.getMessage();
		this.j2.sendAdminPlusLog(message);
		return message;
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
	public int numReports(){
		synchronized(sync){
			return this.reports.size();
		}
	}
	public ArrayList<Report> getReports(){
		return new ArrayList<Report>(this.reports);
	}
}
