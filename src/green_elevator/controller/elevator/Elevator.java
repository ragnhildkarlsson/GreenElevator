package green_elevator.controller.elevator;

import green_elevator.controller.elevator.task.Task;
import green_elevator.controller.elevator.task.Task.TaskType;
import green_elevator.controller.message.Message;
import green_elevator.controller.message.Message.MessageType;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Elevator implements Runnable {

    private final int id;
    final Lock positionLock = new ReentrantLock();
    private double position;

    private int goalFloor;
    final Lock goalFloorLock = new ReentrantLock();

    private Lock directionLock = new ReentrantLock();
    private Direction orderedDirection;

    private TaskManager taskManager;

    private LinkedBlockingQueue<Double> positionBuffer;

    private AtomicBoolean outOfOrder;

    public enum Direction {
	UP, DOWN, STATIC
    }

    public Elevator(TaskManager taskManager, int id) {
	this.id = id;
	this.taskManager = taskManager;
	orderedDirection = Direction.UP;
	goalFloor = 0;
	positionBuffer = new LinkedBlockingQueue<Double>();
	position = 0;
	outOfOrder = new AtomicBoolean(false);
    }

    @Override
    public void run() {
	while (true) {
	    double currentPosition = readPosition();
	    Task task = taskManager.getTask(currentPosition);
	    if (task.getTaskType() == TaskType.OUTSIDETASK) {
		updateDirection(task.getDirection().get());
		updateGoalFloor(task.getGoalFloor());
	    }
	    if (task.getTaskType() == TaskType.INSIDETASK) {
		updateGoalFloor(readGoalFloor());
	    }
	}

    }

    // Task --loop
    // wait for available task (if no task is available change direction to
    // static)
    // Fulfill task
    // moveToGoalFloor() || or stopElevator()
    //

    /**
     * Calculates the direction the elevator has to move to get from the
     * currentPosition to the given floor
     */
    private Direction calculateDirection(int floor) {
	int currentPosition = (int) Math.round(readPosition());

	if (currentPosition < floor) {
	    return Direction.UP;
	}
	if (currentPosition > floor) {
	    return Direction.DOWN;
	} else {
	    return Direction.STATIC;
	}
    }

    private void moveToGoalFloor() {
	// send command to start elevator
	// loop-for checking position
	// for each place close to floor check shouldStop
	// if stop on goal floor set goal floor to -1 return.
    }

    /**
     * Sends stop command, send door open command, sleeps for a while, send
     * close door command
     */
    private void performStopProcedure() {

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

    public boolean addTask(Message message) {
	if (outOfOrder.get()) {
	    return false;
	}
	if (message.getMessageType() == MessageType.STOPMESSAGE) {
	    outOfOrder.set(true);
	    return true;
	}

	double currentPositon = readPosition();
	int currentGoalFloor = readGoalFloor();
	Direction currentDirection = readDirection();
	return taskManager.addTask(message, currentPositon, currentGoalFloor, currentDirection);
    }

    public double readPosition() {
	positionLock.lock();
	try {
	    return position;

	} finally {
	    positionLock.unlock();
	}
    }

    /**
     * Returns the direction that the elevator is ordered to move in. Observe
     * that the Direction therefore does not have to reflect the current
     * physical movement of the elevator.
     * 
     * @return
     */
    public Direction readDirection() {
	directionLock.lock();
	try {
	    return orderedDirection;
	} finally {
	    directionLock.unlock();
	    ;
	}
    }

    public int readGoalFloor() {
	goalFloorLock.lock();
	try {
	    return goalFloor;
	} finally {
	    goalFloorLock.lock();
	}
    }

    public void updatePostion(double position) {
	positionLock.lock();
	try {
	    this.position = position;
	} finally {
	    positionLock.unlock();
	}
    }

    public void updateDirection(Direction direction) {
	directionLock.lock();
	try {
	    this.orderedDirection = direction;
	} finally {
	    directionLock.unlock();
	}
    }

    public void updateGoalFloor(int goalFloor) {
	goalFloorLock.lock();
	try {
	    this.goalFloor = goalFloor;
	} finally {
	    goalFloorLock.unlock();
	}
    }
}
