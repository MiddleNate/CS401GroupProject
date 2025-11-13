import java.net.*;
import java.util.*;
import java.io.*;
import java.lang.Integer;

public class Server {
	protected static Map<Integer, BankAccount> accounts;
	// access within ClientHandler using Server.accounts
	
	protected static Map<String, User> users;
	// access within ClientHandler using Server.users
	
	public static void main(String[] args) {
		// loading users and accounts into memory
		try {
			// load hashmap of users from file (key is username)
			FileInputStream userFile = new FileInputStream("users.txt");
			ObjectInputStream userStream = new ObjectInputStream(userFile);
			users = (HashMap<String, User>) userStream.readObject();
			// convert it to a synchronized map (prevents multithreading issues)
			users = Collections.synchronizedMap(users);
			
			// load hashmap of accounts from file (key is account id)
			FileInputStream accountFile = new FileInputStream("accounts.txt");
			ObjectInputStream accountStream = new ObjectInputStream(accountFile);
			accounts = (HashMap<Integer, BankAccount>) accountStream.readObject();
			// convert it to a synchronized map (prevents multithreading issues)
			accounts = Collections.synchronizedMap(accounts);
			
			// get the static counters for customer, employee, and bankaccount from their file
			FileInputStream countFile = new FileInputStream("counts.txt");
			Scanner counts = new Scanner(countFile);
			Customer.setCustomerCount(counts.nextInt());
			Employee.setEmployeeCount(counts.nextInt());
			BankAccount.setCount(counts.nextInt());
			Transaction.setTransactionCount(counts.nextInt());
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
	
	private static class ClientHandler implements Runnable {
		private final Socket client;
		private User user;
		
		public ClientHandler(Socket client) {
			this.client = client;
			user = null;
		}
		
		public void run() {
			ObjectOutputStream out = null;
			ObjectInputStream in = null;
			
			try {
				out = new ObjectOutputStream(client.getOutputStream());
				in = new ObjectInputStream(client.getInputStream());
				
				boolean loggingout = false;
				
				while (!loggingout) {
					Message m = (Message) in.readObject();
					
					// if we are not logged in, only accept login messages
					if (user == null && m.getType() == MessageType.Login) {
						
						// await next message
						continue;
					}
					
					// if we are logged in as a customer, accept inforequests and transactions only
					if (user != null && user instanceof Customer) {
						switch (m.getType()) {
						case MessageType.Logout:
							
							break;
						case MessageType.InfoRequest:
							
							break;
						case MessageType.Transaction:
							
							break;
						default:
							// do nothing for other message types
							break;
						}
					}
					
					// if we are logged in as an employee, accept inforequests, transactions, and account operations
					if (user != null && user instanceof Employee) {
						switch (m.getType()) {
						case MessageType.Logout:
							
							break;
						case MessageType.InfoRequest:
							
							break;
						case MessageType.Transaction:
							
							break;
						case MessageType.CreateCustomer:
							
							break;
						case MessageType.OpenAccount:
							
							break;
						case MessageType.CloseAccount:
							
							break;
						case MessageType.UpdateAccount:
							
							break;
						default:
							// do nothing for other message types
							break;
						}
					}
				}
				
			} catch (Exception e) {
				// this will catch exceptions with the inputstream only
				
			} finally {
				// close resources and set the client as not logged in since we are disconnecting
				
			}
		}
	}

}
