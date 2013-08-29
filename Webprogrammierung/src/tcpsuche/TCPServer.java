package tcpsuche;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class TCPServer {

	private ArrayList<String> names = null;
	private ArrayList<String> output;

	private Socket socket = null;
	private ServerSocket sSocket = null;
	private BufferedReader reader = null;
	private ObjectOutputStream printer = null;

	private TCPServer(int port) {
		System.out.println("TCP Server Konstruktor");
		try {
			sSocket = new ServerSocket(port);
			System.out.println("Wartet auf TCP-Verbindung!");
			socket = sSocket.accept();
			reader = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			printer = new ObjectOutputStream(socket.getOutputStream());
			initList();
		} catch (IOException e) {
			e.getStackTrace();
		}
	}

	public boolean validInit() {
		if (sSocket != null && socket != null && reader != null
				&& printer != null && names != null && names.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public static TCPServer getServer(int port) {
		TCPServer s = new TCPServer(port);
		if (s.validInit()) {
			return s;

		} else {
			s.close();
			return null;
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
			if (sSocket != null)
				sSocket.close();
		} catch (IOException e) {
			e.getStackTrace();
		}
	}

	public String readMessage() {
		try {
			return reader.readLine();
		} catch (IOException e) {
			e.getStackTrace();
			return null;
		}
	}

	public boolean sendAnswer(String input) {
		output = new ArrayList<String>();
		for (String name : names) {
			if (name.matches(input)) {
				output.add(name);
			}
		}
		try {
			printer.writeObject(new MessageContainer(output));
			printer.flush();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static void main(String[] args) throws IOException {
		boolean shutdown = false;

		while (!shutdown) {
			boolean close = false;
			TCPServer s = TCPServer.getServer(50000);
			if (s == null) {
				System.out
						.println("TCP Server wurde nicht richtig initialisiert");
				close = true;
			}		
			while (!close) {
				System.out.println("TCP Server wartet auf Eingabe");
				String input = s.readMessage();
				if (input == null) {
					input = "";
					System.out
							.println("Input wurde durch leeren String ersetzt!");
				}
				System.out.println("Eingabe: " + input);
				if (input.toUpperCase().equals("SHUTDOWN")) {
					shutdown = true;
					close = true;
				} else if (input.toUpperCase().equals("FINISH")) {
					close = true;
				} else {
					if(!s.sendAnswer(input))
						close = true;
				}
			}
			if( s != null) {
				s.close();
			}

			System.out.println("TCP Server: Client abgearbeitet.");
		}
		System.out.println("TCP Sever-Service wird beendet!");
	}

	private void initList() {
		BufferedReader readerNames = null;
		try {
			readerNames = new BufferedReader(new FileReader("namen.txt"));
			String zeile = readerNames.readLine();
			names = new ArrayList<String>();
			while (zeile != null) {
				names.add(zeile);
				zeile = readerNames.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (readerNames != null)
					readerNames.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
