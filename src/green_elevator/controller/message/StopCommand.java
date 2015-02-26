package green_elevator.controller.message;

public class StopCommand implements Message {

    private final int elevatorNumber;

    public StopCommand(int elevatorNumber) {
	this.elevatorNumber = elevatorNumber;
    }

    @Override
    public MessageType getMessageType() {
	return MessageType.STOPCOMMAND;
    }

    public int getElevatorNumber() {
	return elevatorNumber;
    }

}
