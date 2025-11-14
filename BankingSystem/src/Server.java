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
			
			// close files & streams
			userFile.close();
			accountFile.close();
			countFile.close();
			userStream.close();
			accountStream.close();
			counts.close();
			} catch (Exception e) {
			System.out.println("File loading error: " + e + "\nExiting...");
			System.exit(1);
		}
		
		
		ServerSocket server = null;
		
		try {
			server = new ServerSocket(7855);
			server.setReuseAddress(true);
			
			boolean running = true;
			Scanner scan = new Scanner(System.in);
			
			// while running, accept connections and create
			// a handler in its own thread for each of them
			while (running) {
				// exit the server if anything has been entered into the console
				if (scan.hasNext()) {
					running = false;
				}
				
				Socket client = server.accept();
				
				ClientHandler handler = new ClientHandler(client);
				
				new Thread(handler).start();
			}
			
		} catch (Exception e) {
			System.out.println(e);
		}
		
		try {
			// clears the users file
			PrintWriter userWriter = new PrintWriter("users.txt");
			userWriter.close();
			
			// write the users map to the file
			FileOutputStream userFile = new FileOutputStream("users.txt");
			ObjectOutputStream userStream = new ObjectOutputStream(userFile);
			userStream.writeObject(users);
			
			
			// clears the accounts file
			PrintWriter accountWriter = new PrintWriter("accounts.txt");
			accountWriter.close();
			
			// write the users map to the file
			FileOutputStream accountFile = new FileOutputStream("accounts.txt");
			ObjectOutputStream accountStream = new ObjectOutputStream(accountFile);
			accountStream.writeObject(accounts);
			
			PrintWriter countWriter = new PrintWriter("counts.txt");
			countWriter.write(Integer.toString(Customer.getCustomerCount()) + "\n");
			countWriter.write(Integer.toString(Employee.getEmployeeCount()) + "\n");
			countWriter.write(Integer.toString(BankAccount.getCount()) + "\n");
			countWriter.write(Integer.toString(Transaction.getTransactionCount()) + "\n");
		} catch (Exception e) {
			
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
						try {
							// try to login if the username exists in the map of users
							if (Server.users.get(m.getUser().getUsername()) != null) {
								
								Server.users.get(m.getUser().getUsername()).tryLogin(
										m.getUser().getUsername(), m.getUser().getPassword());
								
							}
							// if no exception has been thrown, we are logged in successfully
							
							// set current user to the user we logged in as
							user = Server.users.get(m.getUser().getUsername());
							
							// reply with a success message
							Message reply = new Message(MessageType.Success,
									Server.users.get(m.getUser().getUsername()));
							out.writeObject(reply);
							// the reply includes the user we logged in as so that the client will know
							// if we are a customer or an employee and be able to use the correct gui
						} catch (Exception e) {
							// if an exception is thrown by tryLogin, the login has failed
							Message reply = new Message(MessageType.Fail);
							out.writeObject(reply);
						}
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
