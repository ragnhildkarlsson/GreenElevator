package green_elevator.controller.message;

public class DoorCloseCommand implements Message {
    private final int elevatorId;

    public DoorCloseCommand(int elevatorId) {
	this.elevatorId = elevatorId;
    }

    public int getElevatorId() {
	return elevatorId;
    }

    @Override
    public MessageType getMessageType() {
	return MessageType.DOORCLOSECOMMAND;
    }

}
