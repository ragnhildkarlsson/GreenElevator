package green_elevator.controller;

import green_elevator.controller.elevator.Elevator;
import green_elevator.controller.message.Message;

import java.util.concurrent.ConcurrentHashMap;

public class Evaluator implements Runnable {
    private ConcurrentHashMap<Integer, Elevator> elevators;
    private MessageBuffer incommingTasks;

    public Evaluator(ConcurrentHashMap<Integer, Elevator> elevators, MessageBuffer incommingTasks) {
	this.elevators = elevators;
	this.incommingTasks = incommingTasks;
    }

    @Override
    public void run() {
	while (true) {
	    // getMessage is a blocking call so the thread will wait for the
	    // buffer to be none empty.
	    Message message = incommingTasks.getMessage();
	    elevators.get(1).addTask(message);
	}

    }

}
