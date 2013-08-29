package tcpsuche;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class TCPServer {

	public static final int port = 50000;

	private static ObjectOutputStream printer;
	private static BufferedReader reader;
	private static Socket socket;
	private static ServerSocket serverSocket;
	private static ArrayList<String> names = new ArrayList<String>();
	private static ArrayList<String> output;
	private static boolean close = false;
	private static boolean shutdown = false;

	public static void main(String[] args) throws IOException {
		initList();

		while (!shutdown) {
			System.out.println("TCP Server started");
			try {
				serverSocket = new ServerSocket(port);
				socket = serverSocket.accept();
				printer = new ObjectOutputStream(socket.getOutputStream());
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

				while (!close) {
					String search = reader.readLine();
					System.out.println("Eingabe: " + search);
					if (search.equals("SHUTDOWN")) {
						shutdown = true;
						close = true;
					} else if (search.equals("FINISH")) {
						close = true;
					} else {
						output = new ArrayList<String>();
						for (String name : names) {
							if (name.matches(search)) {
								output.add(name);
							}
						}
						printer.writeObject(new MessageContainer(output));
						printer.flush();
					}
				}
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (printer != null)
					printer.close();
				if (reader != null)
					reader.close();
				if (socket != null)
					socket.close();
				if (serverSocket != null) {
					serverSocket.close();
				}
			}
			System.out.println("TCP Server closed");
		}
		System.out.println("TCP Server finish");
	}

	private static void initList() throws IOException {
		BufferedReader readerNames = null;
		try {
			readerNames = new BufferedReader(new FileReader("namen.txt"));
			String zeile = readerNames.readLine();
			while (zeile != null) {
				names.add(zeile);
				zeile = readerNames.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (readerNames != null)
				readerNames.close();
		}

	}
}
