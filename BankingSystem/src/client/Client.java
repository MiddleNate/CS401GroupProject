package client;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.junit.jupiter.params.shadow.com.univocity.parsers.common.input.EOFException;

import shared.*;

public class Client {
	
	private static Socket connection;
	private static AtomicBoolean exiting = new AtomicBoolean(false);
	private static AtomicBoolean showTransactions = new AtomicBoolean(false);
	private static AtomicInteger accToCheck = new AtomicInteger(0);
	private static User currentUser = null;
	private static ObjectInputStream in;
	private static ObjectOutputStream out;

	public static void main(String[] args) {

		
		// screen to input port and host
		JFrame frame = new JFrame("Connect");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 130);
		frame.setLayout(new GridLayout(4, 2, 2, 2));
		frame.setLocationRelativeTo(null);
		
		// text fields
		JTextField hostTxt = new JTextField(15);
		JTextField portTxt = new JTextField("7855", 15);
		JButton connectBtn = new JButton("Connect");
		
		// labels
		frame.add(new JLabel("Host: "));
		frame.add(new JLabel("Port: "));
		frame.add(hostTxt);
		frame.add(portTxt);
		frame.add(connectBtn);
		frame.setVisible(true);
			
		connectBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String host = hostTxt.getText();
				String port = portTxt.getText();
				
				//check if text fields are empty
				if (host.isEmpty() || port.isEmpty()) {
					JOptionPane.showMessageDialog(frame, "Host/Port is empty");
					return;
				}
				
				// check if port is a number
				try {
					Integer.parseInt(port);
				}
				catch (NumberFormatException nf) {
					JOptionPane.showMessageDialog(frame, "Port is not a number");
					return;
				}
				
				// shows what the user is connecting to
				JOptionPane.showMessageDialog(frame, "Connecting to " + host + ":" + port);
				
