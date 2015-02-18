package green_elevator.controller.elevator;

import green_elevator.controller.elevator.Elevator.Direction;
import green_elevator.controller.elevator.task.Task;
import green_elevator.controller.elevator.task.Task.TaskType;
import green_elevator.controller.message.InsideMessage;
import green_elevator.controller.message.Message;
import green_elevator.controller.message.Message.MessageType;
import green_elevator.controller.message.OutsideMessage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TaskManager {

	private final Lock taskLock;
	private final Condition taskAvailable;
	private boolean notOpenForCommands;
	private List<Task> tasks;
	private int taskId;

	public TaskManager() {
		this.notOpenForCommands = false;
		this.tasks = new ArrayList<Task>();
		this.taskId = 0;
		this.taskLock = new ReentrantLock();
		this.taskAvailable = taskLock.newCondition();
	}

	public void addTask(Message command, double currentPosition, int goalFloor, Direction direction) {
		taskLock.lock();

		if (notOpenForCommands)
			return;

		if (command.getMessageType() == MessageType.STOPCOMMAND) {
			notOpenForCommands = true;
			return;
		}

		if (command.getMessageType() == MessageType.INSIDECOMMAND) {
			InsideMessage insideMessage = (InsideMessage) command;
			int wantedFloor = insideMessage.getFloorNumber();
			switch (direction) {
			case STATIC: // static direction means all inside commands are okay
				tasks.add(new Task(getTaskID(), TaskType.INSIDETASK, Optional.empty(), Optional.of(wantedFloor)));
				taskAvailable.signal();
				break;
			case UP:
				int lowestFloor = Math.min(goalFloor, (int) currentPosition);
				if (lowestFloor < wantedFloor) {
					tasks.add(new Task(getTaskID(), TaskType.INSIDETASK, Optional.empty(), Optional.of(wantedFloor)));
					taskAvailable.signal();
				}
				break;
			case DOWN:
				int highestFloor = Math.max(goalFloor, (int) currentPosition);
				if (highestFloor > wantedFloor) {
					tasks.add(new Task(getTaskID(), TaskType.INSIDETASK, Optional.empty(), Optional.of(wantedFloor)));
					taskAvailable.signal();
				}
				break;
			}
		}

		if (command.getMessageType() == MessageType.OUTSIDECOMMAND) {
			OutsideMessage outsideMessage = (OutsideMessage) command;
			int wantedFloor = outsideMessage.getFloorNumber();
			tasks.add(new Task(getTaskID(), TaskType.OUTSIDETASK, Optional.empty(), Optional.of(wantedFloor)));
			taskAvailable.signal();
		}

		taskLock.unlock();
	}

	private int getTaskID() {
		taskId++;
		return taskId;
	}

	public int getNumberOfTasks() {
		taskLock.lock();
		int size = tasks.size();
		taskLock.unlock();
		return size;

	}

	/**
	 * Blocking call to get a task.
	 * 
	 * @return
	 */
	public Optional<Task> getTask(Optional<Direction> direction) {

		taskLock.lock();
		try {
			while (tasks.size() == 0)
				taskAvailable.wait();

			// try to first find inside commands matching the current direction TODO do I have to check direction?
			if (direction.isPresent())
				for (Task task : tasks) {
					if ((task.getTaskType() == TaskType.INSIDETASK))
						return Optional.of(task);

				}

			// Get any outside task with any direction?

			// Worst case: inside task with wrong direction!?

		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			taskLock.unlock();
		}

		// TODO implement
		// PriorityList
		// (1) stop tasks --if stop -stop the possibility to add new tasks to
		// this elevator
		// (2) inside commands such as same direction,
		//
		// (3) outside commands
		// only if goalFloor and Direction is compatible with the elevators
		// postions direction AND goalFloor
		return Optional.empty();
	}

	/**
	 * Evaluates if the elevator should stop at next floor given the position, direction and goalFloor of the elevator
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
		taskLock.lock();
		try {
			boolean shouldStop = false;
			int closestFloor = (int) Math.round(position);

			// check if the true direction and the given direction is in conflict and goal floor != closest floor
			if ((direction == Direction.UP) && (goalFloor < closestFloor))
				return false;

			if ((direction == Direction.DOWN) && (goalFloor > closestFloor))
				return false;

			Iterator<Task> iterator = tasks.iterator();
			while (iterator.hasNext()) {
				Task task = iterator.next();
				// remove all inner tasks which wants to stop at this floor
				if ((task.getTaskType() == TaskType.INSIDETASK) && (task.getGoalFloor().get() == closestFloor)) {
					iterator.remove();
					shouldStop = true;
				}
				// remove outside tasks which wants to travel in the same direction and who is waiting at the closing
				// floor
				if ((task.getTaskType() == TaskType.OUTSIDETASK) && (task.getDirection().get() == direction)
						&& (task.getGoalFloor().get() == closestFloor)) {
					iterator.remove();
					shouldStop = true;
				}

			}

			return shouldStop;
		} finally {
			taskLock.unlock();
		}
	}

}
