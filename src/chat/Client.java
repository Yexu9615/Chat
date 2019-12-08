package chat;

import java.net.Socket;
import java.util.Scanner;
import java.io.*;

public class Client {
	/**
	 * java.net.Socket contains TCP Socket runs on Client
	 */
	private Socket socket;

	/**
	 * localhost = 127.0.0.1 range 0-65535 avoid 0-4000 usually 8088 two parameters
	 * 1. find the computer according to the ip-adress 2. find the application in
	 * the server
	 * 
	 * dummy text
	 */
	public Client() throws Exception {
		System.out.println("connecting to the server");
		socket = new Socket("localhost", 8088);
		// or socket = new Socket("127.0.0.1",8088)
		System.out.println("connected to server!");
	}

	public void start() {
		try {
			Scanner scan = new Scanner(System.in);
			/**
			 * nickname
			 */
			// ask the user to giva a nickname
			String nickname = null;
			while (true) {
				System.out.println("Please give a user-name");
				nickname = scan.nextLine();
				if (nickname.length() > 0) {
					break;
				}
				System.out.println("Inappropriate input");
			}
			System.out.println("Welcome " +nickname+"!");
			// OutputStram is an abstract class, return a subclass
			OutputStream out = socket.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
			PrintWriter pw = new PrintWriter(osw, true);
			/**
			 * send the nickname to the server
			 */
			ServerHandler handler = new ServerHandler();
			Thread t = new Thread(handler);
			t.start();
			pw.println(nickname);
			/*
			 * send characters to the server
			 */
			// pw.println("hello server");
			while (true) {
				String text = scan.nextLine();
				pw.println(text);
				// br.readLine();
			}

			// pw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		try {
			Client client = new Client();
			client.start();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("failure! can't run the client");
		}
	}

	/**
	 * the thread is to receive messages sent from server and output them to client
	 * 
	 * @author alen
	 *
	 */
	class ServerHandler implements Runnable {
		public void run() {
			try {
				InputStream in = socket.getInputStream();
				InputStreamReader isr = new InputStreamReader(in, "UTF-8");
				BufferedReader br = new BufferedReader(isr);
				String message = null;
				while ((message = br.readLine()) != null) {
					System.out.println(message);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	}
}