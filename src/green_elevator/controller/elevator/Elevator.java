package green_elevator.controller.elevator;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Elevator implements Runnable {

    private final int id;
    final Lock positionLock = new ReentrantLock();
    private boolean consumedPosition;
    private AtomicBoolean acceptingNewPositons;
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

    public Elevator(TaskManager taskManager, int id) {
	this.id = id;
	this.taskManager = taskManager;
	consumedPosition = true;
	acceptingNewPositons.set(false);
	direction = Direction.STATIC;
	goalFloor = -1;
    }

    @Override
    public void run() {
	// Task --loop
	// wait for available task (if no task is available change direction to
	// static)
	// Fulfill task
	// moveToGoalFloor() || or stopElevator()
	//

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

    private void moveToGoalFloor() {
	// send command to start elevator
	// loop-for checking position
	// for each place close to floor check shouldStop
	// if stop on goal floor set goal floor to -1 return.
    }

    public void updatePosition(double position) throws InterruptedException {
	if (!acceptingNewPositons.get()) {
	    return;
	}
	positionLock.lock();
	try {
	    // If the elevator has sent a stop command no new position data is
	    // relevant
	    while (!consumedPosition)
		elevatorConsumedPosition.await();
	    if (!acceptingNewPositons.get()) {
		return;
	    }
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

    private void setAcceptingNewPositions(boolean value) {
	if (value == acceptingNewPositons.get()) {
	    return;
	}
	if (value == false) {
	    acceptingNewPositons.set(false);
	    elevatorConsumedPosition.signal();

	}
	if (value == true) {
	    acceptingNewPositons.set(true);
	}
    }
}
