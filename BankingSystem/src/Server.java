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
			
			scan.close();
			
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
			
			userFile.close();
			accountFile.close();
			userStream.close();
			accountStream.close();
			countWriter.close();
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
						Message reply;
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
							reply = new Message(MessageType.Success, user);
							// the reply includes the user we logged in as so that the client will know
							// if we are a customer or an employee and be able to use the correct gui
						} catch (Exception e) {
							// if an exception is thrown by tryLogin, the login has failed
							reply = new Message(MessageType.Fail, e);
						}
						out.writeObject(reply);
						// await next message
						continue;
					}
					
					// if we are logged in as a customer, accept inforequests and transactions only
					if (user != null && user instanceof Customer) {
						switch (m.getType()) {
						case MessageType.Logout: {
							loggingout = true;
							break; }
						case MessageType.InfoRequest: {
							ArrayList<BankAccount> accounts = new ArrayList<BankAccount>();
							
							// for each of the account ids in the customer
							for (int i = 0; i < ((Customer)user).getAccounts().size(); i++) {
								// add the account to the arraylist we will send from the map 
								accounts.add(Server.accounts.get(((Customer)user).getAccounts().get(i)));
							}
							
							Message reply = new Message(MessageType.Info, accounts);
							out.writeObject(reply);
							break; }
						case MessageType.Transaction: {
							Message reply = null;
							
							// check that the account specified in the message
							// is in the list of accounts owned by the current user
							if (!((Customer)user).getAccounts().contains(m.getTransaction().getAccount().getID())) {
								reply = new Message(MessageType.Fail, new Exception("You do not have access to that account"));
							} else {
								try {
									// pass the message to the account to be tried
									Server.accounts.get(m.getTransaction().getAccount().getID()).tryTransaction(m.getTransaction());
									// if no exception was thrown, the transaction was successful
									// so we can set the reply to success
									reply = new Message(MessageType.Success);
								} catch (Exception e) {
									reply = new Message(MessageType.Fail, e);
								}
							}
							
							// send the reply
							out.writeObject(reply);
							break; }
						default:
							// do nothing for other message types
							break;
						}
					}
					
					// if we are logged in as an employee, accept inforequests, transactions, and account operations
					if (user != null && user instanceof Employee) {
						switch (m.getType()) {
						case MessageType.Logout: {
							loggingout = true;
							break; }
						case MessageType.InfoRequest: {
							Message reply = null;
							// check that the user provided in the inforequest exists
							if (Server.users.containsKey(m.getUser().getUsername())) {
								Customer tempCustomer = (Customer) Server.users.get(m.getUser().getUsername());
								ArrayList<BankAccount> accounts = new ArrayList<BankAccount>();
								
								// for each of the account ids in the customer
								for (int i = 0; i < tempCustomer.getAccounts().size(); i++) {
									// add the account to the arraylist we will send from the map 
									accounts.add(Server.accounts.get(((Customer)user).getAccounts().get(i)));
								}
								
								reply = new Message(MessageType.Info, accounts);
							} else {
								reply = new Message(MessageType.Fail, new Exception("User does not exist"));
							}
							out.writeObject(reply);
							break; }
						case MessageType.Transaction: {
							Message reply = null;
							
							// check that the account specified in the message
							// is an account that exists
							if (Server.accounts.containsKey(m.getTransaction().getAccount().getID())) {
								reply = new Message(MessageType.Fail, new Exception("Account does not exist"));
							} else {
								try {
									// pass the message to the account to be tried
									Server.accounts.get(m.getTransaction().getAccount().getID()).tryTransaction(m.getTransaction());
									// if no exception was thrown, the transaction was successful
									// so we can set the reply to success
									reply = new Message(MessageType.Success);
								} catch (Exception e) {
									reply = new Message(MessageType.Fail, e);
								}
							}
							
							// send the reply
							out.writeObject(reply);
							break; }
						case MessageType.CreateCustomer: {
							Message reply = null;
							
							// check that the username does not already exist in the user map
							if (Server.users.containsKey(m.getUser().getUsername())) {
								reply = new Message(MessageType.Fail, new Exception("Account already exists"));
							} else {
								// add the user to the map, and set the reply to a success
								Server.users.put(m.getUser().getUsername(), m.getUser());
								reply = new Message(MessageType.Success);
							}
							
							out.writeObject(reply);
							break; }
						case MessageType.OpenAccount: {
							Message reply = null;
							
							switch (m.getAccount().getType()) {
							case AccountType.Checking: {
								// check that the provided owners exist
								for (int i = 0; i < m.getAccount().getOwners().size(); i++) {
									if (!Server.users.containsKey(m.getAccount().getOwners().get(i))) {
										reply = new Message(MessageType.Fail, new Exception("Owner does not exist"));
										break;
									}
								}

								// put the new account if everything was valid
								// a new object is created so that the id is set server-side
								CheckingAccount newAcc = new CheckingAccount(m.getAccount().getOwners());
								Server.accounts.put(newAcc.getID(), newAcc);
								
								reply = new Message(MessageType.Success);
								break; }
							case AccountType.Savings: {
								// check that the provided owners exist
								for (int i = 0; i < m.getAccount().getOwners().size(); i++) {
									if (!Server.users.containsKey(m.getAccount().getOwners().get(i))) {
										reply = new Message(MessageType.Fail, new Exception("Owner does not exist"));
										break;
									}
								}
								
								double interest = ((SavingsAccount) m.getAccount()).getInterest();
								double limit = ((SavingsAccount) m.getAccount()).getWithdrawlLimit();
								
								// check that interest and limit are positive
								if (interest < 0 || limit < 0) {
									reply = new Message(MessageType.Fail, new Exception("Values must be above zero"));
									break;
								}
								
								// put the new account if everything was valid
								// a new object is created so that the id is set server-side
								SavingsAccount newAcc = new SavingsAccount(m.getAccount().getOwners(), interest, limit);
								Server.accounts.put(newAcc.getID(), newAcc);
								
								reply = new Message(MessageType.Success);
								break ;}
							case AccountType.LineOfCredit:  {
								// check that the provided owners exist
								for (int i = 0; i < m.getAccount().getOwners().size(); i++) {
									if (!Server.users.containsKey(m.getAccount().getOwners().get(i))) {
										reply = new Message(MessageType.Fail, new Exception("Owner does not exist"));
										break;
									}
								}
								
								double interest = ((LOCAccount) m.getAccount()).getInterest();
								double limit = ((LOCAccount) m.getAccount()).getLimit();
								double minimum = ((LOCAccount) m.getAccount()).getMinimumDue();
								
								
								// check that interest, limit, and minimum due are positive
								if (interest < 0 || limit < 0 || minimum < 0) {
									reply = new Message(MessageType.Fail, new Exception("Valies must be above zero"));
									break;
								}
								
								// put the new account if everything was valid
								// a new object is created so that the id is set server-side
								LOCAccount newAcc = new LOCAccount(m.getAccount().getOwners(), limit, interest, minimum);
								Server.accounts.put(newAcc.getID(), newAcc);
								
								reply = new Message(MessageType.Success);
								break ;}
								
							}
							
							out.writeObject(reply);
							break; }
						case MessageType.CloseAccount: {
							Message reply = null;
							
							// check that the account exists
							if (!Server.accounts.containsKey(m.getAccount().getID())) {
								reply = new Message(MessageType.Fail, new Exception("Account does not exist"));
							} else {
								try {
									// try to close the account
									Server.accounts.get(m.getAccount().getID()).closeAccount();
									reply = new Message(MessageType.Success);
								} catch (Exception e) {
									// something went wrong if closeaccount throws
									reply = new Message(MessageType.Fail, e);
								}
							}
							
							out.writeObject(reply);
							break; }
						case MessageType.UpdateAccount: {
							Message reply = null;
							int id = m.getAccount().getID();
							
							// check that the account exists and type matches
							if (!Server.accounts.containsKey(id) || Server.accounts.get(id).getType() != m.getAccount().getType()) {
								reply = new Message(MessageType.Fail, new Exception("Account of that type does not exist"));
							} else {
								switch (m.getAccount().getType()) {
								case AccountType.Checking: {
									// checking accounts have no fields to be modified, so it fails
									reply = new Message(MessageType.Fail, new Exception("Checking accounts cannot be modified"));
									break; }
								case AccountType.Savings: {
									// validate the fields that will be modified
									double interest = ((LOCAccount) m.getAccount()).getInterest();
									double limit = ((LOCAccount) m.getAccount()).getLimit();
									if (interest < 0 || limit < 0) {
										reply = new Message(MessageType.Fail, new Exception("Values must be above zero"));
										break;
									}
									
									((SavingsAccount) Server.accounts.get(id)).setInterest(interest);
									((SavingsAccount) Server.accounts.get(id)).setWithdrawlLimit(limit);
									reply = new Message(MessageType.Success);
									break; }
								case AccountType.LineOfCredit: {
									// validate the fields that will be modified
									double interest = ((LOCAccount) m.getAccount()).getInterest();
									double limit = ((LOCAccount) m.getAccount()).getLimit();
									double minimum = ((LOCAccount) m.getAccount()).getMinimumDue();
									if (interest < 0 || limit < 0 || minimum < 0) {
										reply = new Message(MessageType.Fail, new Exception ("Values must be above zero"));
										break;
									}
									
									((LOCAccount) Server.accounts.get(id)).setInterest(interest);
									((LOCAccount) Server.accounts.get(id)).setCreditLimit(limit);
									((LOCAccount) Server.accounts.get(id)).setMinimumDue(minimum);
									reply = new Message(MessageType.Success);
									break;}
								}
								
								out.writeObject(reply);
							}
							break; }
						case MessageType.AddToAccount: {
							Message reply = null;
							
							// validate that the account exists and the user exists and account is not closed
							if (!Server.accounts.containsKey(m.getAccount().getID())) {
								reply = new Message(MessageType.Fail, new Exception("Account does not exist"));
							} else if (!Server.users.containsKey(m.getUser().getUsername())) {
								reply = new Message(MessageType.Fail, new Exception("User does not exist"));
							} else if (Server.accounts.get(m.getAccount().getID()).isOpen()) {
								reply = new Message(MessageType.Fail, new Exception("Account is closed"));
							} else {
								Server.accounts.get(m.getAccount().getID()).addUser(m.getUser().getUsername());
								reply = new Message(MessageType.Success);
							}
							
							out.writeObject(reply);
						break; }
						case MessageType.RemoveFromAccount: {
							Message reply = null;
							
							// validate that the account exists, the user exists, the account is not closed
							if (!Server.accounts.containsKey(m.getAccount().getID())) {
								reply = new Message(MessageType.Fail, new Exception("Account does not exist"));
							} else if (!Server.users.containsKey(m.getUser().getUsername())) {
								reply = new Message(MessageType.Fail, new Exception("User does not exist"));
							} else if (Server.accounts.get(m.getAccount().getID()).isOpen()) {
								reply = new Message(MessageType.Fail, new Exception("Account is closed"));
							} else {
								// this will still return a success if the user was not on the account to begin with
								Server.accounts.get(m.getAccount().getID()).removeOwner(m.getUser().getUsername());
								reply = new Message(MessageType.Success);
							}
							
							out.writeObject(reply);
						break; }
						default:
							// do nothing for other message types
							break;
						}
					}
				}
				
			} catch (Exception e) {
				// this will catch exceptions with the inputstream only
				
			} finally {
				// make sure to logout if we are logged in
				if (user != null) Server.users.get(user.getUsername()).logout();
				
				// close resources
				try {
					out.close();
					in.close();
					client.close();
				} catch (Exception e) {
					System.out.println("Error closing resources: " + e);
				}
			}
		}
	}

}
