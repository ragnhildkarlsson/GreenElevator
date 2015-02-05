package green_elevator.controller;

import java.io.BufferedReader;

public class IndataReciever implements Runnable {

    private final BufferedReader reader;
    private final MessageBuffer outBuffer;

    public IndataReciever(BufferedReader reader, MessageBuffer outBuffer) {
	this.reader = reader;
	this.outBuffer = outBuffer;
    }

    /**
     * Read incoming messages and put them into the given buffer
     * 
     */
    @Override
    public void run() {

    }

}
