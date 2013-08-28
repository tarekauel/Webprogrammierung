package tcpsuche;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.zip.GZIPOutputStream;

public class TCPClient {

	public static final int port = 50002;
	public static final String host = "localhost";

	private static PrintWriter printer;
	private static ObjectInputStream reader;
	private static BufferedReader console;
	private static Socket socket;

	private static boolean nextInput = true;

	public static void main(String[] args) throws IOException {
		System.out.println("TCP Client start");
		try {
			socket = new Socket(host, port);
			printer = new PrintWriter( socket.getOutputStream() );
			reader = new ObjectInputStream(socket.getInputStream());
			console = new BufferedReader(new InputStreamReader(System.in));

			while (nextInput) {
				String input = console.readLine();
				printer.println(input);
				printer.flush();
				if (input.equals("FINISH") || input.equals("SHUTDOWN")) {
					nextInput = false;
				} else {
					
					MessageContainer message = (MessageContainer) reader
							.readObject();

					System.out.println(message.getDate());

					for (String zeile : message.getMessage()) {
						System.out.println(zeile);
					}
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (printer != null)
				printer.close();
			if (reader != null)
				reader.close();
			if (socket != null)
				socket.close();
		}
		System.out.println("TCP Client stop");
	}

}
