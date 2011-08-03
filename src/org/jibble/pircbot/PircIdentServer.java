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
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A simple IdentServer (also know as "The Identification Protocol"). An ident
 * server provides a means to determine the identity of a user of a particular
 * TCP connection.
 * <p>
 * Most IRC servers attempt to contact the ident server on connecting hosts in
 * order to determine the user's identity. A few IRC servers will not allow you
 * to connect unless this information is provided.
 * <p>
 * So when a PircBot is run on a machine that does not run an ident server, it
 * may be necessary to provide a "faked" response by starting up its own ident
 * server and sending out apparently correct responses.
 * <p>
 * An instance of this class can be used to start up an ident server only if it
 * is possible to do so. Reasons for not being able to do so are if there is
 * already an ident server running on port 113, or if you are running as an
 * unprivileged user who is unable to create a server socket on that port
 * number.
 * 
 * @since 0.9c
 * @author Paul James Mutton, <a
 *         href="http://www.jibble.org/">http://www.jibble.org/</a>
 * @version 1.5.0 (Build time: Mon Dec 14 20:07:17 2009)
 */
public class PircIdentServer extends Thread {

    /**
     * Constructs and starts an instance of an IdentServer that will respond to
     * a client with the provided login. Rather than calling this constructor
     * explicitly from your code, it is recommended that you use the
     * startIdentServer method in the PircBot class.
     * <p>
     * The ident server will wait for up to 60 seconds before shutting down.
     * Otherwise, it will shut down as soon as it has responded to an ident
     * request.
     * 
     * @param bot
     *            The PircBot instance that will be used to log to.
     * @param login
     *            The login that the ident server will respond with.
     */
    PircIdentServer(PircBot bot, String login) {
        this._bot = bot;
        this._login = login;

        try {
            this._ss = new ServerSocket(113);
            this._ss.setSoTimeout(60000);
        } catch (final Exception e) {
            this._bot.log("*** Could not start the ident server on port 113.");
            return;
        }

        this._bot.log("*** Ident server running on port 113 for the next 60 seconds...");
        this.setName(this.getClass() + "-Thread");
        this.start();
    }

    /**
     * Waits for a client to connect to the ident server before making an
     * appropriate response. Note that this method is started by the class
     * constructor.
     */
    @Override
    public void run() {
        try {
            final Socket socket = this._ss.accept();
            socket.setSoTimeout(60000);

            final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String line = reader.readLine();
            if (line != null) {
                this._bot.log("*** Ident request received: " + line);
                line = line + " : USERID : UNIX : " + this._login;
                writer.write(line + "\r\n");
                writer.flush();
                this._bot.log("*** Ident reply sent: " + line);
                writer.close();
            }
        } catch (final Exception e) {
            // We're not really concerned with what went wrong, are we?
        }

        try {
            this._ss.close();
        } catch (final Exception e) {
            // Doesn't really matter...
        }

        this._bot.log("*** The Ident server has been shut down.");
    }

    private PircBot _bot;
    private String _login;
    private ServerSocket _ss = null;

}
