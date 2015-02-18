package green_elevator.controller.message;

import green_elevator.controller.elevator.Elevator.Direction;

public class MoveCommand implements Message {

    private final int elevatorNumber;
    private final Direction direction;

    public MoveCommand(int elevatorNumber, Direction direction) {
	this.elevatorNumber = elevatorNumber;
	this.direction = direction;
    }

    public int getElevatorNumber() {
	return elevatorNumber;
    }

    public Direction getDirection() {
	return direction;
    }

    @Override
    public MessageType getMessageType() {
	return MessageType.MOVECOMMAND;
    }

}
