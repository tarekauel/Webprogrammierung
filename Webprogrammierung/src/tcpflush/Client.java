package tcpflush;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
	private Socket socket = null;
	private BufferedReader reader = null;
	private PrintWriter printer = null;

	public Client(String host, int port) {
		try {
			socket = new Socket(host, port);
			reader = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			printer = new PrintWriter(socket.getOutputStream());
		} catch (IOException e) {
			e.getStackTrace();
		}
	}

	public void close() {
		try {
			if (reader != null)
				reader.close();
			if (printer != null)
				printer.close();
			if (socket != null)
				socket.close();
		} catch (IOException e) {
			e.getStackTrace();
		}
	}

	public String readMessage() {
		try {
			return reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void writeMessage(String message) {
		printer.println(message);
		//printer.flush();

	}

	public static void main(String[] args) {
		Client c = new Client( "localhost", 50000);
		for(int i=0; i<5462-1; i++) {
			c.writeMessage("A");
		}
		c.close();
	}
}