				// separate threads
				new Thread(() -> {
					try {
						connection = new Socket(host, Integer.parseInt(port));
						out = new ObjectOutputStream(connection.getOutputStream());
						in = new ObjectInputStream(connection.getInputStream());
						
						// check if user is connected, if so, display login GUI
						if (connection.isConnected()) {
							JOptionPane.showMessageDialog(frame, "Connection Successful :)");
							frame.dispose();
							GUI loginGUI = new GUI();
							loginGUI.run();
							while (!exiting.get()) {
								Message response = (Message) in.readObject();
								onResponse(response, loginGUI);
							}
						}
						else {
							JOptionPane.showMessageDialog(frame, "Connection Unsuccessful :(");
						}
					
					} catch (java.net.UnknownHostException uh) {
						JOptionPane.showMessageDialog(frame, "Host not found");
					} catch (ConnectException ce) {
						JOptionPane.showMessageDialog(frame, "Port not found");
					} catch (java.net.NoRouteToHostException nrth) {
						JOptionPane.showMessageDialog(frame, "Connection refused");
					} catch (IllegalArgumentException ia) {
						JOptionPane.showMessageDialog(frame, "Port out of range");
					} catch (EOFException eof) {
						eof.printStackTrace();
					} catch (ClassNotFoundException cnf) {
						cnf.printStackTrace();
					} catch (IOException io) {
						io.printStackTrace();
					} finally {
						try {
							if (connection != null) 
								connection.close();
						} catch (Exception close) {
							System.out.println("Error closing connection: " + close);
						}
						System.exit(1);
					}
				}).start();
			}
		});
	}

	private static void onResponse(Message response, GUI gui) {
		
		User data = response.getUser();
		if(data instanceof User) {
			if(response.getUser() instanceof Customer) {
				SwingUtilities.invokeLater(() ->gui.showCustomerInterface(response.getUser()));
				currentUser = response.getUser();
				return;
			}
			else if(response.getUser() instanceof Employee) {
				SwingUtilities.invokeLater(() ->gui.showEmployeeInterface());
				currentUser = response.getUser();
				return;
			}
			else
				gui.doFailMessage("Response went wrong");
		}

		
		switch(response.getType()) {
		case Success:
			gui.doSuccessMessage(response.getText());
			break;
		case Fail :
			gui.doFailMessage(response.getText());
			break;
		case Invalid:
			gui.doInvalidMessage();
			break;
		case Info:
			if (!showTransactions.get()) {
				if (currentUser instanceof Customer) {
					SwingUtilities.invokeLater(() ->gui.updateCustomerInterface(response.getText()));
				} else if (currentUser instanceof Employee) {
					SwingUtilities.invokeLater(() ->gui.updateEmployeeInterface(response.getText()));
				}
			} else {
				ArrayList<BankAccount> accs = response.getAccounts();
				BankAccount acc = null;
				String output;
				for (int i = 0; i < accs.size(); i++) {
					if (accs.get(i).getID() == accToCheck.get()) {
						acc = accs.get(i);
					}
				}
				if (acc == null) {
					output = "Account not found for selected user";
				} else {
					output = getAccountTransactions(acc);
				}
				if (currentUser instanceof Customer) {
					SwingUtilities.invokeLater(() ->gui.updateCustomerInterface(output));
				} else if (currentUser instanceof Employee) {
					SwingUtilities.invokeLater(() ->gui.updateEmployeeInterface(output));
				}
				showTransactions.set(false);
				accToCheck.set(0);;
			}
			break;
		default:
			System.out.println("Unknown Message Type: " + response.getType());
			break;
		}
	}
	
	private static void sendLoginMessage(String username, String password) {
		try
		{
			User user = new User(username, password);
			Message msg = new Message(MessageType.Login,user);
			out.writeObject(msg);
			out.flush();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void sendLogoutMessage() {
		try {
			exiting.set(true);;
			Message msg = new Message(MessageType.Logout);
			out.writeObject(msg);
			out.flush();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// call with the current username if we are logged in as a customer
	private static void sendInfoRequestMessage(String username) {
		Message msg = new Message(MessageType.InfoRequest, new User(username, ""));
		try {
			out.writeObject(msg);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void sendTransactionMessage(Transaction action) {
		// create and send a message through the stream
		try {
			Message msg = new Message(MessageType.Transaction,action);
			out.writeObject(msg);
			out.flush();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// minimum is ignored if updating a savings account
	private static void sendUpdateAccountMessage(int id, AccountType type, double interest, double limit, double minimum) {
		Message msg = new Message(MessageType.Invalid);
		switch (type) {
		case AccountType.Checking: {
			// checking accounts cannot be modified
			return;
		}
		case AccountType.Savings: {
			SavingsAccount acc = new SavingsAccount(id, interest, limit);
			msg = new Message(MessageType.UpdateAccount, acc);
			break;
		}
		case AccountType.LineOfCredit: {
			LOCAccount acc = new LOCAccount(id, limit, interest, minimum);
			msg = new Message(MessageType.UpdateAccount, acc);
			break;
		}
		}
		try {
			out.writeObject(msg);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// parameters that don't apply to the type will be ignored
	private static void sendOpenAccountMessage(AccountType type, ArrayList<String> owners, 
			double interest, double limit, double minimum) {
		Message msg = new Message(MessageType.Invalid);
		switch (type) {
		case AccountType.Checking: {
			CheckingAccount acc = new CheckingAccount(owners);
			msg = new Message(MessageType.OpenAccount, acc);
		break;}
		case AccountType.Savings: {
			SavingsAccount acc = new SavingsAccount(owners, interest, limit);
			msg = new Message(MessageType.OpenAccount, acc);
		break;}
		case AccountType.LineOfCredit: {
			LOCAccount acc = new LOCAccount(owners, limit, interest, minimum);
			msg = new Message(MessageType.OpenAccount, acc);
		}
		}
		try {
			out.writeObject(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void sendCloseAccountMessage(int id) {
		// type does not matter for this input
		// savings is used because it has an id constructor
		SavingsAccount acc = new SavingsAccount(id, 0, 0);
		
		Message msg = new Message(MessageType.CloseAccount, acc);
		try {
			out.writeObject(msg);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void sendAddToAccountMessage(int id, String username) {
		// type does not matter for this input
		// savings is used because it has an id constructor
		SavingsAccount acc = new SavingsAccount(id, 0 , 0);
		User user = new User(username, "");
		Message msg = new Message(MessageType.AddToAccount, acc, user);
		try {
			out.writeObject(msg);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void sendRemoveFromAccountMessage(int id, String username) {
		// type does not matter for this input
		// savings is used because it has an id constructor
		SavingsAccount acc = new SavingsAccount(id, 0 , 0);
		User user = new User(username, "");
		Message msg = new Message(MessageType.RemoveFromAccount, acc, user);
		try {
			out.writeObject(msg);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void sendCreateCustomerMessage(String username, String password) {
		Customer cust = new Customer(username, password);
		Message msg = new Message(MessageType.CreateCustomer, cust);
		try {
			out.writeObject(msg);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getAccountTransactions(BankAccount acc) {
		String output = "";
		ArrayList<Transaction> transactions = acc.getTransactions();
		for (int i = 0; i < transactions.size(); i++) {
			output += transactions.get(i).toString();
		}
		return output;
	}

	private static class GUI implements Runnable {
		private CardLayout cardLayout;
		private JPanel mainPanel;
		private JFrame frame;

		public void run() {
			doLoginScreen();
			frame.add(mainPanel);
			frame.setVisible(true);
		}

		public GUI() {

			this.frame = new JFrame();
			// --- Set up frame ---
			frame.setTitle("Banking With Us");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(400, 130);
			frame.setLocationRelativeTo(null);

			// --- Create card layout ---
			cardLayout = new CardLayout();
			mainPanel = new JPanel(cardLayout);
		}

		public void doLoginScreen() {
			// --- Login Attributes ---
			// text fields for user name and password
			JTextField userNameTxt = new JTextField(15);
			JPasswordField passwordTxt = new JPasswordField(15);
			userNameTxt.setText("");
			passwordTxt.setText("");
			
			// shows the box the user puts their info in
			JPanel inputGrid = new JPanel(new GridLayout(2, 2, 2, 2));
			inputGrid.add(new JLabel("Username: "));
			inputGrid.add(new JLabel("Password: "));
			inputGrid.add(userNameTxt);
			inputGrid.add(passwordTxt);

			// --- Add both panels to the main panel ---
			JButton loginBtn = new JButton("Login");
			JPanel loginPanel = new JPanel(new BorderLayout(10, 10));
			loginPanel.add(inputGrid, BorderLayout.CENTER);
			loginPanel.add(loginBtn, BorderLayout.SOUTH);
			
			mainPanel.add(loginPanel, "LOGIN");
			
			// --- Add functionality to buttons ---
			// saves input as 2 string objects
			loginBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					sendLoginMessage(userNameTxt.getText(), new String(passwordTxt.getPassword()));
				}
			});
		}
		
		public void showCustomerInterface(User user) {
			doCustomerInterface(user);
			cardLayout.show(mainPanel,"CUSTOMER");
		}
		
		public void doCustomerInterface(User user) {
			// --- Panel 2: Client Panel with list of available transactions ---
			frame.setSize(1000,550);
			frame.setLocationRelativeTo(null);
			JPanel clientPanel = new JPanel(new GridLayout(2,2,2,2));
		    
			//4 button layout
			JButton withdrawlBtn = new JButton("Withdrawal");
			JButton depositBtn = new JButton("Deposit");
			JTextField accForTransactions = new JTextField("Account number");
			accForTransactions.setHorizontalAlignment(JTextField.CENTER);
			JButton seeTransHistoryBtn = new JButton("Transaction History");
			JButton showAccountsBtn = new JButton("Show Accounts");
			JButton backBtn = new JButton("Back");
			JButton logoutBtn = new JButton("Log out");
			
			// --- Add functions ---
			backBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					cardLayout.show(mainPanel, "CUSTOMER");
				}
			});
			depositBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					showDeposit(user.getUsername());	
				}
			});
			withdrawlBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					showWithdrawl(user.getUsername());	
				}
			});
			showAccountsBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					sendInfoRequestMessage(user.getUsername());
				}
			});
			seeTransHistoryBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						accToCheck.set(Integer.parseInt(accForTransactions.getText()));
						showTransactions.set(true);
						sendInfoRequestMessage(user.getUsername());
					} catch (NumberFormatException NaN) {
						doInvalidMessage();
					}
				}
			});
			logoutBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					sendLogoutMessage();
				}
			});
			
			// --- Add attributes ---
			clientPanel.add(showAccountsBtn);
			clientPanel.add(withdrawlBtn);
			clientPanel.add(depositBtn);
			clientPanel.add(accForTransactions);
			clientPanel.add(seeTransHistoryBtn);
			clientPanel.add(logoutBtn);
			
			mainPanel.add(clientPanel, "CUSTOMER");
		}
		
		public void showEmployeeInterface() {
			doEmployeeInterface();
			cardLayout.show(mainPanel,"EMPLOYEE");
		}
		
		public void doEmployeeInterface() {
			// --- Panel 3: Employee Panel with list of available transactions ---
			JPanel employeePanel = new JPanel(new FlowLayout());
			// --- Input for Customer Info ---
			JTextField customerUsername = new JTextField(7);
			JButton infoRequestBtn = new JButton("Submit");
			JButton logoutBtn = new JButton("Log out");

			// --- Add Attributes ---
			employeePanel.add(new JLabel("Welcome Employee"));
			employeePanel.add(customerUsername);
			employeePanel.add(infoRequestBtn);
			employeePanel.add(logoutBtn);
			
			// --- Add Button functions ---
			infoRequestBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					sendInfoRequestMessage(customerUsername.getText());
					cardLayout.show(mainPanel, "UPDATE");
				}
			});

			logoutBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					sendLogoutMessage();
				}
			});
			
			// --- Add panel to the main panel ---
			mainPanel.add(employeePanel, "EMPLOYEE");
			cardLayout.show(mainPanel, "UPDATE");

		}
		
		public void updateEmployeeInterface(String acc) {
			//display everything
			frame.setSize(1000,550);
			frame.setLocationRelativeTo(null);
			JPanel addTextArea = new JPanel(new FlowLayout());
			JTextArea displayCustomerAccounts = new JTextArea(10,5);
			displayCustomerAccounts.setEditable(false);
			JScrollPane scrollPane = new JScrollPane(displayCustomerAccounts);
			JTextField employeeUserNameTxt = new JTextField();
			displayCustomerAccounts.append(acc + '\n');
			JTextField customerUsername = new JTextField("Enter owner(s) name");
			AccountType[] accTypes = { AccountType.Checking,AccountType.Savings,AccountType.LineOfCredit};
			JComboBox<AccountType> accountDropdown = new JComboBox<>(accTypes);		
			JTextField interestTxt = new JTextField("Enter Interest");
			JTextField limitTxt = new JTextField("Enter Limit amount");
			JTextField miniDue = new JTextField("Enter minimum due");
			JTextField accountId = new JTextField("Enter account Id");

			JButton createCustomerBtn = new JButton("Create Customer");
			JButton withdrawlBtn = new JButton("Withdrawl");
			JButton depositBtn = new JButton("Deposit");
			JButton seeTransHistoryBtn = new JButton("Transaction History");
			JButton openAccountBtn = new JButton("Open Account");
			JButton closeAccountBtn = new JButton("Close Account");
			JButton backBtn = new JButton("Back");
			JButton logoutBtn = new JButton("Log out");
			
			addTextArea.add(displayCustomerAccounts);
			addTextArea.add(scrollPane);
			addTextArea.add(customerUsername);
			addTextArea.add(accountDropdown);
			addTextArea.add(interestTxt);
			addTextArea.add(limitTxt);
			addTextArea.add(accountId);
			addTextArea.add(createCustomerBtn);
			addTextArea.add(withdrawlBtn);
			addTextArea.add(depositBtn);
			addTextArea.add(seeTransHistoryBtn);
			addTextArea.add(openAccountBtn);
			addTextArea.add(closeAccountBtn);
			addTextArea.add(backBtn);
			addTextArea.add(logoutBtn);
			
			backBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					cardLayout.show(mainPanel, "EMPLOYEE");
				}
			});
			depositBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					showDeposit(employeeUserNameTxt.getText());	
				}
			});
			withdrawlBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					showWithdrawl(employeeUserNameTxt.getText());	
				}
			});
			seeTransHistoryBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						showTransactions.set(true);
						accToCheck.set(Integer.parseInt(accountId.getText()));
						sendInfoRequestMessage(customerUsername.getText());
					} catch (NumberFormatException NaN) {
						doInvalidMessage();
					}
				}
			});
			openAccountBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String input = customerUsername.getText();
					String[] parts = input.split(",");

					ArrayList<String> owners = new ArrayList<>();
					for(String p : parts) {
						owners.add(p);
					}
					try {
						if(accountDropdown.getSelectedItem() == AccountType.Savings) {

							sendOpenAccountMessage(AccountType.Savings,owners,Double.parseDouble(interestTxt.getText()),Double.parseDouble(limitTxt.getText()),0);	

						}
						else if(accountDropdown.getSelectedItem() == AccountType.LineOfCredit) {
							sendOpenAccountMessage(AccountType.LineOfCredit,owners,Double.parseDouble(interestTxt.getText()),Double.parseDouble(limitTxt.getText()), Double.parseDouble(miniDue.getText()));	

						}
						else {
							sendOpenAccountMessage(AccountType.Checking,owners,0,0,0);	
						}
					} catch (NumberFormatException NaN) {
						doInvalidMessage();
					}
				}
			});
			closeAccountBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {					
					sendCloseAccountMessage(Integer.parseInt(accountId.getText()));	
				}
			});
		    
			logoutBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					sendLogoutMessage();
				}
			});
			// --- Add panel to the main panel ---
			mainPanel.add(addTextArea, "UPDATE");
			cardLayout.show(mainPanel, "UPDATE");
		}
		
		public void updateCustomerInterface(String text) {
			JPanel addResponseText = new JPanel();
			JTextArea displayText = new JTextArea();
			displayText.setText(text);
			JButton backBtn = new JButton("Back");
			backBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					cardLayout.show(mainPanel, "CUSTOMER");
				}
			});
			addResponseText.add(displayText);
			addResponseText.add(backBtn);
			mainPanel.add(addResponseText, "UPDATE");
			cardLayout.show(mainPanel, "UPDATE");
		}
		
		public void showWithdrawl(String username) {
			doWithdrawl(username);
			cardLayout.show(mainPanel,"WITHDRAWAL");
		}
		public void doWithdrawl(String username) {
			JPanel withdrawlPanel = new JPanel(new GridLayout(3,1));
			JTextField amountTxt = new JTextField();
			JTextField bankAccTxt = new JTextField();
			JButton submitBtn = new JButton("Submit");
			JButton backBtn = new JButton("Back");

			withdrawlPanel.add(new JLabel("Enter Withdrawal Amount:"));
			withdrawlPanel.add(new JLabel("Enter Bank Account Number:"));
			withdrawlPanel.add(amountTxt);
			withdrawlPanel.add(bankAccTxt);
			withdrawlPanel.add(submitBtn);
			withdrawlPanel.add(backBtn);
			
			// --- Add Button Function ---
			submitBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						Double withdrawlAmount = Double.parseDouble(amountTxt.getText());
						User user = new User(username,null);
			    		Transaction withdrawl = new Transaction(withdrawlAmount,TransactionType.Withdrawal,user,Integer.parseInt(bankAccTxt.getText()));
			    		sendTransactionMessage(withdrawl);
					} catch (NumberFormatException NaN) {
						doInvalidMessage();
					}
				}
			});
			backBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (currentUser instanceof Customer) {
						cardLayout.show(mainPanel, "CUSTOMER");
					} else {
						cardLayout.show(mainPanel, "EMPLOYEE");
					}
				}
			});
			
    		mainPanel.add(withdrawlPanel, "WITHDRAWAL");
		}
		
		public void showDeposit(String username) {
			doDeposit(username);
			cardLayout.show(mainPanel,"DEPOSIT");
		}
		public void doDeposit(String username) {
			JPanel depositPanel = new JPanel(new GridLayout(3,1));
			JTextField amountTxt = new JTextField();
			JTextField bankAccTxt = new JTextField();
			JButton submitBtn = new JButton("Submit");
			JButton backBtn = new JButton("Back");
			depositPanel.add(new JLabel("Enter Deposit Amount:"));
			depositPanel.add(new JLabel("Enter Bank Account Number:"));
			depositPanel.add(amountTxt);
			depositPanel.add(bankAccTxt);
			depositPanel.add(submitBtn);
			depositPanel.add(backBtn);
			
			// --- Add Button Function ---
			submitBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						Double depositAmount = Double.parseDouble(amountTxt.getText());
						User user = new User(username,null);
			    		Transaction deposit = new Transaction(depositAmount,TransactionType.Deposit,user,Integer.parseInt(bankAccTxt.getText()));
			    		sendTransactionMessage(deposit);
					} catch (NumberFormatException NaN) {
						doInvalidMessage();
					}	
				}
			});
			backBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (currentUser instanceof Customer) {
						cardLayout.show(mainPanel, "CUSTOMER");
					} else {
						cardLayout.show(mainPanel, "EMPLOYEE");
					}
				}
			});
    		
    		mainPanel.add(depositPanel, "DEPOSIT");
		}		
		public void doSuccessMessage(String showTxt) {
			JOptionPane.showMessageDialog(null, '"' + showTxt + '"');
		}

		public void doFailMessage(String showTxt) {
			JOptionPane.showMessageDialog(null, '"' + showTxt + '"');
		}

		public void doInvalidMessage() {
			JOptionPane.showMessageDialog(null, "Invalid Entry, Try Again");
		}

		public void doAccountUpdatedMessage() {
			JOptionPane.showMessageDialog(null, "Account Updated Successfully");
		}
	}
}
