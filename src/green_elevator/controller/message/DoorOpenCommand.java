package green_elevator.controller.message;

public class DoorOpenCommand implements Message {

    private final int elevatorId;

    public int getElevatorId() {
	return elevatorId;
    }

    public DoorOpenCommand(int elevatorId) {
	this.elevatorId = elevatorId;
    }

    @Override
    public MessageType getMessageType() {
	return MessageType.DOOROPENCOMMAND;
    }

}
