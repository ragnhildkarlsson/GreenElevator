package green_elevator.controller;

import java.util.Map;

import elevator.Elevator;

public class IndataDistributor implements Runnable {

    MessageBuffer incommingData;
    MessageBuffer commandBuffer;
    Map<String, Elevator> elevators;

    public IndataDistributor(MessageBuffer incommingData, MessageBuffer commandBuffer, Map<String, Elevator> elevators) {

    }

    @Override
    public void run() {
	// TODO Auto-generated method stub

    }

}
