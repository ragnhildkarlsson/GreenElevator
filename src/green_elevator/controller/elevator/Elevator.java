package green_elevator.controller.elevator;

import green_elevator.controller.message.Message;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Elevator implements Runnable {

    private final int id;
    final Lock positionLock = new ReentrantLock();
    private double position;

    private int goalFloor;
    final Lock goalFloorLock = new ReentrantLock();

    private Lock directionLock = new ReentrantLock();
    private Direction direction;

    private TaskManager taskManager;

    private LinkedBlockingQueue<Double> positionBuffer;

    public enum Direction {
	UP, DOWN, STATIC
    }

    public Elevator(TaskManager taskManager, int id) {
	this.id = id;
	this.taskManager = taskManager;
	direction = Direction.STATIC;
	goalFloor = -1;
	positionBuffer = new LinkedBlockingQueue<Double>();
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

    private void moveToGoalFloor() {
	// send command to start elevator
	// loop-for checking position
	// for each place close to floor check shouldStop
	// if stop on goal floor set goal floor to -1 return.
    }

    /**
     * Inform the elevator of its latest calculated position .
     * 
     * @param position
     *            : the latest calculated position for this elevator
     * 
     */
    public void addPositionData(double position) {
	try {
	    positionBuffer.put(position);
	} catch (InterruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    public void addTask(Message message) {
	// TODO implement
    }

    public double readPosition() {
	positionLock.lock();
	try {
	    return position;

	} finally {
	    positionLock.unlock();
	}
    }

    public Direction readDirection() {
	directionLock.lock();
	try {
	    return direction;
	} finally {
	    directionLock.unlock();
	    ;
	}
    }

    public double readGoalFloor() {
	goalFloorLock.lock();
	try {
	    return goalFloor;
	} finally {
	    goalFloorLock.lock();
	}
    }
}
