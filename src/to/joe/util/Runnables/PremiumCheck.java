package to.joe.util.Runnables;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.bukkit.ChatColor;

import to.joe.J2;

public class PremiumCheck implements Runnable {

    private final String username;
    private final J2 j2;

    public PremiumCheck(String username, J2 j2) {
        this.username = username;
        this.j2 = j2;
    }

    @Override
    public void run() {
        try {
            final URL url = new URL("http://www.minecraft.net/haspaid.jsp?user=" + this.username);
            final URLConnection connection = url.openConnection();
            connection.setDoOutput(true);
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(15000);
            connection.setRequestProperty("User-agent", "meow");
            // OutputStreamWriter writer=new
            // OutputStreamWriter(connection.getOutputStream());
            // writer.write(POSTstring);
            // writer.flush();
            final StringBuilder stringBuilder = new StringBuilder();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            // writer.close();
            reader.close();
            accountStatus status = accountStatus.WTF;
            final String result = stringBuilder.toString();
            if (result.contains("true")) {
                status = accountStatus.PREMIUM;
            }
            if (result.contains("false")) {
                if (status.equals(accountStatus.PREMIUM)) {
                    status = accountStatus.WTF;
                } else {
                    status = accountStatus.NONPREMIUM;
                }
            }
            String message;
            switch (status) {
                case PREMIUM:
                    message = ChatColor.DARK_AQUA + this.username + ChatColor.AQUA + " is premium";
                    break;
                case NONPREMIUM:
                    message = ChatColor.DARK_AQUA + this.username + ChatColor.AQUA + " is not premium. Just letting you know.";
                    break;
                default:
                    message = ChatColor.AQUA + "Could not identify ownership of " + ChatColor.DARK_AQUA + this.username;
                    break;
            }
            if (status.equals(accountStatus.NONPREMIUM)) {
                this.j2.sendAdminPlusLog(message);
                this.j2.irc.messageAdmins(message);
            } else {
                //this.j2.log(message);
            }
        } catch (final Exception e) {

        }
    }

    private enum accountStatus {
        PREMIUM, NONPREMIUM, WTF
    }

}
