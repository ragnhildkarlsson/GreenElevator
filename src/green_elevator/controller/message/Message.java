package green_elevator.controller.message;

public interface Message {

    public enum MessageType {
	POSITIONMESSAGE, VELOCITY, STOPMESSAGE, INSIDEMESSAGE, OUTSIDEMESSAGE, MOVECOMMAND, STOPCOMMAND
    }

    public MessageType getMessageType();
}
