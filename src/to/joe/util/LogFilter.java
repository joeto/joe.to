package to.joe.util;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class LogFilter implements Filter {

	private String _cantKeepUpString = "Can't keep up! Did the system time change, or is the server overloaded?";

	public boolean isLoggable(LogRecord logRecord)
	{
		return !logRecord.getMessage().equals(this._cantKeepUpString);
	}
}