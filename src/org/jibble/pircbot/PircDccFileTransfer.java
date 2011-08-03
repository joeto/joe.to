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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class is used to administer a DCC file transfer.
 * 
 * @since 1.2.0
 * @author Paul James Mutton, <a
 *         href="http://www.jibble.org/">http://www.jibble.org/</a>
 * @version 1.5.0 (Build time: Mon Dec 14 20:07:17 2009)
 */
public class PircDccFileTransfer {

    /**
     * The default buffer size to use when sending and receiving files.
     */
    public static final int BUFFER_SIZE = 1024;

    /**
     * Constructor used for receiving files.
     */
    PircDccFileTransfer(PircBot bot, PircDccManager manager, String nick, String login, String hostname, String type, String filename, long address, int port, long size) {
        this._bot = bot;
        this._manager = manager;
        this._nick = nick;
        this._login = login;
        this._hostname = hostname;
        this._type = type;
        this._file = new File(filename);
        this._address = address;
        this._port = port;
        this._size = size;
        this._received = false;

        this._incoming = true;
    }

    /**
     * Constructor used for sending files.
     */
    PircDccFileTransfer(PircBot bot, PircDccManager manager, File file, String nick, int timeout) {
        this._bot = bot;
        this._manager = manager;
        this._nick = nick;
        this._file = file;
        this._size = file.length();
        this._timeout = timeout;
        this._received = true;

        this._incoming = false;
    }

    /**
     * Receives a DccFileTransfer and writes it to the specified file. Resuming
     * allows a partial download to be continue from the end of the current file
     * contents.
     * 
     * @param file
     *            The file to write to.
     * @param resume
     *            True if you wish to try and resume the download instead of
     *            overwriting an existing file.
     * 
     */
    public synchronized void receive(File file, boolean resume) {
        if (!this._received) {
            this._received = true;
            this._file = file;

            if (this._type.equals("SEND") && resume) {
                this._progress = file.length();
                if (this._progress == 0) {
                    this.doReceive(file, false);
                } else {
                    this._bot.sendCTCPCommand(this._nick, "DCC RESUME file.ext " + this._port + " " + this._progress);
                    this._manager.addAwaitingResume(this);
                }
            } else {
                this._progress = file.length();
                this.doReceive(file, resume);
            }
        }
    }

    /**
     * Receive the file in a new thread.
     */
    void doReceive(final File file, final boolean resume) {
        new Thread() {
            @Override
            public void run() {

                BufferedOutputStream foutput = null;
                Exception exception = null;

                try {

                    // Convert the integer address to a proper IP address.
                    final int[] ip = PircDccFileTransfer.this._bot.longToIp(PircDccFileTransfer.this._address);
                    final String ipStr = ip[0] + "." + ip[1] + "." + ip[2] + "." + ip[3];

                    // Connect the socket and set a timeout.
                    PircDccFileTransfer.this._socket = new Socket(ipStr, PircDccFileTransfer.this._port);
                    PircDccFileTransfer.this._socket.setSoTimeout(30 * 1000);
                    PircDccFileTransfer.this._startTime = System.currentTimeMillis();

                    // No longer possible to resume this transfer once it's
                    // underway.
                    PircDccFileTransfer.this._manager.removeAwaitingResume(PircDccFileTransfer.this);

                    final BufferedInputStream input = new BufferedInputStream(PircDccFileTransfer.this._socket.getInputStream());
                    final BufferedOutputStream output = new BufferedOutputStream(PircDccFileTransfer.this._socket.getOutputStream());

                    // Following line fixed for jdk 1.1 compatibility.
                    foutput = new BufferedOutputStream(new FileOutputStream(file.getCanonicalPath(), resume));

                    final byte[] inBuffer = new byte[PircDccFileTransfer.BUFFER_SIZE];
                    final byte[] outBuffer = new byte[4];
                    int bytesRead = 0;
                    while ((bytesRead = input.read(inBuffer, 0, inBuffer.length)) != -1) {
                        foutput.write(inBuffer, 0, bytesRead);
                        PircDccFileTransfer.this._progress += bytesRead;
                        // Send back an acknowledgement of how many bytes we
                        // have got so far.
                        outBuffer[0] = (byte) ((PircDccFileTransfer.this._progress >> 24) & 0xff);
                        outBuffer[1] = (byte) ((PircDccFileTransfer.this._progress >> 16) & 0xff);
                        outBuffer[2] = (byte) ((PircDccFileTransfer.this._progress >> 8) & 0xff);
                        outBuffer[3] = (byte) ((PircDccFileTransfer.this._progress >> 0) & 0xff);
                        output.write(outBuffer);
                        output.flush();
                        PircDccFileTransfer.this.delay();
                    }
                    foutput.flush();
                } catch (final Exception e) {
                    exception = e;
                } finally {
                    try {
                        foutput.close();
                        PircDccFileTransfer.this._socket.close();
                    } catch (final Exception anye) {
                        // Do nothing.
                    }
                }

                PircDccFileTransfer.this._bot.onFileTransferFinished(PircDccFileTransfer.this, exception);
            }
        }.start();
    }

