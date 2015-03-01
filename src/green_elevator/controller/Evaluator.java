package green_elevator.controller;

import green_elevator.controller.elevator.Elevator;
import green_elevator.controller.elevator.Elevator.Direction;
import green_elevator.controller.message.OutsideMessage;

import java.util.HashSet;
import java.util.Set;
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
	    OutsideMessage message = (OutsideMessage) incommingTasks.getMessage();
	    elevators.get(1).addTask(message);
	}

    }

    private int findOptimalElevator(OutsideMessage message) {
	int[] goalfloors = new int[elevators.size()];
	double[] currentPositions = new double[elevators.size()];
	Direction[] currentOrderedDirections = new Direction[elevators.size()];
	for (Elevator elevator : elevators.values()) {
	    int arrayIndex = elevator.getId() - 1;
	    goalfloors[arrayIndex] = elevator.readGoalFloor();
	    currentPositions[arrayIndex] = elevator.readPosition();
	    currentOrderedDirections[arrayIndex] = elevator.readDirection();
	}
	// sort elevators in three cetegorize
	Set<Integer> elevatorsInRightDirection = new HashSet<Integer>();
	Set<Integer> staticElevators = new HashSet<Integer>();
	Set<Integer> elevatorsInWrongDirection = new HashSet<Integer>();
	int wantedFloor = message.getFloorNumber();

	for (Elevator elevator : elevators.values()) {
	    int id = elevator.getId();
	    Direction currentOrderedDirection = currentOrderedDirections[id - 1];
	    if (currentOrderedDirection == Direction.STATIC) {
		staticElevators.add(id);
	    } else if (currentOrderedDirection == message.getDirection()) {
		if (isInRightDirection(currentPositions[id - 1], wantedFloor, currentOrderedDirection))
		    elevatorsInRightDirection.add(id);
		else
		    elevatorsInWrongDirection.add(id);
	    } else {
		elevatorsInWrongDirection.add(id);
	    }

	}

	return 0;
    }

    private boolean isInRightDirection(double currentPosition, int wantedFloor, Direction currentDirection) {
	if (currentDirection == Direction.UP) {
	    int nextFloor = (int) Math.ceil(currentPosition);
	    if (nextFloor <= wantedFloor)
		return true;
	    else
		return false;
	}
	if (currentDirection == Direction.DOWN) {
	    int nextFloor = (int) Math.floor(currentPosition);
	    if (nextFloor >= wantedFloor)
		return true;
	    else
		return false;
	}
	throw new IllegalStateException();
    }
}
