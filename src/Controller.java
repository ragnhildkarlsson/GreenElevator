import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
	

public class Controller {

	private final static String host = "192.168.0.101";
	private final static int port = 4711;
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		Socket socket = new Socket(host,port);
		new Thread(new ListenerThread(socket)).start();
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
		String commandIn;
		System.out.println("readdy to recieve commands");
		while((commandIn = reader.readLine())!= null){
			out.println(commandIn);
		}
	}

}