    /**
     * Method to send the file inside a new thread.
     */
    void doSend(final boolean allowResume) {
        new Thread() {
            @Override
            public void run() {

                BufferedInputStream finput = null;
                Exception exception = null;

                try {

                    ServerSocket ss = null;

                    final int[] ports = PircDccFileTransfer.this._bot.getDccPorts();
                    if (ports == null) {
                        // Use any free port.
                        ss = new ServerSocket(0);
                    } else {
                        for (final int port : ports) {
                            try {
                                ss = new ServerSocket(port);
                                // Found a port number we could use.
                                break;
                            } catch (final Exception e) {
                                // Do nothing; go round and try another port.
                            }
                        }
                        if (ss == null) {
                            // No ports could be used.
                            throw new IOException("All ports returned by getDccPorts() are in use.");
                        }
                    }

                    ss.setSoTimeout(PircDccFileTransfer.this._timeout);
                    PircDccFileTransfer.this._port = ss.getLocalPort();
                    InetAddress inetAddress = PircDccFileTransfer.this._bot.getDccInetAddress();
                    if (inetAddress == null) {
                        inetAddress = PircDccFileTransfer.this._bot.getInetAddress();
                    }
                    final byte[] ip = inetAddress.getAddress();
                    final long ipNum = PircDccFileTransfer.this._bot.ipToLong(ip);

                    // Rename the filename so it has no whitespace in it when we
                    // send it.
                    // .... I really should do this a bit more nicely at some
                    // point ....
                    String safeFilename = PircDccFileTransfer.this._file.getName().replace(' ', '_');
                    safeFilename = safeFilename.replace('\t', '_');

                    if (allowResume) {
                        PircDccFileTransfer.this._manager.addAwaitingResume(PircDccFileTransfer.this);
                    }

                    // Send the message to the user, telling them where to
                    // connect to in order to get the file.
                    PircDccFileTransfer.this._bot.sendCTCPCommand(PircDccFileTransfer.this._nick, "DCC SEND " + safeFilename + " " + ipNum + " " + PircDccFileTransfer.this._port + " " + PircDccFileTransfer.this._file.length());

                    // The client may now connect to us and download the file.
                    PircDccFileTransfer.this._socket = ss.accept();
                    PircDccFileTransfer.this._socket.setSoTimeout(30000);
                    PircDccFileTransfer.this._startTime = System.currentTimeMillis();

                    // No longer possible to resume this transfer once it's
                    // underway.
                    if (allowResume) {
                        PircDccFileTransfer.this._manager.removeAwaitingResume(PircDccFileTransfer.this);
                    }

                    // Might as well close the server socket now; it's finished
                    // with.
                    ss.close();

                    final BufferedOutputStream output = new BufferedOutputStream(PircDccFileTransfer.this._socket.getOutputStream());
                    final BufferedInputStream input = new BufferedInputStream(PircDccFileTransfer.this._socket.getInputStream());
                    finput = new BufferedInputStream(new FileInputStream(PircDccFileTransfer.this._file));

                    // Check for resuming.
                    if (PircDccFileTransfer.this._progress > 0) {
                        long bytesSkipped = 0;
                        while (bytesSkipped < PircDccFileTransfer.this._progress) {
                            bytesSkipped += finput.skip(PircDccFileTransfer.this._progress - bytesSkipped);
                        }
                    }

                    final byte[] outBuffer = new byte[PircDccFileTransfer.BUFFER_SIZE];
                    final byte[] inBuffer = new byte[4];
                    int bytesRead = 0;
                    while ((bytesRead = finput.read(outBuffer, 0, outBuffer.length)) != -1) {
                        output.write(outBuffer, 0, bytesRead);
                        output.flush();
                        input.read(inBuffer, 0, inBuffer.length);
                        PircDccFileTransfer.this._progress += bytesRead;
                        PircDccFileTransfer.this.delay();
                    }
                } catch (final Exception e) {
                    exception = e;
                } finally {
                    try {
                        finput.close();
                        PircDccFileTransfer.this._socket.close();
                    } catch (final Exception e) {
                        // Do nothing.
                    }
                }

                PircDccFileTransfer.this._bot.onFileTransferFinished(PircDccFileTransfer.this, exception);
            }
        }.start();
    }

