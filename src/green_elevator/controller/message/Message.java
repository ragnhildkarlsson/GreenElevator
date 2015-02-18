package green_elevator.controller.message;

public interface Message {

    public enum MessageType {
	POSITIONMESSAGE, VELOCITY, STOPMESSAGE, INSIDECOMMAND, OUTSIDECOMMAND, MOVECOMMAND
    }

    public MessageType getMessageType();
}
