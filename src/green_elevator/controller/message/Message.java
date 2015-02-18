package green_elevator.controller.message;

public interface Message {

    public enum MessageType {
	POSITIONMESSAGE, VELOCITY, STOPMESSAGE, INSIDECOMMAND, OUTSIDECOMMAND, MOVECOMMAND, STOPCOMMAND
    }

    public MessageType getMessageType();
}