    /**
     * Package mutator for setting the progress of the file transfer.
     */
    void setProgress(long progress) {
        this._progress = progress;
    }

    /**
     * Delay between packets.
     */
    private void delay() {
        if (this._packetDelay > 0) {
            try {
                Thread.sleep(this._packetDelay);
            } catch (final InterruptedException e) {
                // Do nothing.
            }
        }
    }

    /**
     * Returns the nick of the other user taking part in this file transfer.
     * 
     * @return the nick of the other user.
     * 
     */
    public String getNick() {
        return this._nick;
    }

    /**
     * Returns the login of the file sender.
     * 
     * @return the login of the file sender. null if we are sending.
     * 
     */
    public String getLogin() {
        return this._login;
    }

    /**
     * Returns the hostname of the file sender.
     * 
     * @return the hostname of the file sender. null if we are sending.
     * 
     */
    public String getHostname() {
        return this._hostname;
    }

    /**
     * Returns the suggested file to be used for this transfer.
     * 
     * @return the suggested file to be used.
     * 
     */
    public File getFile() {
        return this._file;
    }

    /**
     * Returns the port number to be used when making the connection.
     * 
     * @return the port number.
     * 
     */
    public int getPort() {
        return this._port;
    }

    /**
     * Returns true if the file transfer is incoming (somebody is sending the
     * file to us).
     * 
     * @return true if the file transfer is incoming.
     * 
     */
    public boolean isIncoming() {
        return this._incoming;
    }

    /**
     * Returns true if the file transfer is outgoing (we are sending the file to
     * someone).
     * 
     * @return true if the file transfer is outgoing.
     * 
     */
    public boolean isOutgoing() {
        return !this.isIncoming();
    }

    /**
     * Sets the delay time between sending or receiving each packet. Default is
     * 0. This is useful for throttling the speed of file transfers to maintain
     * a good quality of service for other things on the machine or network.
     * 
     * @param millis
     *            The number of milliseconds to wait between packets.
     * 
     */
    public void setPacketDelay(long millis) {
        this._packetDelay = millis;
    }

    /**
     * returns the delay time between each packet that is send or received.
     * 
     * @return the delay between each packet.
     * 
     */
    public long getPacketDelay() {
        return this._packetDelay;
    }

    /**
     * Returns the size (in bytes) of the file being transfered.
     * 
     * @return the size of the file. Returns -1 if the sender did not specify
     *         this value.
     */
    public long getSize() {
        return this._size;
    }

    /**
     * Returns the progress (in bytes) of the current file transfer. When
     * resuming, this represents the total number of bytes in the file, which
     * may be greater than the amount of bytes resumed in just this transfer.
     * 
     * @return the progress of the transfer.
     */
    public long getProgress() {
        return this._progress;
    }

    /**
     * Returns the progress of the file transfer as a percentage. Note that this
     * should never be negative, but could become greater than 100% if you
     * attempt to resume a larger file onto a partially downloaded file that was
     * smaller.
     * 
     * @return the progress of the transfer as a percentage.
     */
    public double getProgressPercentage() {
        return 100 * (this.getProgress() / (double) this.getSize());
    }

    /**
     * Stops the DCC file transfer by closing the connection.
     */
    public void close() {
        try {
            this._socket.close();
        } catch (final Exception e) {
            // Let the DCC manager worry about anything that may go wrong.
        }
    }

    /**
     * Returns the rate of data transfer in bytes per second. This value is an
     * estimate based on the number of bytes transfered since the connection was
     * established.
     * 
     * @return data transfer rate in bytes per second.
     */
    public long getTransferRate() {
        final long time = (System.currentTimeMillis() - this._startTime) / 1000;
        if (time <= 0) {
            return 0;
        }
        return this.getProgress() / time;
    }

    /**
     * Returns the address of the sender as a long.
     * 
     * @return the address of the sender as a long.
     */
    public long getNumericalAddress() {
        return this._address;
    }

    private final PircBot _bot;
    private final PircDccManager _manager;
    private final String _nick;
    private String _login = null;
    private String _hostname = null;
    private String _type;
    private long _address;
    private int _port;
    private final long _size;
    private boolean _received;

    private Socket _socket = null;
    private long _progress = 0;
    private File _file = null;
    private int _timeout = 0;
    private final boolean _incoming;
    private long _packetDelay = 0;

    private long _startTime = 0;

}
