package green_elevator.controller.elevator;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Elevator implements Runnable {

    final Lock positionLock = new ReentrantLock();
    private boolean consumedPosition;
    private boolean acceptingNewPositons;
    final Condition elevatorConsumedPosition = positionLock.newCondition();
    final Condition newPositionAvailable = positionLock.newCondition();
    private double position;

    private int goalFloor;

    private Lock directionLock = new ReentrantLock();
    private Direction direction;

    private TaskManager taskManager;

    public enum Direction {
	UP, DOWN, STATIC
    }

    public Elevator(TaskManager taskManager) {
	this.taskManager = taskManager;
	consumedPosition = true;
	acceptingNewPositons = false;
	direction = Direction.STATIC;
	goalFloor = -1;
    }

    @Override
    public void run() {
	// TODO Auto-generated method stub

    }

    private double consumePosition() throws InterruptedException {
	positionLock.lock();
	try {
	    while (consumedPosition)
		newPositionAvailable.await();
	    consumedPosition = true;
	    elevatorConsumedPosition.signalAll();
	    return position;
	} finally {
	    positionLock.unlock();
	}
    }

    public void updatePosition(double position) throws InterruptedException {
	positionLock.lock();
	try {
	    // If the elevator has sent a stop command no new position data is
	    // relevant
	    if (!acceptingNewPositons) {
		return;
	    }
	    while (!consumedPosition)
		elevatorConsumedPosition.await();
	    consumedPosition = false;
	    this.position = position;
	    newPositionAvailable.signalAll();
	} finally {
	    positionLock.unlock();
	}
    }

    public double readPosition() {
	positionLock.lock();
	try {
	    return position;

	} finally {
	    positionLock.unlock();
	}
    }

}
