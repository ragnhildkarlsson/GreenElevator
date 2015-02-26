package green_elevator.controller.elevator.task;

import green_elevator.controller.elevator.Elevator.Direction;

import java.util.Optional;

public class Task {
    private final int id;
    private final TaskType taskType;
    private final Optional<Direction> direction;
    private final int goalFloor;

    public enum TaskType {
	INSIDETASK, OUTSIDETASK
    }

    public Task(int id, TaskType taskType, Optional<Direction> direction, int goalFloor) {
	this.id = id;
	this.taskType = taskType;
	this.direction = direction;
	this.goalFloor = goalFloor;
    }

    public int getId() {
	return id;
    }

    public TaskType getTaskType() {
	return taskType;
    }

    public Optional<Direction> getDirection() {
	return direction;
    }

    public int getGoalFloor() {
	return goalFloor;
    }

    /**
     * Returns a string representation of the task
     */
    @Override
    public String toString() {
	if (direction.isPresent())
	    return "Taskid: " + id + " tasktype " + taskType + " goalfloor " + goalFloor + " dorection "
		    + direction.get();
	else
	    return "Taskid:" + id + " tasktype " + taskType + " goalfloor" + goalFloor;
    }
}
