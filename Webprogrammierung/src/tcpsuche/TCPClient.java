package tcpsuche;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPClient {

	private PrintWriter printer;
	private ObjectInputStream reader;
	private BufferedReader console;
	private Socket socket;

	private TCPClient(String host, int port) {
		try {
			socket = new Socket(host, port);
			reader = new ObjectInputStream(socket.getInputStream());
			printer = new PrintWriter(socket.getOutputStream());
			console = new BufferedReader(new InputStreamReader(System.in));
		} catch (IOException e) {
			e.getStackTrace();
		}
	}

	public boolean validInit() {
		if (socket != null && reader != null && printer != null
				&& console != null) {
			return true;
		} else {
			return false;
		}
	}

	public static TCPClient getClient(String host, int port) {
		TCPClient c = new TCPClient(host, port);
		if(c.validInit()) {
			return c;
		} else {
			c.close();
			return null;
		}
	}

	public void close() {
		try {
			if (console != null)
				console.close();
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

	public MessageContainer readMessage() {
		try {
			return (MessageContainer) reader.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String readConsole() {
		try {
			return console.readLine();
		} catch (IOException e) {
			e.getStackTrace();
			return null;
		}
	}

	public void sendMessage(String message) {
		printer.println(message);
		printer.flush();
	}

	public static void main(String[] args) {
		System.out.println("TCP Client start");
		TCPClient c = TCPClient.getClient("localhost", 50000);
		if(c == null) {
			System.out.println("Error. Client wurde nicht richtig initialisiert!");
			return;
		} 
		boolean inputMode = true;
		while (inputMode) {
			String input = c.readConsole();
			c.sendMessage(input);
			if (input.equals("FINISH") || input.equals("SHUTDOWN")) {
				inputMode = false;
			} else {
				MessageContainer reply = c.readMessage();
				System.out.println(reply.getDate());
				for (String zeile : reply.getMessage()) {
					System.out.println(zeile);
				}
			}
		}
		c.close();
		System.out.println("TCP Client stop");
	}

}
