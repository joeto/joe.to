package to.joe.util.BanCooperative;

public abstract class BanCoopBan {

    private final String reason;
    private final String server;

    public BanCoopBan(String server, String reason) {
        this.server = server;
        this.reason = reason;
    }

    public String getReason() {
        return this.reason;
    }

    public String getServer() {
        return this.server;
    }

    @Override
    public abstract String toString();

}
