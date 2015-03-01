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
			int bestElevatorId = findOptimalElevator(message);
			elevators.get(bestElevatorId).addTask(message);
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
		// sort elevators in three categories
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
				if (isInRightDirection(currentPositions[id - 1], wantedFloor, currentOrderedDirection,
						goalfloors[id - 1]))
					elevatorsInRightDirection.add(id);
				else
					elevatorsInWrongDirection.add(id);
			} else {
				elevatorsInWrongDirection.add(id);
			}
		}
		// Try to first take an elevator from the group which is in the right direction with the shortest path to the
		// wanted floor
		if (!elevatorsInRightDirection.isEmpty())
			return elevatorWithShortestPath(elevatorsInRightDirection, currentPositions, wantedFloor);

		// Else we try to get a static elevator with the shortest path to the wanted floor
		if (!staticElevators.isEmpty()) {
			return elevatorWithShortestPath(staticElevators, currentPositions, wantedFloor);
		}
		// If we have no other option we take the elevator with the current least tasks
		return elevatorWithLeastTasks(elevatorsInWrongDirection);
	}

	private int elevatorWithLeastTasks(Set<Integer> elevatorsInWrongDirection) {
		int minTaskAmount = Integer.MAX_VALUE;
		int bestElevatorId = 0;
		for (int id : elevatorsInWrongDirection) {
			int nTasks = elevators.get(id).numberOfTasks();
			if (nTasks < minTaskAmount) {
				minTaskAmount = nTasks;
				bestElevatorId = id;
			}
		}
		return bestElevatorId;
	}

	private int elevatorWithShortestPath(Set<Integer> elevators, double[] currentPositions, int wantedFloor) {

		double shortestDistance = Double.MAX_VALUE;
		int bestElevatorId = 0;
		for (int id : elevators) {
			double distance = Math.abs(currentPositions[id - 1] - wantedFloor);
			if (distance < shortestDistance) {
				shortestDistance = distance;
				bestElevatorId = id;
			}

		}
		return bestElevatorId;
	}

	private boolean isInRightDirection(double currentPosition, int wantedFloor, Direction currentDirection,
			int currentGoalFloor) {
		if (currentDirection == Direction.UP) {
			int nextFloor = (int) Math.ceil(currentPosition);
			if (nextFloor <= wantedFloor || currentGoalFloor <= wantedFloor)
				return true;
			else
				return false;
		}
		if (currentDirection == Direction.DOWN) {
			int nextFloor = (int) Math.floor(currentPosition);
			if (nextFloor >= wantedFloor || currentGoalFloor >= wantedFloor)
				return true;
			else
				return false;
		}
		throw new IllegalStateException();
	}
}
