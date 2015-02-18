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
	 * Puts the message into the buffer, if no space is available blocks until there is space available in the buffer.
	 * 
	 * @param message
	 *            the message to add to the buffer
	 * @throws InterruptedException
	 */
	public void putMessage(Message message) throws InterruptedException {
		buffer.put(message);
	}

	/**
	 * Returns the first element in the queue. Blocks until a message is available.
	 * 
	 * @return the first message in the queue
	 * @throws InterruptedException
	 */
	public Message getMessage() throws InterruptedException {
		return buffer.take();
	}
}
