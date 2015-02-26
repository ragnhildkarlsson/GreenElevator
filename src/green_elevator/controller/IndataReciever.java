package green_elevator.controller;

import green_elevator.controller.message.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;

public class IndataReciever implements Runnable {

    private final BufferedReader reader;
    private final MessageBuffer outBuffer;
    private final MesssageTranslator messsageTranslator;
    private final static boolean debug = false;

    public IndataReciever(BufferedReader reader, MessageBuffer outBuffer, MesssageTranslator messsageTranslator) {
	this.reader = reader;
	this.outBuffer = outBuffer;
	this.messsageTranslator = messsageTranslator;
    }

    /**
     * Read incoming messages and put them into the given buffer
     * 
     */
    @Override
    public void run() {
	String input = "";
	try {
	    while ((input = reader.readLine()) != null) {
		if (debug)
		    System.out.println("Indatareciever: Recieved input " + input);
		Optional<Message> message = messsageTranslator.getMessage(input);
		if (message.isPresent()) {
		    if (debug)
			System.out.println(input + " created a messsage");
		    outBuffer.putMessage(message.get());
		}
	    }
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }
}
