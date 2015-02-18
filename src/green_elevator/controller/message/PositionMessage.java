package green_elevator.controller.message;

public class PositionMessage implements Message {

    private final int elevatorNumber;
    private final double position;

    public PositionMessage(int elevatorNumber, double position) {
	this.elevatorNumber = elevatorNumber;
	this.position = position;
    }

    @Override
    public MessageType getMessageType() {
	return MessageType.POSITIONMESSAGE;
    }

    public int getElevatorNumber() {
	return elevatorNumber;
    }

    public double getPosition() {
	return position;
    }

}
