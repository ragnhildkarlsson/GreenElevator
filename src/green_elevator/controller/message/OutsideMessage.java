package green_elevator.controller.message;

import green_elevator.controller.elevator.Elevator.Direction;

public class OutsideMessage implements Message {

    private final int floorNumber;
    private final Direction direction;

    public OutsideMessage(int floorNumber, Direction direction) {
	this.floorNumber = floorNumber;
	this.direction = direction;
    }

    @Override
    public MessageType getMessageType() {
	return MessageType.OUTSIDEMESSAGE;
    }

    public int getFloorNumber() {
	return floorNumber;
    }

    public Direction getDirection() {
	return direction;
    }

}
