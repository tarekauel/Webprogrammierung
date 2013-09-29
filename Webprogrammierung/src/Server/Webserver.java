package Server;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;

public class Webserver {

	private ServerSocket	sSocket;
	private Socket			socket;
	private BufferedReader	reader;

	public Webserver(int port) throws IOException {
		sSocket = new ServerSocket(port);
	}

	public void runServer() throws IOException {
		System.out.println("----- Run Server -----");
		socket = sSocket.accept();
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		ArrayList<String> request = readRequest();
		String method = getMethod(request);
		String fileName = getRequestedFile(request);

		String contentType = parseContentType(fileName);

		if (getFileExtension(fileName).equals("png")) {
			byte[] file = readFileToByte(fileName);
			if( file == null ) {
				sendAnswer(404, null, null);
			}
			sendAnswer(file, contentType);
		} else {
			ArrayList<String> file = null;
			if (method.equals("POST")) {
				HashMap<String, String> postVars = readPostVars(request);
				file = readFile(fileName, postVars);
			} else {
				file = readFile(fileName);
			}
			sendAnswer(((file == null) ? 404 : 200), file, contentType);
		}
		close();
	}

	private ArrayList<String> readRequest() throws IOException {
		System.out.println("----- Lese Request -----");
		ArrayList<String> request = new ArrayList<String>();
		try {
			String line = readLine(reader);
			while (line != null) {				
				System.out.println(line);
				request.add(line);
				if (!reader.ready()) {
					break;
				}
				line = readLine(reader);
			}

		} catch (SocketTimeoutException e) {
			System.out.println("Lesen durch Timeout beendet");
		}
		if( request.size() > 0 && request.get(0).equals("")) 
			request.remove(0);
		return request;
	}

	private String readLine(BufferedReader reader) throws IOException {
		String line = "";
		while (reader.ready()) {
			char c = (char) reader.read();
			if ((int) c == 10)
				break;
			if ((int) c == 13) {
				reader.read();
				break;
			}
			line += String.valueOf(c);

		}
		return line;
	}

	private String getMethod(ArrayList<String> request) {
		System.out.println("----- Lese Method -----");
		String method = "";

		method = request.get(0).split(" ")[0];

		if (!method.equals("POST") && !method.equals("GET"))
			throw new IllegalArgumentException("Method ist ungültig: " + method);

		System.out.println("Method geparst: " + method);

		return method;

	}

	private HashMap<String, String> readPostVars(ArrayList<String> request) {
		System.out.println("----- Parse Post Vars -----");
		HashMap<String, String> output = new HashMap<String, String>();
		boolean postVarsStarted = false;
		for (String line : request) {

			if (postVarsStarted) {
				String key = line.split("=")[0];
				String value = line.split("=")[1];
				output.put(key, value);
			}

			if (line.equals("")) {
				postVarsStarted = true;
			}
		}
		return output;
	}

	private String getRequestedFile(ArrayList<String> request) {
		System.out.println("----- Parse File Name -----");
		String requestedFile = "";
		String requestLine = request.get(0);
		try {

			requestedFile = requestLine.split(" ")[1];

			// Ersten Slash entfernen
			requestedFile = requestedFile.substring(1, requestedFile.length());

			// Get-Anhang entfernen
			requestedFile = ((requestedFile.indexOf("?") != -1) ? requestedFile
					.substring(0, requestedFile.indexOf("?")) : requestedFile);

			System.out.println("File geparst: " + requestedFile);

		} catch (NullPointerException e) {
			throw new IllegalArgumentException("Request zeile ist ungültig! " + requestLine);
		}

		return requestedFile;
	}

	private ArrayList<String> readFile(String file) throws IOException {
		System.out.println("------- Read File ---------");

		ArrayList<String> output = new ArrayList<String>();
		try {
			BufferedReader fileReader = new BufferedReader(new FileReader(file));
			String line = fileReader.readLine();
			while (line != null) {
				System.out.println(line);
				output.add(line);
				line = fileReader.readLine();
			}
			fileReader.close();
		} catch (FileNotFoundException e) {
			return null;
		}
		return output;
	}

