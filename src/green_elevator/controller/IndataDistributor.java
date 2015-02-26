package green_elevator.controller;

import green_elevator.controller.elevator.Elevator;
import green_elevator.controller.message.Message;
import green_elevator.controller.message.PositionMessage;
import green_elevator.controller.message.StopMessage;

import java.util.concurrent.ConcurrentHashMap;

public class IndataDistributor implements Runnable {

    private final MessageBuffer incommingData;
    private final MessageBuffer commandBuffer;
    private final ConcurrentHashMap<Integer, Elevator> elevators;

    public IndataDistributor(MessageBuffer incommingData, MessageBuffer commandBuffer,
	    ConcurrentHashMap<Integer, Elevator> elevators) {
	this.incommingData = incommingData;
	this.commandBuffer = commandBuffer;
	this.elevators = elevators;

    }

    @Override
    public void run() {
	Elevator elevator;
	int elevatorId;
	while (true) {
	    Message message = incommingData.getMessage();
	    switch (message.getMessageType()) {

	    case POSITIONMESSAGE:
		PositionMessage positionMessage = (PositionMessage) message;
		elevatorId = positionMessage.getElevatorNumber();
		elevator = elevators.get(elevatorId);
		elevator.addPositionData(positionMessage.getPosition());
		break;
	    case STOPMESSAGE:
		StopMessage stopMessage = (StopMessage) message;
		elevatorId = stopMessage.getElevatorNumber();
		elevator = elevators.get(elevatorId);
		elevator.addTask(stopMessage);
		break;
	    default:
		commandBuffer.putMessage(message);
	    }
	}
    }
}
