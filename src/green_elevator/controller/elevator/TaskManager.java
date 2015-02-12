package green_elevator.controller.elevator;

import green_elevator.controller.elevator.Elevator.Direction;
import green_elevator.controller.elevator.task.Task;
import green_elevator.controller.message.Message;
import green_elevator.controller.message.Message.MessageType;

import java.util.Optional;

public class TaskManager {

    public void addTask(Message command) {
	// Pseudo Code
	// Pick up present position and direction data.
	if (notOpenfornewcommands) {
	    return;
	}
	if (command.getMessageType() == MessageType.STOPMESSAGE) {
	    // set no open for new commands
	}
	if (command.getMessageType() == MessageType.INSIDEMESSAGE) {
	    Direction direction;
	    // check if goal floor is compatible with the position and direction
	    // of the elevator
	    // discard otherwise
	}
	if (command.getMessageType() == MessageType.OUTSIDEMESSAGE) {
	    // add allways
	}
    }

    public int getNumberOfTasks() {
	// useful for evaluator
    }

    public Optional<Task> getTask() {
	// TODO implement
	// PriorityList
	// (1) stop tasks --if stop -stop the possibility to add new tasks to
	// this elevator
	// (2) inside commands such as same direction,
	//
	// (3) outside commands
	// only if goalFloor and Direction is compatible with the elevators
	// postions direction AND goalFloor
	return null;
    }

    /**
     * Evaluates if the elevator should stop at next floor given the position
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
    public boolean shouldStop(double position, Elevator.Direction direction, int goalFloor) {
	// TODO implement
	// check if compatible with any task
	// remove all tasks fulfilled by this stop
	return false;
    }

    private void removeTask() {

    }

}
