package to.joe.manager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import to.joe.J2;

/**
 * Creates and maintains webpages with info.
 * 
 */
public class WebPage {
    private final J2 j2;
    private int servernum;

    public WebPage(J2 j2) {
        this.j2 = j2;
    }

    /**
     * Start webpaging!
     * 
     * @param servnum
     *            Server number
     */
    public void go(int servnum) {
        this.startUpdateTimer1();
        this.servernum = servnum;
        // startUpdateTimer2();
    }

    private boolean stop;
    public boolean restart = false;

    private void startUpdateTimer1() {
        this.stop = false;
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (WebPage.this.stop) {
                    timer.cancel();
                    return;
                }
                WebPage.this.update5Second();
            }
        }, 1000, 5000);
    }

    /*
     * public void startUpdateTimer2() { stop = false; final Timer timer = new
     * Timer(); timer.schedule(new TimerTask() {
     * 
     * @Override public void run() { if (stop) { timer.cancel(); return; }
     * update20(); } }, 1000, 5000); }
     */
    private void update5Second() {
        final ArrayList<Integer> watched = new ArrayList<Integer>(this.j2.watchlist);
        final HashMap<Integer, String> output = new HashMap<Integer, String>();
        for (final Integer i : watched) {
            output.put(i, "");
        }
        String playerlist = "";
        int playercount = 0;
        String aliaslist = "";
        for (final Player p : this.j2.getServer().getOnlinePlayers()) {
            if (p != null) {
                playercount++;
                playerlist += " " + p.getDisplayName();
                final Inventory check = p.getInventory();
                final String name = p.getName();
                for (final Integer i : watched) {
                    if (check.contains(i, 10)) {
                        String temp = new String(output.get(i));
                        output.remove(i);
                        temp += " " + name;
                        output.put(i, temp);
                    }
                }
                final String known = this.j2.ip.getKnown(p.getName());
                if (!known.equals("")) {
                    aliaslist += known + "<br>";
                }
            }
        }
        try {
            final BufferedWriter out = new BufferedWriter(new FileWriter("/home/minecraft/public_html/detector/" + this.servernum + "/current.txt"));
            out.write("Players(" + playercount + "/" + this.j2.playerLimit + "):" + playerlist + "<br /><br /><br />");
            out.write("<table>");
            for (final Integer i : watched) {
                out.write("<tr><td>" + Material.getMaterial(i) + "</td><td>" + output.get(i) + "</td></tr>");
            }
            out.write("</table><br />");
            if (!aliaslist.equals("")) {
                out.write("If a username below is RED, then that user has been banned</br>" + "Otherwise, they are not necessarily a threat.<br>" + "Just watch if they're RED, really :)");
                out.write("<table>");
                out.write(aliaslist);
                out.write("</table>");
            }
            out.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }

        try {
            final BufferedWriter out = new BufferedWriter(new FileWriter("/home/minecraft/public_html/" + this.servernum + "/current.txt"));
            out.write("Players(" + playercount + "/" + this.j2.playerLimit + "):" + playerlist + "<br />");
            out.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        try {
            final BufferedWriter out = new BufferedWriter(new FileWriter("/home/minecraft/public_html/" + this.servernum + "/players.txt"));
            out.write(playercount + "/" + this.j2.playerLimit);
            out.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
