package green_elevator.controller;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Optional;

public class OutdataSender implements Runnable {

    private MessageBuffer buffer;
    private PrintWriter writer;
    private MesssageTranslator translator;
    private final boolean debug = true;

    public OutdataSender(OutputStream output, MessageBuffer buffer, MesssageTranslator translator) {

	this.buffer = buffer;
	this.writer = new PrintWriter(output, true);
	this.translator = translator;

    }

    /**
     * Start sending messages from the message buffer to the output stream, one
     * at a time.
     * 
     */
    @Override
    public void run() {
	while (true) {

	    // getMessage is a blocking call so the thread will wait for the
	    // buffer to be none empty.
	    Optional<String> message = translator.getMessageString(buffer.getMessage());
	    if (message.isPresent()) {
		if (debug)
		    System.out.println("Outdatasender sends " + message.get());
		writer.println(message.get());
		writer.flush();
	    }

	}

    }
}
