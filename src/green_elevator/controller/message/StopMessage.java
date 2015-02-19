package green_elevator.controller.message;

public class StopMessage implements Message {

    private final int elevatorNumber;

    public StopMessage(int elevatorId) {
	this.elevatorNumber = elevatorId;
    }

    @Override
    public MessageType getMessageType() {
	return MessageType.STOPMESSAGE;
    }

    public int getElevatorNumber() {
	return elevatorNumber;
    }

}
