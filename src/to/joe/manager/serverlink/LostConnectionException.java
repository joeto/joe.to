package to.joe.manager.serverlink;

public class LostConnectionException extends Exception {
    
    private static final long serialVersionUID = 8858223535521555873L;
    private final String host;
    
    public LostConnectionException(String host) {
        super("Lost connection to " + host);
        this.host = host;
    }
    
    public String getHost() {
        return host;
    }
    
}