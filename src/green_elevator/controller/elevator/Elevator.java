package green_elevator.controller.elevator;

import green_elevator.controller.MessageBuffer;
import green_elevator.controller.elevator.task.Task;
import green_elevator.controller.elevator.task.Task.TaskType;
import green_elevator.controller.message.DoorCloseCommand;
import green_elevator.controller.message.DoorOpenCommand;
import green_elevator.controller.message.Message;
import green_elevator.controller.message.Message.MessageType;
import green_elevator.controller.message.MoveCommand;
import green_elevator.controller.message.StopCommand;

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

    private MessageBuffer outgoingMessages;

    private final String upStopLimit = ".92";
    private final String downStopLimit = ".039";
    private final int doorOpenInterval = 2000;

    public enum Direction {
	UP, DOWN, STATIC
    }

    public Elevator(TaskManager taskManager, int id, MessageBuffer outgoingMessages) {
	this.id = id;
	this.taskManager = taskManager;
	orderedDirection = Direction.UP;
	goalFloor = 0;
	positionBuffer = new LinkedBlockingQueue<Double>();
	position = 0;
	outOfOrder = new AtomicBoolean(false);
	this.outgoingMessages = outgoingMessages;
    }

    @Override
    public void run() {
	while (!outOfOrder.get()) {
	    double currentPosition = readPosition();
	    Task task = taskManager.getTask(currentPosition);
	    if (task.getTaskType() == TaskType.OUTSIDETASK) {
		updateDirection(task.getDirection().get());
		updateGoalFloor(task.getGoalFloor());
	    }
	    if (task.getTaskType() == TaskType.INSIDETASK) {
		updateGoalFloor(readGoalFloor());
	    }
	    moveToGoalFloor();

	}

    }

    private void moveToGoalFloor() {
	// Case elevator is on the wanted floor
	double currentPosition = readPosition();
	Direction currentDirection = readDirection();
	int currentGoalFloor = readGoalFloor();
	if (taskManager.shouldStop(currentPosition, currentDirection, currentGoalFloor)) {
	    performStopProcedure();
	    if (getClosestFloor(currentPosition) == currentGoalFloor) {
		return;
	    }
	}
	int closestFloor = getClosestFloor(currentPosition);
	Direction moveDirection;
	if (closestFloor < currentGoalFloor) {
	    moveDirection = Direction.UP;
	} else {
	    moveDirection = Direction.DOWN;
	}
	positionBuffer.clear();
	sendMoveMessage(moveDirection);
	while (true) {
	    currentPosition = updatePosition();
	    if (outOfOrder.get()) {
		sendStopMessage();
		return;
	    }
	    if (isOnStopPosition(moveDirection, currentPosition))
		if (taskManager.shouldStop(currentPosition, currentDirection, currentGoalFloor)) {
		    performStopProcedure();
		    if (getClosestFloor(currentPosition) == currentGoalFloor)
			return;
		    else
			sendMoveMessage(moveDirection);
		}
	}
    }

    /**
     * Sends stop command, send door open command, sleeps for a while, send
     * close door command
     */
    private void performStopProcedure() {
	sendStopMessage();
	outgoingMessages.putMessage(new DoorOpenCommand(id));
	try {
	    Thread.sleep(doorOpenInterval);
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
	outgoingMessages.putMessage(new DoorCloseCommand(id));
    }

    private int getClosestFloor(double position) {
	return (int) Math.round(position);
    }

    private void sendMoveMessage(Direction direction) {
	Message message = new MoveCommand(id, direction);
	outgoingMessages.putMessage(message);
    }

    private void sendStopMessage() {
	outgoingMessages.putMessage(new StopCommand(id));
    }

    private boolean isOnStopPosition(Direction direction, double position) {
	String currentStopPositon;
	if (direction == Direction.UP)
	    currentStopPositon = upStopLimit;
	else
	    currentStopPositon = downStopLimit;
	String currentPosition = Double.toString(position);
	if (currentPosition.contains(currentStopPositon))
	    return true;
	return false;
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

    private double updatePosition() {
	try {
	    Double newPosition = positionBuffer.take();
	    positionLock.lock();
	    try {
		this.position = newPosition;
		return this.position;
	    } finally {
		positionLock.unlock();
	    }
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
	throw new IllegalStateException();
    }

    private void updateDirection(Direction direction) {
	directionLock.lock();
	try {
	    this.orderedDirection = direction;
	} finally {
	    directionLock.unlock();
	}
    }

    private void updateGoalFloor(int goalFloor) {
	goalFloorLock.lock();
	try {
	    this.goalFloor = goalFloor;
	} finally {
	    goalFloorLock.unlock();
	}
    }
}
