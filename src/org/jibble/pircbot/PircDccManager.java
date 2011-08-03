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

import java.util.StringTokenizer;
import java.util.Vector;

/**
 * This class is used to process DCC events from the server.
 * 
 * @since 1.2.0
 * @author Paul James Mutton, <a
 *         href="http://www.jibble.org/">http://www.jibble.org/</a>
 * @version 1.5.0 (Build time: Mon Dec 14 20:07:17 2009)
 */
public class PircDccManager {

    /**
     * Constructs a DccManager to look after all DCC SEND and CHAT events.
     * 
     * @param bot
     *            The PircBot whose DCC events this class will handle.
     */
    PircDccManager(PircBot bot) {
        this._bot = bot;
    }

    /**
     * Processes a DCC request.
     * 
     * @return True if the type of request was handled successfully.
     */
    boolean processRequest(String nick, String login, String hostname, String request) {
        final StringTokenizer tokenizer = new StringTokenizer(request);
        tokenizer.nextToken();
        final String type = tokenizer.nextToken();
        final String filename = tokenizer.nextToken();

        if (type.equals("SEND")) {
            final long address = Long.parseLong(tokenizer.nextToken());
            final int port = Integer.parseInt(tokenizer.nextToken());
            long size = -1;
            try {
                size = Long.parseLong(tokenizer.nextToken());
            } catch (final Exception e) {
                // Stick with the old value.
            }

            final PircDccFileTransfer transfer = new PircDccFileTransfer(this._bot, this, nick, login, hostname, type, filename, address, port, size);
            this._bot.onIncomingFileTransfer(transfer);

        } else if (type.equals("RESUME")) {
            final int port = Integer.parseInt(tokenizer.nextToken());
            final long progress = Long.parseLong(tokenizer.nextToken());

            PircDccFileTransfer transfer = null;
            synchronized (this._awaitingResume) {
                for (int i = 0; i < this._awaitingResume.size(); i++) {
                    transfer = (PircDccFileTransfer) this._awaitingResume.elementAt(i);
                    if (transfer.getNick().equals(nick) && (transfer.getPort() == port)) {
                        this._awaitingResume.removeElementAt(i);
                        break;
                    }
                }
            }

            if (transfer != null) {
                transfer.setProgress(progress);
                this._bot.sendCTCPCommand(nick, "DCC ACCEPT file.ext " + port + " " + progress);
            }

        } else if (type.equals("ACCEPT")) {
            final int port = Integer.parseInt(tokenizer.nextToken());
            @SuppressWarnings("unused")
            final
            long progress = Long.parseLong(tokenizer.nextToken());

            PircDccFileTransfer transfer = null;
            synchronized (this._awaitingResume) {
                for (int i = 0; i < this._awaitingResume.size(); i++) {
                    transfer = (PircDccFileTransfer) this._awaitingResume.elementAt(i);
                    if (transfer.getNick().equals(nick) && (transfer.getPort() == port)) {
                        this._awaitingResume.removeElementAt(i);
                        break;
                    }
                }
            }

            if (transfer != null) {
                transfer.doReceive(transfer.getFile(), true);
            }

        } else if (type.equals("CHAT")) {
            final long address = Long.parseLong(tokenizer.nextToken());
            final int port = Integer.parseInt(tokenizer.nextToken());

            final PircDccChat chat = new PircDccChat(this._bot, nick, login, hostname, address, port);

            new Thread() {
                @Override
                public void run() {
                    PircDccManager.this._bot.onIncomingChatRequest(chat);
                }
            }.start();
        } else {
            return false;
        }

        return true;
    }

    /**
     * Add this DccFileTransfer to the list of those awaiting possible resuming.
     * 
     * @param transfer
     *            the DccFileTransfer that may be resumed.
     */
    @SuppressWarnings("unchecked")
    void addAwaitingResume(PircDccFileTransfer transfer) {
        synchronized (this._awaitingResume) {
            this._awaitingResume.addElement(transfer);
        }
    }

    /**
     * Remove this transfer from the list of those awaiting resuming.
     */
    void removeAwaitingResume(PircDccFileTransfer transfer) {
        this._awaitingResume.removeElement(transfer);
    }

    private final PircBot _bot;
    @SuppressWarnings("rawtypes")
    private final Vector _awaitingResume = new Vector();

}
