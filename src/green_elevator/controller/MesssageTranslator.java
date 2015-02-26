package green_elevator.controller;

import green_elevator.controller.elevator.Elevator.Direction;
import green_elevator.controller.message.DoorCloseCommand;
import green_elevator.controller.message.DoorOpenCommand;
import green_elevator.controller.message.InsideMessage;
import green_elevator.controller.message.Message;
import green_elevator.controller.message.Message.MessageType;
import green_elevator.controller.message.MoveCommand;
import green_elevator.controller.message.OutsideMessage;
import green_elevator.controller.message.PositionMessage;
import green_elevator.controller.message.StopCommand;

import java.util.Optional;

public class MesssageTranslator {

    public Optional<String> getMessageString(Message message) {
	switch (message.getMessageType()) {
	case MOVECOMMAND:
	    MoveCommand moveCommand = (MoveCommand) message;
	    return Optional.of("m " + moveCommand.getElevatorNumber() + " "
		    + getDirectionNumber(moveCommand.getDirection()));
	case STOPCOMMAND:
	    StopCommand stopCommand = (StopCommand) message;
	    return Optional.of("m " + stopCommand.getElevatorNumber() + " " + 0);
	case DOOROPENCOMMAND:
	    DoorOpenCommand doorOpenCommand = (DoorOpenCommand) message;
	    return Optional.of("d " + doorOpenCommand.getElevatorId() + " 1");
	case DOORCLOSECOMMAND:
	    DoorCloseCommand doorCloseCommand = (DoorCloseCommand) message;
	    return Optional.of("d " + doorCloseCommand.getElevatorId() + " -1");
	default:
	    return Optional.empty();
	}
    }

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
	    case INSIDEMESSAGE:
		messageWords = input.split(" ");
		elevatorNumber = Integer.parseInt(messageWords[1]);
		floorNumber = Integer.parseInt(messageWords[2]);
		message = new InsideMessage(elevatorNumber, floorNumber);
		return Optional.of(message);
	    case OUTSIDEMESSAGE:
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
	    default:
		return Optional.empty();
	    }
	}
	return Optional.empty();

    }

    private Optional<MessageType> getMessageType(String input) {
	if (input.contains("p")) {
	    return Optional.of(MessageType.INSIDEMESSAGE);
	}
	if (input.contains("b")) {
	    return Optional.of(MessageType.OUTSIDEMESSAGE);
	}

	if (input.contains("f")) {
	    return Optional.of(MessageType.POSITIONMESSAGE);

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

    private int getDirectionNumber(Direction direction) {
	switch (direction) {
	case UP:
	    return 1;
	case DOWN:
	    return -1;
	default:
	    return 0;
	}
    }

}
