package to.joe.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;


public class DogLog {
    private final Logger log;
    private FileHandler fh;
    private boolean canLog;

    public DogLog(int serverNumber) {
        this.canLog = true;
        this.log = Logger.getLogger("wooflog");
        try {
            this.fh = new FileHandler("/home/minecraft/public_html/detector/"+serverNumber+"/woof.txt", true);
        } catch (final Exception e) {
            this.canLog = false;
            System.out.println(e.getMessage());
            return;
        }
        this.fh.setFormatter(new WoofFormatter());
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

    private class WoofFormatter extends Formatter {
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");

        @Override
        public String format(LogRecord lr) {
            return this.dateformat.format(new Date(lr.getMillis())) + " " + lr.getMessage() + "\n";
        }
    }
}
