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
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.junit.jupiter.params.shadow.com.univocity.parsers.common.input.EOFException;

import shared.*;

public class Client {
	
	private static Socket connection;
	private static boolean exiting = false;
	private static ObjectInputStream in;
	private static ObjectOutputStream out;

	public static void main(String[] args) {
		// screen to input port and host
		GUI connectionGUI = new GUI();
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
						in = new ObjectInputStream(connection.getInputStream());
						out = new ObjectOutputStream(connection.getOutputStream());
						
						// check if user is connected, if so, display login GUI
						if (connection.isConnected()) {
							JOptionPane.showMessageDialog(frame, "Connection Successful :)");
							frame.dispose();
							GUI loginGUI = new GUI();
							loginGUI.run();
							while (!exiting) {
								Message response = (Message) in.readObject();
								onResponse(response, loginGUI);
							}
						}
						else {
							JOptionPane.showMessageDialog(frame, "Connection Unsuccessful :(");
						}
					
					} catch (java.net.UnknownHostException uh) {
						JOptionPane.showMessageDialog(frame, "Host not found");
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
					}
				}).start();
			}
		});
	}

	private static void onResponse(Message response, GUI gui) {
		
		User data = response.getUser();
		if(data instanceof User) {
			if(response.getUser() instanceof Customer) {
				SwingUtilities.invokeLater(() ->gui.showCustomerInterface());
				return;
			}
			else if(response.getUser() instanceof Employee) {
				SwingUtilities.invokeLater(() ->gui.showEmployeeInterface());
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
		default:
			System.out.println("Unknown Message Type: " + response.getType());
			break;
		}
	}
	
	private static void sendLoginMessage(String username, String password) {
		// create and send a message through the stream
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
	
	private static void sendLogoutMessage(String username, String password) {
		try {
			User user = new User(username,password);
			Message msg = new Message(MessageType.Logout,user);
			out.writeObject(msg);
			out.flush();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	//TODO : Add Parameters
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

	// more methods for sending other message types

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
		public void showCustomerInterface() {
			doCustomerInterface();
			cardLayout.show(mainPanel,"CUSTOMER");
		}
		public void doCustomerInterface() {
			// --- Panel 2: Client Panel with list of available transactions ---
			JPanel clientPanel = new JPanel(new GridLayout(2,2,2,2));
		    
			//4 button layout
			JButton withdrawlBtn = new JButton("Withdrawal");
			JButton depositBtn = new JButton("Deposit");
			JButton seeTransHistoryBtn = new JButton("See Transaction History");
			
			JButton logoutBtn = new JButton("Log out");
			
			// --- Add functions ---
			
			// --- Add attributes ---
			clientPanel.add(new JLabel("Welcome"));
			clientPanel.add(withdrawlBtn);
			clientPanel.add(seeTransHistoryBtn);
			clientPanel.add(depositBtn);
			clientPanel.add(logoutBtn);
			
			mainPanel.add(clientPanel, "CUSTOMER");
		}
		public void showEmployeeInterface() {
			doEmployeeInterface();
			cardLayout.show(mainPanel,"EMPLOYEE");
		}
		public void doEmployeeInterface() {
			// --- Panel 3: Employee Panel with list of available transactions ---
			JPanel employeePanel = new JPanel();
			employeePanel.setLayout(cardLayout);
			
			JButton withdrawlBtn = new JButton();
			JButton depositBtn = new JButton();
			JButton seeTransHistoryBtn = new JButton();
			JButton openOrCloseAccountbtn = new JButton();
			JButton seeBankAccountsbtn = new JButton();
			JButton logoutBtn = new JButton();
			// --- Add panel to the main panel ---
			mainPanel.add(employeePanel, "EMPLOYEE");
		}
		
		public void doOpenOrCloseAccount() {
			JPanel openCloseAccount = new JPanel();
			openCloseAccount.setLayout(cardLayout);

			mainPanel.add(openCloseAccount);

		}

		public void doBankAccounts() {
			// --- Attributes ---
			// --- Text Area : Read Only --- 
			TextArea tAOutput;
			tAOutput = new TextArea(5,50); // allocate TextField
		    tAOutput.setEditable(false);  // read-only
		    
		}

		public void doBankAccountDetails() {
			// --- Attributes ---
			// --- Text Area : Read Only --- 
			TextArea tAOutput;
			tAOutput = new TextArea(5,50); // allocate TextField
		    tAOutput.setEditable(false);  // read-only
		}

		public void doTransactionMessage(User user) {
			//GUI Interface
			mainPanel.setLayout(new GridLayout());
			JPanel upperPanel = new JPanel();
			//
			JLabel greetingLabel = new JLabel("Welcome Customer");  

			JButton withdrawlBtn = new JButton("Withdraw");
		    JButton depositBtn = new JButton("Deposit");
		    JButton seeTransactionHistoryBtn = new JButton("View Transaction History");
		    
		    // --- button functionalities ---
		    // create new transaction --> create new Message & send it
		    withdrawlBtn.addActionListener(new ActionListener() {
		    	public void actionPerformed(ActionEvent e) {
		    		doWithdrawl();

		    	}

				private void doWithdrawl() {
					TextField amountTxt = new TextField();
					TextField bankAccTxt = new TextField();
					JButton submitBtn = new JButton();
					// TODO: add to panel
					add(amountTxt,BorderLayout.CENTER);
					add(bankAccTxt, BorderLayout.SOUTH);
					
					Double withdrawlAmount = Double.parseDouble(amountTxt.getText());
					
		    		// TODO : change text field to a number or add error checking for parseint
		    		Transaction withdrawl = new Transaction(withdrawlAmount,TransactionType.Withdrawal,user,Integer.parseInt(bankAccTxt.getText()));
		    		sendTransactionMessage(withdrawl);
				}
		    });
		    depositBtn.addActionListener(new ActionListener() {
		    	public void actionPerformed(ActionEvent e) {
		    		TextField amountTxt = new TextField();
					TextField bankAccTxt = new TextField();
					// TODO: add to panel
					add(amountTxt,BorderLayout.CENTER);
					add(bankAccTxt, BorderLayout.SOUTH);
					
					Double depositAmount = Double.parseDouble(amountTxt.getText());
					
		    		// TODO : change text field to a number or add error checking for parseint
		    		Transaction deposit = new Transaction(depositAmount,TransactionType.Deposit,user,Integer.parseInt(bankAccTxt.getText()));
		    		sendTransactionMessage(deposit);
		    	}
		    });
		    seeTransactionHistoryBtn.addActionListener(new ActionListener() {
		    	public void actionPerformed(ActionEvent e) {
					TextField bankAccTxt = new TextField();
					// TODO: add to panel
					add(Integer.parseInt(bankAccTxt.getText()), BorderLayout.SOUTH);
					
		    	}
		    });
			mainPanel.add(upperPanel);
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
