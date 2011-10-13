package to.joe.manager.serverlink;

public interface ConnectionCallback {
    
    String checkKey(String key);
    void process(String name, String line);
    void disconnect(String name);
    
}