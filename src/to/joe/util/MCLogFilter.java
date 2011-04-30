package to.joe.util;

import java.util.ArrayList;
import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class MCLogFilter implements Filter {

	private ArrayList<String> strings;
	public MCLogFilter(){
		strings=new ArrayList<String>();
		strings.add("Can't keep up! Did the system time change, or is the server overloaded?");
	}
	public boolean isLoggable(LogRecord logRecord)
	{
		return !strings.contains(logRecord.getMessage());
	}
}