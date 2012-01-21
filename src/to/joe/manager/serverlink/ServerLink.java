package to.joe.manager.serverlink;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import to.joe.J2;
@SuppressWarnings("deprecation")
public class ServerLink implements ConnectionCallback {
    
    private final Connection conn = new Connection(this);
    private boolean running = true;
    
    private J2 j2;
    private Configuration conf;
    
    public ServerLink(J2 j2){
        this.j2=j2;
    }
    
    public void start() {
        this.conf=new Configuration(new File(this.j2.getDataFolder(),"serverlink.yml"));
        conf.load();
        try {
            conn.startServer(conf.getInt("settings.port", 56386));
            conf.save();
        }
        catch (IOException ex) {
            printException("Could not start listening for connections", ex);
        }
        connectOutgoing();
        
        System.out.println(this + " is now enabled!");
        this.startTimer();
    }
    private void startTimer() {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ping();
            }
        }, 10000, 10000);
    }
    private void ping(){
        try {
            conn.ping();
        }
        catch (LostConnectionException ex) {
            String host = ex.getHost();
            this.j2.log("[ServerLink] Lost connection to " + host + ", reconnecting");
            ConfigurationNode node = conf.getNode("outgoing." + host);
            try {
                conn.connect(host, node.getString("hostname", ""), node.getInt("port", 0), node.getString("key", ""), true);
            }
            catch (IOException ex2) {
                // printException("Could not re-connect to " + host, ex2);
                connectOutgoing(); // starts the retry cycle
            }
        }
    }
    private void connectOutgoing() {
        boolean success = true;
        for (Entry<String,ConfigurationNode> set:conf.getNodes("outgoing").entrySet()) {
            if(set==null){
                continue;
            }
            String key=set.getKey();
            try {
                if (conn.connect(key, set.getValue().getString("hostname",""), set.getValue().getInt("port", 0), set.getValue().getString("key", ""))) {
                    System.out.println("[ServerLink] Connected to " + key);
                }
            }
            catch (IOException ex) {
                success = false;
                printException("Could not connect to " + key, ex);
            }
        }
        if (!success) {
            System.out.println("[ServerLink] Could not connect to some servers, retrying in 60 seconds");
            this.j2.getServer().getScheduler().scheduleSyncDelayedTask(this.j2, new Runnable() {
                public void run() {
                    connectOutgoing();
                }
            }, 30 * 20);
        }
    }
    
    public void onDisable() {
        if (!running) return;
        try {
            conn.disconnectAll();
        }
        catch (IOException ex) {
            printException("Error shutting down", ex);
        }
        System.out.println(this + " is now disabled!");
    }
    
    
    public void broadcastChat(String text) {
        broadcast("chat " + text);
    }

    public String checkKey(String key) {
        int i = key.indexOf(':');
        String name = key.substring(0, i);
        String pass = key.substring(i + 1);
        
        String realPass = conf.getString("incoming." + name + ".key");
        if (realPass == null || !pass.equals(realPass)) {
            System.out.println("[ServerLink] Server " + name + " had invalid key");
            this.j2.getServer().getScheduler().scheduleSyncDelayedTask(this.j2, new Runnable() {
                public void run() {
                    connectOutgoing();
                }
            }, 30 * 20);
            return null;
        }
        System.out.println("[ServerLink] Server " + name + " connected");
        return name;
    }

    public void process(String name, String line) {
        // System.out.println("[ServerLink] From " + name + ": " + line);
        if (line.startsWith("chat ")) {
            this.j2.sendAdminPlusLog(formatColor("incoming." + name + ".chat-prefix", "(" + name + ") ") + line.substring(5));
        }
    }
    
    private void broadcast(String line) {
        try {
            conn.broadcast(line);
        }
        catch (LostConnectionException ex) {
            String host = ex.getHost();
            System.out.println("[ServerLink] Lost connection to " + host + ", reconnecting");
            ConfigurationNode node = conf.getNode("outgoing." + host);
            try {
                conn.connect(host, node.getString("hostname", ""), node.getInt("port", 0), node.getString("key", ""), true);
                broadcast(line);
            }
            catch (IOException ex2) {
                // printException("Could not re-connect to " + host, ex2);
                connectOutgoing(); // starts the retry cycle
            }
        }
    }
    
    private String formatColor(String node, String def) {
        return conf.getString(node, def).replace('`', '\u00A7');
    }
    
    private void printException(String message, Exception ex) {
        System.out.println("[ServerLink] " + message + ": " + ex.getMessage());
        if (conf.getBoolean("settings.debug", false)) {
            ex.printStackTrace();
        }
    }

    @Override
    public void disconnect(String name) {
        // TODO Stuff might go here
        
    }
}
