package green_elevator.controller;

import green_elevator.controller.message.Message;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageBuffer {

    private BlockingQueue<Message> buffer;
    private static final boolean debug = true;

    public MessageBuffer() {
	this.buffer = new LinkedBlockingQueue<Message>();
    }

    /**
     * Puts the message into the buffer, if no space is available blocks until
     * there is space available in the buffer.
     * 
     * @param message
     *            the message to add to the buffer
     */
    public void putMessage(Message message) {
	try {
	    buffer.put(message);
	} catch (InterruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    /**
     * Returns the first element in the queue. Blocks until a message is
     * available.
     * 
     * @return the first message in the queue
     */
    public Message getMessage() {
	try {
	    return buffer.take();
	} catch (InterruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	throw new IllegalStateException();
    }
}
