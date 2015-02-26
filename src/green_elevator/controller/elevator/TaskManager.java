package green_elevator.controller.elevator;

import green_elevator.controller.elevator.Elevator.Direction;
import green_elevator.controller.elevator.task.Task;
import green_elevator.controller.elevator.task.Task.TaskType;
import green_elevator.controller.message.InsideMessage;
import green_elevator.controller.message.Message;
import green_elevator.controller.message.Message.MessageType;
import green_elevator.controller.message.OutsideMessage;

import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TaskManager {

    private BlockingQueue<Task> tasks;
    private int taskId;
    private final boolean debug = true;

    public TaskManager() {

	this.tasks = new LinkedBlockingQueue<Task>();
	this.taskId = 0;
    }

    public boolean addTask(Message command, double currentPosition, int goalFloor, Direction direction) {

	if (command.getMessageType() == MessageType.INSIDEMESSAGE) {
	    InsideMessage insideMessage = (InsideMessage) command;
	    int wantedFloor = insideMessage.getFloorNumber();
	    try {
		tasks.put(new Task(getTaskID(), TaskType.INSIDETASK, Optional.empty(), wantedFloor));

	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}

	if (command.getMessageType() == MessageType.OUTSIDEMESSAGE) {
	    OutsideMessage outsideMessage = (OutsideMessage) command;
	    int wantedFloor = outsideMessage.getFloorNumber();
	    tasks.add(new Task(getTaskID(), TaskType.OUTSIDETASK, Optional.of(outsideMessage.getDirection()),
		    wantedFloor));
	}
	return true;
    }

    private int getTaskID() {
	taskId++;
	return taskId;
    }

    public int getNumberOfTasks() {
	int size = tasks.size();
	return size;

    }

    /**
     * Blocking call to get a task. Returns a task with the given
     * 
     * @return
     */
    public Task getTask(double position, Direction direction) {
	try {

	    int presentFloor = (int) Math.round(position);
	    // try to first find inside commands in right direction
	    for (Task task : tasks) {
		if ((task.getTaskType() == TaskType.INSIDETASK)) {
		    if (direction == direction.UP && task.getGoalFloor() > presentFloor)
			return task;
		    if ((direction == direction.DOWN && task.getGoalFloor() < presentFloor))
			return task;
		}
	    }
	    // Take any inside task
	    for (Task task : tasks) {
		if ((task.getTaskType() == TaskType.INSIDETASK))
		    return task;
	    }
	    // Get the outside task that is closest to present position
	    Task bestTask = null;
	    int bestDiff = Integer.MAX_VALUE;
	    for (Task task : tasks) {
		if ((Math.abs(task.getGoalFloor() - presentFloor)) < bestDiff) {
		    bestDiff = Math.abs(task.getGoalFloor() - presentFloor);
		    bestTask = task;
		}
	    }
	    if (bestTask != null)
		return bestTask;

	    bestTask = tasks.take();
	    tasks.put(bestTask);
	    return bestTask;
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
	throw new IllegalStateException();

    }

    /**
     * Evaluates if the elevator should stop at next floor given the position,
     * direction and goalFloor of the elevator
     * 
     * @param position
     *            of the asking elevator
     * @param direction
     *            of the asking elevator
     * @param goalFloor
     *            of the asking elevator
     * @return
     */
    public boolean shouldStop(double position, Direction direction, int goalFloor) {

	boolean shouldStop = false;
	int closestFloor = (int) Math.round(position);
	if ((direction == Direction.UP) && (goalFloor < closestFloor))
	    return false;

	if ((direction == Direction.DOWN) && (goalFloor > closestFloor))
	    return false;

	Iterator<Task> iterator = tasks.iterator();
	while (iterator.hasNext()) {

	    Task task = iterator.next();
	    // remove all inner tasks which wants to stop at this floor
	    if ((task.getTaskType() == TaskType.INSIDETASK) && (task.getGoalFloor() == closestFloor)) {
		iterator.remove();
		shouldStop = true;
	    }
	    // remove outside tasks which wants to travel in the same direction
	    // and who is waiting at the closing floor
	    // check if the true direction and the given direction is in
	    // conflict and goal floor != closest floor

	    if ((task.getTaskType() == TaskType.OUTSIDETASK) && (task.getDirection().get() == direction)
		    && (task.getGoalFloor() == closestFloor)) {
		if (debug)
		    System.out.println("Taskmanager removed " + task.toString());
		iterator.remove();
		shouldStop = true;
	    }
	}
	return shouldStop;
    }
}
