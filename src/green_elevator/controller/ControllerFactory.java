package green_elevator.controller;

import green_elevator.controller.elevator.Elevator;
import green_elevator.controller.elevator.TaskManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class ControllerFactory {

	public static void main(String[] args) {
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		int nElevators = Integer.parseInt(args[2]);
		ControllerFactory controllerFactory = new ControllerFactory();
		try {
			controllerFactory.setUpController(host, port, nElevators);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Set up for controlling an elevator environment on the given host and port.
	 * 
	 * @param host
	 * @param port
	 * @throws IOException
	 */
	public void setUpController(String host, int port, int nElevators) throws IOException {

		Socket socket = new Socket(host, port);

		MesssageTranslator messsageTranslator = new MesssageTranslator();

		MessageBuffer outgoingMessages = new MessageBuffer();
		OutdataSender outdataSender = new OutdataSender(socket.getOutputStream(), outgoingMessages, messsageTranslator);

		ConcurrentHashMap<Integer, Elevator> elevators = new ConcurrentHashMap<Integer, Elevator>();

		for (int i = 1; i <= nElevators; i++) {
			TaskManager taskManager = new TaskManager();
			Elevator elevator = new Elevator(taskManager, i, outgoingMessages);
			elevators.put(i, elevator);
		}
		MessageBuffer taskBuffer = new MessageBuffer();
		Evaluator evaluator = new Evaluator(elevators, taskBuffer);

		MessageBuffer incomingMessages = new MessageBuffer();
		IndataDistributor indataDistributor = new IndataDistributor(incomingMessages, taskBuffer, elevators);

		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		IndataReciever indataReciever = new IndataReciever(reader, incomingMessages, messsageTranslator);

		new Thread(outdataSender).start();
		;
		for (int i = 1; i <= nElevators; i++) {
			new Thread(elevators.get(i)).start();
		}
		new Thread(evaluator).start();
		new Thread(indataDistributor).start();
		Thread threadReciever = new Thread(indataReciever);
		threadReciever.start();

		try {
			threadReciever.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		socket.close();

	}

}
