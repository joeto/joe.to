/* 
Copyright Paul James Mutton, 2001-2009, http://www.jibble.org/

This file is part of PircBot.

This software is dual-licensed, allowing you to choose between the GNU
General Public License (GPL) and the www.jibble.org Commercial License.
Since the GPL may be too restrictive for use in a proprietary application,
a commercial license is also provided. Full license information can be
found at http://www.jibble.org/licenses/

 */

package org.jibble.pircbot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 * A Thread which reads lines from the IRC server. It then passes these lines to
 * the PircBot without changing them. This running Thread also detects
 * disconnection from the server and is thus used by the OutputThread to send
 * lines to the server.
 * 
 * @author Paul James Mutton, <a
 *         href="http://www.jibble.org/">http://www.jibble.org/</a>
 * @version 1.5.0 (Build time: Mon Dec 14 20:07:17 2009)
 */
public class PircInputThread extends Thread {

    /**
     * The InputThread reads lines from the IRC server and allows the PircBot to
     * handle them.
     * 
     * @param bot
     *            An instance of the underlying PircBot.
     * @param breader
     *            The BufferedReader that reads lines from the server.
     * @param bwriter
     *            The BufferedWriter that sends lines to the server.
     */
    PircInputThread(PircBot bot, Socket socket, BufferedReader breader, BufferedWriter bwriter) {
        this._bot = bot;
        this._socket = socket;
        this._breader = breader;
        this._bwriter = bwriter;
        this.setName(this.getClass() + "-Thread");
    }

    /**
     * Sends a raw line to the IRC server as soon as possible, bypassing the
     * outgoing message queue.
     * 
     * @param line
     *            The raw line to send to the IRC server.
     */
    void sendRawLine(String line) {
        PircOutputThread.sendRawLine(this._bot, this._bwriter, line);
    }

    /**
     * Returns true if this InputThread is connected to an IRC server. The
     * result of this method should only act as a rough guide, as the result may
     * not be valid by the time you act upon it.
     * 
     * @return True if still connected.
     */
    boolean isConnected() {
        return this._isConnected;
    }

    /**
     * Called to start this Thread reading lines from the IRC server. When a
     * line is read, this method calls the handleLine method in the PircBot,
     * which may subsequently call an 'onXxx' method in the PircBot subclass. If
     * any subclass of Throwable (i.e. any Exception or Error) is thrown by your
     * method, then this method will print the stack trace to the standard
     * output. It is probable that the PircBot may still be functioning normally
     * after such a problem, but the existance of any uncaught exceptions in
     * your code is something you should really fix.
     */
    @Override
    public void run() {
        try {
            boolean running = true;
            while (running) {
                try {
                    String line = null;
                    while ((line = this._breader.readLine()) != null) {
                        try {
                            this._bot.handleLine(line);
                        } catch (final Throwable t) {
                            // Stick the whole stack trace into a String so we
                            // can output it nicely.
                            final StringWriter sw = new StringWriter();
                            final PrintWriter pw = new PrintWriter(sw);
                            t.printStackTrace(pw);
                            pw.flush();
                            final StringTokenizer tokenizer = new StringTokenizer(sw.toString(), "\r\n");
                            synchronized (this._bot) {
                                this._bot.log("### Your implementation of PircBot is faulty and you have");
                                this._bot.log("### allowed an uncaught Exception or Error to propagate in your");
                                this._bot.log("### code. It may be possible for PircBot to continue operating");
                                this._bot.log("### normally. Here is the stack trace that was produced: -");
                                this._bot.log("### ");
                                while (tokenizer.hasMoreTokens()) {
                                    this._bot.log("### " + tokenizer.nextToken());
                                }
                            }
                        }
                    }
                    if (line == null) {
                        // The server must have disconnected us.
                        running = false;
                    }
                } catch (final InterruptedIOException iioe) {
                    // This will happen if we haven't received anything from the
                    // server for a while.
                    // So we shall send it a ping to check that we are still
                    // connected.
                    this.sendRawLine("PING " + (System.currentTimeMillis() / 1000));
                    // Now we go back to listening for stuff from the server...
                }
            }
        } catch (final Exception e) {
            // Do nothing.
        }

        // If we reach this point, then we must have disconnected.
        try {
            this._socket.close();
        } catch (final Exception e) {
            // Just assume the socket was already closed.
        }

        if (!this._disposed) {
            this._bot.log("*** Disconnected.");
            this._isConnected = false;
            this._bot.onDisconnect();
        }

    }

    /**
     * Closes the socket without onDisconnect being called subsequently.
     */
    public void dispose() {
        try {
            this._disposed = true;
            this._socket.close();
        } catch (final Exception e) {
            // Do nothing.
        }
    }

    private PircBot _bot = null;
    private Socket _socket = null;
    private BufferedReader _breader = null;
    private BufferedWriter _bwriter = null;
    private boolean _isConnected = true;
    private boolean _disposed = false;

    public static final int MAX_LINE_LENGTH = 512;

}
