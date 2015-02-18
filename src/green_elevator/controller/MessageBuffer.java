package green_elevator.controller;

import green_elevator.controller.message.Message;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageBuffer {

    private BlockingQueue<Message> buffer;

    public MessageBuffer() {
	this.buffer = new LinkedBlockingQueue<Message>();
    }

    /**
     * Puts the message into the buffer, if no space is available until it is
     * space available in the buffer
     * 
     * @param message
     * @throws InterruptedException
     */
    public void putMessage(Message message) throws InterruptedException {
	buffer.put(message);
    }

    /**
     * Return the first element in the queue. Blocks until a message is
     * available.
     * 
     * @param message
     * @return
     * @throws InterruptedException
     */
    public Message getMessage(Message message) throws InterruptedException {
	return buffer.take();
    }
}
