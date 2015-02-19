package green_elevator.controller;

import green_elevator.controller.elevator.Elevator;
import green_elevator.controller.message.Message;
import green_elevator.controller.message.PositionMessage;
import green_elevator.controller.message.StopMessage;

import java.util.Map;

public class IndataDistributor implements Runnable {

    private final MessageBuffer incommingData;
    private final MessageBuffer commandBuffer;
    private final Map<Integer, Elevator> elevators;
    private final Evaluator evaluator;

    public IndataDistributor(MessageBuffer incommingData, MessageBuffer commandBuffer,
	    Map<Integer, Elevator> elevators, Evaluator evaluator) {
	this.incommingData = incommingData;
	this.commandBuffer = commandBuffer;
	this.elevators = elevators;
	this.evaluator = evaluator;
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
		// TODO add task to evaluator
	    }
	}

	// TODO Auto-generated method stub

    }

}
