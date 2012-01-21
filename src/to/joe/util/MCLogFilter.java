package to.joe.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Filter on the log. Removes some lines Sends some other lines to secondary.log
 * as well
 * 
 */
public class MCLogFilter implements Filter {

    private final ArrayList<String> strings;
    private final SecondaryLog slog;

    public MCLogFilter() {
        this.strings = new ArrayList<String>();
        this.strings.add("Can't keep up! Did the system time change, or is the server overloaded?");
        this.slog = new SecondaryLog();
    }

    Pattern p1 = Pattern.compile("(<).*?(>)( )", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    @Override
    public boolean isLoggable(LogRecord logRecord) {
        final String message = logRecord.getMessage();
        final Matcher m1 = this.p1.matcher(message);
        if (m1.find() || message.startsWith("* ") || message.contains("[J2CMD]") || message.contains("[HARASS]BLOCKED")) {
            this.slog.add(message);
        }
        return !this.strings.contains(message);
    }

    /**
     * The Secondary logging system
     * 
     */
    private class SecondaryLog {
        private final Logger log;
        private FileHandler fh;
        private boolean canLog;

        public SecondaryLog() {
            this.canLog = true;
            this.log = Logger.getLogger("j2log");
            try {
                this.fh = new FileHandler("secondary.log", true);
            } catch (final SecurityException e) {
                this.canLog = false;
            } catch (final IOException e) {
                this.canLog = false;
            }
            this.fh.setFormatter(new j2Formatter());
            this.log.addHandler(this.fh);
            this.log.setUseParentHandlers(false);
        }

        /**
         * Add a line to the log
         * 
         * @param line
         */
        public void add(String line) {
            if (this.canLog) {
                this.log.info(line);
            }
        }

        private class j2Formatter extends Formatter {
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");

            @Override
            public String format(LogRecord lr) {
                return this.dateformat.format(new Date(lr.getMillis())) + " " + lr.getMessage() + "\n";
            }
        }
    }
}