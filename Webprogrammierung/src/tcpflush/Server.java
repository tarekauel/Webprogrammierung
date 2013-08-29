package tcpflush;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	private Socket socket = null;
	private ServerSocket sSocket = null;
	private BufferedReader reader = null;
	private PrintWriter printer = null;

	public Server(int port) {
		try {
			sSocket = new ServerSocket(port);
			socket = sSocket.accept();
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
			if (sSocket != null)
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

	public static void main(String[] args) {

			Server s = new Server(50000);
			System.out.println(s.readMessage());
			s.close();
		
	}

}
