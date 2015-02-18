package green_elevator.controller;

import green_elevator.controller.elevator.Elevator.Direction;
import green_elevator.controller.message.InsideMessage;
import green_elevator.controller.message.Message;
import green_elevator.controller.message.Message.MessageType;
import green_elevator.controller.message.MoveCommand;
import green_elevator.controller.message.OutsideMessage;
import green_elevator.controller.message.PositionMessage;

import java.util.Optional;

public class MesssageTranslator {

    public Optional<Message> getMessage(String input) {
	// TODO implement verlocity and stopbuttonmessage
	if (getMessageType(input).isPresent()) {
	    MessageType messageType = getMessageType(input).get();
	    String[] messageWords;
	    int floorNumber;
	    int elevatorNumber;
	    Direction direction;
	    Message message;
	    switch (messageType) {
	    case INSIDECOMMAND:
		messageWords = input.split(" ");
		elevatorNumber = Integer.parseInt(messageWords[1]);
		floorNumber = Integer.parseInt(messageWords[2]);
		message = new InsideMessage(elevatorNumber, floorNumber);
		return Optional.of(message);
	    case OUTSIDECOMMAND:
		messageWords = input.split(" ");
		floorNumber = Integer.parseInt(messageWords[1]);
		direction = getDirection(Integer.parseInt(messageWords[2])).get();
		message = new OutsideMessage(floorNumber, direction);
		return Optional.of(message);
	    case POSITIONMESSAGE:
		messageWords = input.split(" ");
		elevatorNumber = Integer.parseInt(messageWords[1]);
		double position = Double.parseDouble(messageWords[2]);
		message = new PositionMessage(elevatorNumber, position);
		return Optional.of(message);
	    case MOVECOMMAND:
		messageWords = input.split(" ");
		elevatorNumber = Integer.parseInt(messageWords[1]);
		direction = getDirection(Integer.parseInt(messageWords[2])).get();
		message = new MoveCommand(elevatorNumber, direction);
		return Optional.of(message);
	    default:
		return Optional.empty();
	    }
	}
	return Optional.empty();

    }

    private Optional<MessageType> getMessageType(String input) {
	if (input.contains("p")) {
	    return Optional.of(MessageType.INSIDECOMMAND);
	}
	if (input.contains("b")) {
	    return Optional.of(MessageType.OUTSIDECOMMAND);
	}
	if (input.contains("f")) {
	    return Optional.of(MessageType.POSITIONMESSAGE);
	}
	if (input.contains("m")) {
	    return Optional.of(MessageType.MOVECOMMAND);
	}
	return Optional.empty();

    }

    private Optional<Direction> getDirection(int value) {
	if (value < 0) {
	    return Optional.of(Direction.DOWN);
	}
	if (value > 0) {
	    return Optional.of(Direction.UP);
	}
	if (value == 0) {
	    return Optional.of(Direction.STATIC);
	}
	return Optional.empty();

    }

}
