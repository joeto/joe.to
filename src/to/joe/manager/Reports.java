package to.joe.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import to.joe.J2;
import to.joe.util.Report;

/**
 * Report manager
 * 
 */
public class Reports {
    private HashMap<Integer,Report> reports;
    J2 j2;
    /**
     * current highest report id
     */
    public int maxid;
    private final Object sync = new Object();

    public Reports(J2 j2) {
        this.j2 = j2;
        this.restartManager();
        this.maxid = -1;
    }

    /**
     * Reset manager
     */
    public void restartManager() {
        synchronized (this.sync) {
            this.reports = new HashMap<Integer,Report>();
            this.maxid=-1;
        }
    }

    /**
     * Add report
     * 
     * @param newReport
     */
    public void addReport(Report newReport) {
        this.j2.mysql.addReport(newReport);
    }

    /**
     * Add report, alert IRC
     * 
     * @param report
     */
    public void addReportAndAlert(Report report) {
        this.j2.irc.messageAdmins(ChatColor.stripColor(this.addReportViaSQL(report)));
    }

    /**
     * Add report, return alert message
     * 
     * @param report
     * @return
     */
    public String addReportViaSQL(Report report) {
        this.reports.put(report.getID(),report);
        if (report.getID() > this.maxid) {
            this.maxid = report.getID();
        }
        final Location location = report.getLocation();
        final String pc = ChatColor.DARK_PURPLE.toString();
        final String gc = ChatColor.GOLD.toString();
        final String wc = ChatColor.WHITE.toString();
        final String x = gc + location.getBlockX() + pc + ",";
        final String y = gc + location.getBlockY() + pc + ",";
        final String z = gc + location.getBlockZ() + pc;
        final String message = pc + "[" + wc + "NEW REPORT" + pc + "][" + report.getID() + "][" + x + y + z + "]<" + gc + report.getUser() + pc + "> " + wc + report.getMessage();
        this.j2.sendAdminPlusLog(message);
        return message;
    }
    
    /**
     * Add report,  no alert message
     * 
     * @param report
     * @return
     */
    public void addReportViaSQLSilent(Report report) {
        this.reports.put(report.getID(),report);
        if (report.getID() > this.maxid) {
            this.maxid = report.getID();
        }
    }
    
    /**
     * Close a report
     * 
     * @param id
     * @param admin
     * @param reason
     */
    public void close(int id, String admin, String reason) {
        synchronized (this.sync) {
            final Report r = this.getReport(id);
            if (r != null) {
                this.j2.debug("Closing report " + id);
                this.j2.mysql.closeReport(id, admin, reason);
                this.reports.remove(id);
            }
        }
    }

    /**
     * Get report by id.
     * 
     * @param id
     * @return
     */
    public Report getReport(int id) {
        synchronized (this.sync) {
            return this.reports.get(id);
        }
    }

    /**
     * Count of reports on server.
     * 
     * @return
     */
    public int numReports() {
        synchronized (this.sync) {
            final int answer = this.reports.size();
            this.j2.debug("Reporting " + answer + " reports");
            return answer;
        }
    }

    /**
     * Get list of reports.
     * 
     * @return
     */
    public ArrayList<Report> getReports() {
        return new ArrayList<Report>(this.reports.values());
    }
}
