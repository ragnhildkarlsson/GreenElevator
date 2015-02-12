package green_elevator.controller.elevator.task;

import green_elevator.controller.elevator.Elevator.Direction;

import java.util.Optional;

public class Task {
    private final int id;
    private final TaskType taskType;
    private final Optional<Direction> direction;
    private final Optional<Integer> goalFloor;

    public enum TaskType {
	INSIDETASK, OUTSIDETASK, STOPTASK
    }

    public Task(int id, TaskType taskType, Optional<Direction> direction, Optional<Integer> goalFloor) {
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

    public Optional<Integer> getGoalFloor() {
	return goalFloor;
    }

}
