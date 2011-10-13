package to.joe.manager.serverlink;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

public class Connection {
    
    private final HashMap<String, Socket> outgoing = new HashMap<String, Socket>();
    private final HashMap<Socket, ConnectionInfo> incoming = new HashMap<Socket, ConnectionInfo>();
    private final ServerThread serverThread = new ServerThread();
    private final ConnectionCallback callback;
    private ServerSocket serverSocket;
    
    public Connection(ConnectionCallback callback) {
        this.callback = callback;
    }
    
    public boolean connect(String id, String hostname, int port, String key, boolean force) throws IOException {
        if (outgoing.containsKey(id) && !force) return false;
        
        Socket socket = new Socket(hostname, port);
        socket.getOutputStream().write(makeLine("identify " + key));
        outgoing.put(id, socket);
        return true;
    }
    
    public boolean connect(String id, String hostname, int port, String key) throws IOException {
        return connect(id, hostname, port, key, false);
    }
    
    public void startServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(10);
        serverThread.start();
    }
    public void ping() throws LostConnectionException {
        for (String name : outgoing.keySet()) {
            Socket socket = outgoing.get(name);
            try {
                socket.getOutputStream().write(makeLine("ping"));
            }
            catch (IOException ex) {
                throw new LostConnectionException(name);
            }
        }
    }
    public void disconnectAll() throws IOException {
        serverThread.running = false;
        try {
            Thread.sleep(150);
        }
        catch (InterruptedException ex) {}
        serverSocket.close();
        for (Socket socket : outgoing.values()) {
            socket.close();
        }
        for (Socket socket : incoming.keySet()) {
            socket.close();
        }
    }
    
    public void broadcast(String line) throws LostConnectionException {
        for (String name : outgoing.keySet()) {
            Socket socket = outgoing.get(name);
            try {
                socket.getOutputStream().write(makeLine(line));
            }
            catch (IOException ex) {
                throw new LostConnectionException(name);
            }
        }
    }
    
    private byte[] makeLine(String line) {
        return (line + "\n").getBytes();
    }

    private class ServerThread extends Thread {

        public boolean running = true;
        
        @Override
        public void run() {
            running = true;
            while (running) {
                try {
                    Socket sock = serverSocket.accept();
                    sock.setSoTimeout(10);
                    incoming.put(sock, new ConnectionInfo());
                }
                catch (SocketTimeoutException ex) {}
                catch (IOException ex) {
                    System.out.println("[ServerLink] Error accepting socket: " + ex.getMessage());
                }
                
                byte[] buffer = new byte[1024];
                
                for (Map.Entry<Socket, ConnectionInfo> entry : incoming.entrySet()) {
                    Socket sock = entry.getKey();
                    ConnectionInfo info = entry.getValue();
                    
                    try {
                        int read = sock.getInputStream().read(buffer);
                        if (read == -1) {
                            if (info.remoteName != null) {
                                callback.disconnect(info.remoteName);
                            }
                            sock.close();
                            incoming.remove(sock);
                            break;
                        } else {
                            info.buffer += new String(buffer, 0, read);
                            while (info.buffer.contains("\n")) {
                                int i = info.buffer.indexOf("\n");
                                String line = info.buffer.substring(0, i);
                                info.buffer = info.buffer.substring(i + 1);
                                if (info.remoteName == null) {
                                    if (line.startsWith("identify ")) {
                                        info.remoteName = callback.checkKey(line.substring(9));
                                    }
                                } else {
                                    callback.process(info.remoteName, line);
                                }
                            }
                        }
                    }
                    catch (SocketTimeoutException ex) {}
                    catch (IOException ex) {
                        try {
                            sock.close();
                        }
                        catch (IOException ex2) {}
                        incoming.remove(sock);
                        System.out.println("[ServerLink] Error reading from " + entry.getValue().remoteName + ": " + ex.getMessage());
                    }
                }
                
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {}
            }
            // System.out.println("Serverthread stopped");
        }
    }
    
    private class ConnectionInfo {
        public String buffer = "";
        public String remoteName = null;
    }
    
}
