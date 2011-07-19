package to.joe.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Filter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * Filter on the log. 
 * Removes some lines
 * Sends some other lines to secondary.log as well
 * @author matt
 *
 */
public class MCLogFilter implements Filter {

	private ArrayList<String> strings;
	private SecondaryLog slog;
	public MCLogFilter(){
		strings=new ArrayList<String>();
		strings.add("Can't keep up! Did the system time change, or is the server overloaded?");
		this.slog=new SecondaryLog();
	}
	Pattern p1 = Pattern.compile("(<).*?(>)( )",Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    
	@Override
	public boolean isLoggable(LogRecord logRecord)
	{
		String message=logRecord.getMessage();
		Matcher m1 = p1.matcher(message);
	    if (m1.find()||message.startsWith("* ")||message.contains("[J2CMD]")||message.contains("[HARASS]BLOCKED")){
			slog.add(message);
		}
		return !strings.contains(message);
	}
	
	/**
	 * The Secondary logging system
	 * @author matt
	 *
	 */
	private class SecondaryLog {
		private Logger log;
		private FileHandler fh;
		private boolean canLog;
		public SecondaryLog(){
			this.canLog=true;
			this.log=Logger.getLogger("j2log");
			try {
				this.fh=new FileHandler("secondary.log",true);
			} catch (SecurityException e) {
				this.canLog=false;
			} catch (IOException e) {
				this.canLog=false;
			}
			this.fh.setFormatter(new j2Formatter());
			this.log.addHandler(this.fh);
			this.log.setUseParentHandlers(false);
		}
		
		/**
		 * Add a line to the log
		 * @param line
		 */
		public void add(String line){
			if(this.canLog){
				this.log.info(line);
			}
		}
		
		private class j2Formatter extends Formatter{
			SimpleDateFormat dateformat=new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
			@Override
			public String format(LogRecord lr) {
				return dateformat.format(new Date(lr.getMillis()))+" "+lr.getMessage()+"\n";
			}
		}
	}
}