	private ArrayList<String> readFile(String file, HashMap<String, String> data) throws IOException {
		System.out.println("------- Read File ---------");

		ArrayList<String> output = new ArrayList<String>();
		try {
			BufferedReader fileReader = new BufferedReader(new FileReader(file));
			String line = fileReader.readLine();
			while (line != null) {
				if (line.matches("<!--INSERT_DATA=.*")) {
					String key = line.split("=")[1];
					key = key.substring(0, key.length() - 3);
					line = "<div class=\"" + key + "\">" + data.get(key) + "</div>";
				}
				System.out.println(line);
				output.add(line);
				line = fileReader.readLine();
			}
			fileReader.close();
		} catch (FileNotFoundException e) {
			return null;
		}
		return output;
	}

	private byte[] readFileToByte(String file) throws IOException {
		InputStream fileReader = null;
		try {
			fileReader = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			return null;
		}
		byte[] buffer = new byte[1024 * 1024];
		ByteArrayOutputStream helperStream = new ByteArrayOutputStream();

		for (int s; (s = fileReader.read(buffer)) != -1;) {
			helperStream.write(buffer, 0, s);
		}

		helperStream.flush();

		// Helper Stream in ByteArray umwandeln
		byte[] output = helperStream.toByteArray();

		return output;
	}

	private String getFileExtension(String filename) {
		String[] split = filename.split("[.]");
		return split[split.length - 1];
	}

	private String parseContentType(String filename) {
		System.out.println("------ Parse File Extension ------");
		String contentType = "text/text";
		String fileExtension = getFileExtension(filename);
		System.out.println("File-Extension: " + fileExtension);
		contentType = ((fileExtension.equals("html")) ? "text/html" : contentType);
		contentType = ((fileExtension.equals("htm")) ? "text/html" : contentType);
		contentType = ((fileExtension.equals("css")) ? "text/css" : contentType);
		contentType = ((fileExtension.equals("png")) ? "image/png" : contentType);
		return contentType;
	}

	private void sendAnswer(int statusCode, ArrayList<String> answer, String contentType) throws IOException {
		PrintWriter writer = new PrintWriter(socket.getOutputStream());
		writer.println("HTTP/1.0 " + statusCode + " " + ((statusCode == 404) ? "NOT FOUND" : "OK"));
		System.out.println("Content-Type: " + ((statusCode == 404) ? "text/html" : contentType));
		writer.println("Content-Type: " + ((statusCode == 404) ? "text/html" : contentType));
		writer.println("");
		if (answer != null) {
			for (String line : answer) {
				writer.println(line);
			}
		}
		if (statusCode == 404) {
			writer.println("<!DOCTYPE html><head><title>404 - Not Found</title></head><body><h1>404 - Not Found</h1></body></html>");
		}
		writer.flush();
		writer.close();

	}

	private void sendAnswer(byte[] file, String contentType) throws IOException {
		OutputStream out = socket.getOutputStream();
		sendLine("HTTP/1.0 200 OK\n", out);
		sendLine("Content-Type: " + contentType + "\n", out);
		sendLine("\n", out);
		out.write(file, 0, file.length);
		out.flush();
		out.close();
	}

	private void sendLine(String line, OutputStream out) throws IOException {
		System.out.println("Sende in Byte konvertiert: " + line);
		out.write(line.getBytes(), 0, line.getBytes().length);
	}

	private void close() throws IOException {
		reader.close();
		socket.close();
	}

	public static void main(String[] args) throws IOException {
		Webserver w = new Webserver(80);
		while (true) {
			try {
				w.runServer();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				System.err.println(e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
				try {
					w.close();
				} catch( Exception err) {
					err.printStackTrace();
				}
			}
		}
	}

}
