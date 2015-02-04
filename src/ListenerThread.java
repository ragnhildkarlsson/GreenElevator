import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


public class ListenerThread implements Runnable{
	
	private Socket socket;
	public ListenerThread(Socket socket) {
		this.socket = socket;
	}
	@Override
	public void run() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String inputLine;
			while((inputLine =reader.readLine())!= null){
				System.out.println("Recieved input: "+ inputLine);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}}
}
