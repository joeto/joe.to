package to.joe.manager;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import to.joe.J2;
import to.joe.util.Report;

/**
 * Report manager
 * @author matt
 *
 */
public class Reports {
	private ArrayList<Report> reports;
	J2 j2;
	/**
	 * current highest report id
	 */
	public int maxid;
	private Object sync=new Object();
	public Reports(J2 j2){
		this.j2=j2;
		this.restartManager();
		maxid=-1;
	}
	/**
	 * Reset manager
	 */
	public void restartManager(){
		synchronized(sync){
			this.reports=new ArrayList<Report>();
		}
	}
	/**
	 * Add report
	 * @param newReport
	 */
	public void addReport(Report newReport){
		this.j2.mysql.addReport(newReport);
	}
	/**
	 * Add report, alert IRC
	 * @param report
	 */
	public void addReportAndAlert(Report report){
		this.j2.irc.messageAdmins(ChatColor.stripColor(addReportViaSQL(report)));
	}
	/**
	 * Add report, return alert message
	 * @param report
	 * @return
	 */
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
	/**
	 * Close a report
	 * @param id
	 * @param admin
	 * @param reason
	 */
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

	/**
	 * Get report by id.
	 * @param id
	 * @return
	 */
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
	/**
	 * Count of reports on server.
	 * @return
	 */
	public int numReports(){
		synchronized(sync){
			int answer=this.reports.size();
			this.j2.debug("Reporting "+answer+" reports");
			return answer;
		}
	}
	/**
	 * Get list of reports.
	 * @return
	 */
	public ArrayList<Report> getReports(){
		return new ArrayList<Report>(this.reports);
	}
}
