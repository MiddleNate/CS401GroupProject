package server;
import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import shared.*;

import java.io.*;
import java.lang.Integer;

public class Server {
	protected static Map<Integer, BankAccount> accounts;
	// access within ClientHandler using Server.accounts
	
	protected static Map<String, User> users;
	// access within ClientHandler using Server.users
	
	protected static AtomicBoolean running = new AtomicBoolean(true);
	// used to stop the server from a separate thread that blocks until console input
	
	
	public static void main(String[] args) {
		// loading users and accounts into memory
		try {
			// load hashmap of users from file (key is username)
			FileInputStream userFile = new FileInputStream("users.txt");
			ObjectInputStream userStream = new ObjectInputStream(userFile);
			users = (Map<String, User>) userStream.readObject();
			// convert it to a synchronized map (prevents multithreading issues)
			users = Collections.synchronizedMap(users);
			
			// load hashmap of accounts from file (key is account id)
			FileInputStream accountFile = new FileInputStream("accounts.txt");
			ObjectInputStream accountStream = new ObjectInputStream(accountFile);
			accounts = (Map<Integer, BankAccount>) accountStream.readObject();
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
			
			Scanner scan = new Scanner(System.in);
			
			// listener to set the running flag when something has been entered in the console
			Stopper stopper = new Stopper();
			new Thread(stopper).start();
			
			// while running, accept connections and create
			// a handler in its own thread for each of them
			while (running.get()) {
				// exit the server if anything has been entered into the console (as detected by stopper)
				
				Socket client = server.accept();
				
				// doesn't start a new thread if a stop message has been sent
				if (!running.get()) break;
				
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
			System.out.println("Saving error: " + e);
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
						Message reply = null;
						try {
							// try to login if the username exists in the map of users
							if (Server.users.containsKey(m.getUser().getUsername())) {
								
								Server.users.get(m.getUser().getUsername()).tryLogin(
										m.getUser().getUsername(), m.getUser().getPassword());
								
							} else {
								throw new Exception("Username not found");
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
						// skip to next loop so it doesn't try to handle the message twice
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
							Integer accountID = m.getTransaction().getAccount().getID();
							
							// check that the account specified in the message
							// is in the list of accounts owned by the current user
							if (!((Customer)user).getAccounts().contains(accountID)) {
								reply = new Message(MessageType.Fail, new Exception("You do not have access to that account"));
							} else {
								try {
									// pass the message to the account to be tried
									Server.accounts.get(accountID).tryTransaction(m.getTransaction(), user);
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
							Integer accountID = m.getTransaction().getID();
							
							// check that the account specified in the message
							// is an account that exists
							if (Server.accounts.containsKey(accountID)) {
								reply = new Message(MessageType.Fail, new Exception("Account does not exist"));
							} else {
								try {
									// pass the message to the account to be tried
									Server.accounts.get(accountID).tryTransaction(m.getTransaction(), user);
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
								reply = new Message(MessageType.Fail, new Exception("Customer already exists"));
							} else if (!(m.getUser() instanceof Customer)) {
								reply = new Message(MessageType.Fail, new Exception("Provided User was not of type Customer"));
							} else {
								Customer newCust = (Customer) m.getUser();
								// add the user to the map, data is copied to ensure we
								// use the server-side customerCount (not that customer id gets
								// used anywhere since users is mapped to the username)
								Server.users.put(newCust.getUsername(), new Customer(
										newCust.getUsername(),
										newCust.getPassword(),
										newCust.getCustomerName(),
										newCust.getSocialSecNumber()));
								// set the reply to success
								reply = new Message(MessageType.Success);
							}
							
							out.writeObject(reply);
							break; }
						case MessageType.OpenAccount: {
							Message reply = null;
							
							switch (m.getAccount().getType()) {
							case AccountType.Checking: {
								// check that the provided owners exist and are type Customer
								ArrayList<String> owners = m.getAccount().getOwners();
								for (int i = 0; i < owners.size(); i++) {
									if (!(Server.users.get(owners.get(i)) instanceof Customer)) {
										reply = new Message(MessageType.Fail, new Exception("Owner does not exist"));
										break;
									}
								}

								// put the new account if everything was valid
								// a new object is created so that the id is set server-side
								CheckingAccount newAcc = new CheckingAccount(owners);
								Server.accounts.put(newAcc.getID(), newAcc);
								// add the new account number to the owner's records
								for (int i = 0; i < owners.size(); i++) {
									((Customer) Server.users.get(owners.get(i))).addAccount(newAcc.getID());
								}
								reply = new Message(MessageType.Success);
								break; }
							case AccountType.Savings: {
								// check that the provided owners exist and are type Customer
								ArrayList<String> owners = m.getAccount().getOwners();
								for (int i = 0; i < owners.size(); i++) {
									if (!(Server.users.get(owners.get(i)) instanceof Customer)) {
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
								SavingsAccount newAcc = new SavingsAccount(owners, interest, limit);
								Server.accounts.put(newAcc.getID(), newAcc);
								// add the new account number to the owner's records
								for (int i = 0; i < owners.size(); i++) {
									((Customer) Server.users.get(owners.get(i))).addAccount(newAcc.getID());
								}
								reply = new Message(MessageType.Success);
								break ;}
							case AccountType.LineOfCredit:  {
								// check that the provided owners exist and are type Customer
								ArrayList<String> owners = m.getAccount().getOwners();
								for (int i = 0; i < owners.size(); i++) {
									if (!(Server.users.get(owners.get(i)) instanceof Customer)) {
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
								LOCAccount newAcc = new LOCAccount(owners, limit, interest, minimum);
								Server.accounts.put(newAcc.getID(), newAcc);
								// add the new account number to the owner's records
								for (int i = 0; i < owners.size(); i++) {
									((Customer) Server.users.get(owners.get(i))).addAccount(newAcc.getID());
								}
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
									// remove the account number from the owner's records
									BankAccount closedAccount = Server.accounts.get(m.getAccount().getID());
									ArrayList<String> owners = closedAccount.getOwners();
									for (int i = 0; i < owners.size(); i++) {
										((Customer) Server.users.get(owners.get(i))).removeAccount(closedAccount.getID());
									}
									reply = new Message(MessageType.Success);
								} catch (Exception e) {
									// the account cannot be closed if closedaccount throws
									reply = new Message(MessageType.Fail, e);
								}
							}
							
							out.writeObject(reply);
							break; }
						case MessageType.UpdateAccount: {
							Message reply = null;
							int accountID = m.getAccount().getID();
							
							// check that the account exists and type matches
							if (!Server.accounts.containsKey(accountID) || Server.accounts.get(accountID).getType() != m.getAccount().getType()) {
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
									
									((SavingsAccount) Server.accounts.get(accountID)).setInterest(interest);
									((SavingsAccount) Server.accounts.get(accountID)).setWithdrawlLimit(limit);
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
									
									((LOCAccount) Server.accounts.get(accountID)).setInterest(interest);
									((LOCAccount) Server.accounts.get(accountID)).setCreditLimit(limit);
									((LOCAccount) Server.accounts.get(accountID)).setMinimumDue(minimum);
									reply = new Message(MessageType.Success);
									break;}
								}
								
								out.writeObject(reply);
							}
							break; }
						case MessageType.AddToAccount: {
							Message reply = null;
							Integer accountID = m.getAccount().getID();
							String username = m.getUser().getUsername();
							// validate that the account exists, the user exists and is type Customer, and account is not closed
							if (!Server.accounts.containsKey(accountID)) {
								reply = new Message(MessageType.Fail, new Exception("Account does not exist"));
							} else if (!(Server.users.get(username) instanceof Customer)) {
								reply = new Message(MessageType.Fail, new Exception("Customer does not exist"));
							} else if (Server.accounts.get(accountID).isOpen()) {
								reply = new Message(MessageType.Fail, new Exception("Account is closed"));
							} else {
								Server.accounts.get(accountID).addUser(username);
								((Customer) Server.users.get(m.getUser().getUsername())).addAccount(m.getAccount().getID());
								reply = new Message(MessageType.Success);
							}
							
							out.writeObject(reply);
						break; }
						case MessageType.RemoveFromAccount: {
							Message reply = null;
							Integer accountID = m.getAccount().getID();
							String username = m.getUser().getUsername();
							// validate that the account exists, the user exists and is type Customer,
							// the user is on the account, and the account is not closed
							if (!Server.accounts.containsKey(accountID)) {
								reply = new Message(MessageType.Fail, new Exception("Account does not exist"));
							} else if (!(Server.users.get(username) instanceof Customer)) {
								reply = new Message(MessageType.Fail, new Exception("Customer does not exist"));
							} else if (Server.accounts.get(accountID).isOpen()) {
								reply = new Message(MessageType.Fail, new Exception("Account is closed"));
							} else if (!Server.accounts.get(accountID).getOwners().contains(username)) {
								reply = new Message(MessageType.Fail, new Exception("Customer is not an account owner"));
							}
							else {
								Server.accounts.get(accountID).removeOwner(username);
								((Customer) Server.users.get(m.getUser().getUsername())).removeAccount(m.getAccount().getID());
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
				System.out.println("ClientHandler Error: " + e);
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
	
	private static class Stopper implements Runnable {
		public void run() {
			Scanner scan = new Scanner(System.in);
			// scan.hasNext() blocks until something is entered in the console, then sets the boolean
			// so that the main will exit the loop the next time it accepts a connection
			if (scan.hasNext()) Server.running.set(false);
			System.out.println("Exiting");
			// after setting the boolean to false the server sends a new connection to ITSELF
			// so that it stops blocking on ServerSocket.accept(), and breaks out of the loop
			// because the flag has been changed
			try {
				Socket self = new Socket("localhost", 7855);
				self.close();
			} catch (Exception e) {
				System.out.println(e);
			}
			scan.close();
		}
	}

}
