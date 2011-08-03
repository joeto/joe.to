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

import java.util.Vector;

/**
 * Queue is a definition of a data structure that may act as a queue - that is,
 * data can be added to one end of the queue and data can be requested from the
 * head end of the queue. This class is thread safe for multiple producers and a
 * single consumer. The next() method will block until there is data in the
 * queue.
 * 
 * This has now been modified so that it is compatible with the earlier JDK1.1
 * in order to be suitable for running on mobile appliances. This means
 * replacing the LinkedList with a Vector, which is hardly ideal, but this Queue
 * is typically only polled every second before dispatching messages.
 * 
 * @author Paul James Mutton, <a
 *         href="http://www.jibble.org/">http://www.jibble.org/</a>
 * @version 1.5.0 (Build time: Mon Dec 14 20:07:17 2009)
 */
public class PircQueue {

    /**
     * Constructs a Queue object of unlimited size.
     */
    public PircQueue() {

    }

    /**
     * Adds an Object to the end of the Queue.
     * 
     * @param o
     *            The Object to be added to the Queue.
     */
    @SuppressWarnings("unchecked")
    public void add(Object o) {
        synchronized (this._queue) {
            this._queue.addElement(o);
            this._queue.notify();
        }
    }

    /**
     * Adds an Object to the front of the Queue.
     * 
     * @param o
     *            The Object to be added to the Queue.
     */
    @SuppressWarnings("unchecked")
    public void addFront(Object o) {
        synchronized (this._queue) {
            this._queue.insertElementAt(o, 0);
            this._queue.notify();
        }
    }

    /**
     * Returns the Object at the front of the Queue. This Object is then removed
     * from the Queue. If the Queue is empty, then this method shall block until
     * there is an Object in the Queue to return.
     * 
     * @return The next item from the front of the queue.
     */
    public Object next() {

        Object o = null;

        // Block if the Queue is empty.
        synchronized (this._queue) {
            if (this._queue.size() == 0) {
                try {
                    this._queue.wait();
                } catch (final InterruptedException e) {
                    return null;
                }
            }

            // Return the Object.
            try {
                o = this._queue.firstElement();
                this._queue.removeElementAt(0);
            } catch (final ArrayIndexOutOfBoundsException e) {
                throw new InternalError("Race hazard in Queue object.");
            }
        }

        return o;
    }

    /**
     * Returns true if the Queue is not empty. If another Thread empties the
     * Queue before <b>next()</b> is called, then the call to <b>next()</b>
     * shall block until the Queue has been populated again.
     * 
     * @return True only if the Queue not empty.
     */
    public boolean hasNext() {
        return (this.size() != 0);
    }

    /**
     * Clears the contents of the Queue.
     */
    public void clear() {
        synchronized (this._queue) {
            this._queue.removeAllElements();
        }
    }

    /**
     * Returns the size of the Queue.
     * 
     * @return The current size of the queue.
     */
    public int size() {
        return this._queue.size();
    }

    @SuppressWarnings("rawtypes")
    private final Vector _queue = new Vector();

}
