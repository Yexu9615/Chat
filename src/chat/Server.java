package chat;

import java.io.*;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {
	/**
	 * ServerSocket is responsible for: 1:apply a server port from the system by
	 * which the client-terminal can be connected 2:observe the applied port, when a
	 * client-terminal tries to connect via the port ServerSocket will create a
	 * Socket to build connection with client terminal
	 */
	private ServerSocket server;

	private Map<String, PrintWriter> allOut;

	public Server() throws Exception {
		server = new ServerSocket(8088);
		allOut = new HashMap<String, PrintWriter>();
	}

	/**
	 * to create a list to hold all output streams
	 */
	private synchronized void addOut(String nickname, PrintWriter out) {
		allOut.put(nickname, out);
	}

	private synchronized void removeOut(String nickname) {
		allOut.remove(nickname);
	}

	private synchronized void sendMessage(String message) {
		for (PrintWriter out : allOut.values()) {
			out.println(message);
		}
	}

	public void start() {
		try {

			System.out.println("waiting for a client to be connected");

			/**
			 * accept method is to jam, to observe the server-terminal, until a client-term
			 * is being connected and then create a Socket using the Socket the server-term
			 * can interact with the new-connected client
			 */
			while (true) {
				Socket socket = server.accept();
				System.out.println("a client is connected");
				sendMessage("");
				Runnable ch = new ClientHandler(socket);
				Thread t = new Thread(ch);
				t.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	class ClientHandler implements Runnable {
		private Socket socket;
		// info about client
		private String host;
		private String nickname;

		ClientHandler(Socket socket) {
			this.socket = socket;
			InetAddress address = socket.getInetAddress();
			/**
			 * get the ip-adress
			 */
			host = address.getHostAddress();
		}

		public void run() {
			/**
			 * method provided by Socket InputStream getInputStream() the data comes from
			 * the distant device
			 */
			PrintWriter pw = null;
			try {
				// System.out.println(host + " is online!"+"nickname: "+nickname);
				// sendMessage(host + " is online!!!" );
				InputStream in = socket.getInputStream();
				InputStreamReader isr = new InputStreamReader(in, "UTF-8");
				BufferedReader br = new BufferedReader(isr);
				// read the nickname
				nickname = br.readLine();
				/**
				 * send message back to client
				 * 
				 */
				OutputStream out = socket.getOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
				pw = new PrintWriter(osw, true);
				/*
				 * save output streams into the list
				 */
				addOut(nickname, pw);
				String message = null;
				/**
				 * when br.readLine is reading the message sent from client if the client
				 * becomes offline the difference of operative system will lead to different
				 * results
				 * 
				 * windows: throw exception Linux:return null
				 */
				// sendMessage(nickname + " is online!!!");
				System.out.println(nickname + " is online!!!");
				while ((message = br.readLine()) != null) {
					if (messageIsPrivate(message)) {
						String receiver=message.substring(1,message.indexOf(":"));
						allOut.get(receiver).println(nickname+": "+message.substring(message.indexOf(":")+1));
					//	System.out.println(message.substring(message.indexOf(":")+1));
					} else {
						System.out.println(nickname + ": " + message);
					}
				}
			} catch (Exception e) {
			} finally {
				/**
				 * when the client becomes offline
				 */

				// delete the output stream from the list
				removeOut(nickname);
				sendMessage(nickname + " is offline");
				try {
					socket.close();
				} catch (IOException e) {
					// e.printStackTrace();
				}
			}

		}

		private boolean messageIsPrivate(String message) {
			if (message.startsWith("@")) {
				int positionOfColon = message.indexOf(":");
				String receiver = message.substring(1, positionOfColon);
				if (allOut.containsKey(receiver)) {
					return true;
				}
			}
			return false;
		}
	}

	public static void main(String[] args) {
		try {
			Server server = new Server();
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("fail to run the server");
		}

	}

}