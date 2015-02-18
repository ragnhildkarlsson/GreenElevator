package green_elevator.controller.message;


public class InsideMessage implements Message {

    private final int elevatorNumber;
    private final int floorNumber;

    public InsideMessage(int elevatorNumber, int floorNumber) {
	this.elevatorNumber = elevatorNumber;
	this.floorNumber = floorNumber;
    }

    @Override
    public MessageType getMessageType() {
	return MessageType.INSIDECOMMAND;
    }

    public int getElevatorNumber() {
	return elevatorNumber;
    }

    public int getFloorNumber() {
	return floorNumber;
    }

}
