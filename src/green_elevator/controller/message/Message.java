package green_elevator.controller.message;

public interface Message {

    public enum MessageType {
	POSITION, VELOCITY, STOPCOMMAND, INSIDECOMMAND, OUTSIDECOMMAND
    }

    public MessageType getMessageType();
}
