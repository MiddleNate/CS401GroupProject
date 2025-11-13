import java.net.*;
import java.util.*;
import java.io.*;
import java.lang.Integer;

public class Server {

	protected static HashMap<Integer, BankAccount> accounts;
	protected static HashMap<String, User> users;
	
	public static void main(String[] args) {
		// loading users and accounts into memory
		try {
			FileInputStream userFile = new FileInputStream("users.txt");
			ObjectInputStream userStream = new ObjectInputStream(userFile);
			users = (HashMap<String, User>) userStream.readObject();
			
			FileInputStream accountFile = new FileInputStream("accounts.txt");
			ObjectInputStream accountStream = new ObjectInputStream(accountFile);
			accounts = (HashMap<Integer, BankAccount>) accountStream.readObject();
		} catch (Exception e) {
			System.out.println("File loading error: " + e + "\nExiting...");
			System.exit(1);
		}
		
		
		ServerSocket server = null;
		
		try {
			server = new ServerSocket(7855);
			server.setReuseAddress(true);
			
			// while running, accept connections and create
			// a handler in its own thread for each of them
			while (true) {
				Socket client = server.accept();
				
				ClientHandler handler = new ClientHandler(client);
				
				new Thread(handler).start();
			}
			
		} catch (Exception e) {
			System.out.println(e);
		}

	}

}
