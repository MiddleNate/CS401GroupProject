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

		GUI gui = new GUI();
		new Thread(gui).start();

		while (!exiting) {
			response = (Message) in.readObject();

			// listen for replies, put the reply in response, redraw gui
			switch(response.getType()) {
				case Login: 
					gui.doLoginScreen();
					break;
				case Logout:
					//method
					break;
				case InfoRequest:
					//method
					break;
				case Info:
					//method
					break;
				case Transaction:
					gui.doTransactionMessage();
					break;
				case Success:
					gui.doSuccessMessage();
					break;
				case Fail :
					gui.doFailMessage();
					break;
				case CreateCustomer:
					//method
					break;
				case OpenAccount:
					//method
					break;
				case CloseAccount:
					//method
					break;
				case UpdateAccount:
					//method
					break;
				case AddToAccount:
					//method
					break;
				case RemoveFromAccount:
					//method
					break;
				case Invalid:
					gui.doInvalidMessage();
					break;
				default:
					System.out.println("Unknown Message Type: " + response.getType());
					break;
			}
		}
		
		try {
			connection = new Socket("localhost", 7855);
			in = new ObjectInputStream(connection.getInputStream());
			out = new ObjectOutputStream(connection.getOutputStream());
			
		while (!exiting) {
			Message response = (Message) in.readObject();
			response(response, gui);
		}
		
		} catch (EOFException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				connection.close();
			} catch (Exception e) {
				System.out.println("Error closing connection: " + e);
			} 
		}
	}

	private static void response(Message response, GUI gui) {
		switch(response.getType()) {
		case Login: 
			gui.doLoginScreen();
			break;
		case Logout:
			//method
			break;
		case InfoRequest:
			//method
			break;
		case Info:
			//method
			break;
		case Transaction:
			gui.doTransactionMessage();
			break;
		case Success:
			gui.doSuccessMessage();
			break;
		case Fail :
			gui.doFailMessage();
			break;
		case CreateCustomer:
			//method
			break;
		case OpenAccount:
			//method
			break;
		case CloseAccount:
			//method
			break;
		case UpdateAccount:
			//method
			break;
		case AddToAccount:
			//method
			break;
		case RemoveFromAccount:
			//method
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
	//TODO : Add Parameters
	private static void sendTransactionMessage(String username, String password) {
		// create and send a message through the stream
		try {
			User user = new User(username,password);
			Message msg = new Message(MessageType.Transaction,user);
			out.writeObject(msg);
			out.flush();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	//TODO : Add parameters
	private static void sendLogoutMessage() {
		try {
			User user = new User(username,password);
			Message msg = new Message(MessageType.Logout);
			out.writeObject(msg);
			out.flush();
		}catch(Exception e) {
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

			// --- Panel 2: Client Panel with list of available transactions ---
			JPanel clientPanel = new JPanel();
			clientPanel.setLayout(cardLayout);
			frame.setLayout(new BorderLayout());
			JLabel greetingLabel = new JLabel("Welcome");
			tAOutput = new TextArea(5,50); // allocate TextField
		    tAOutput.setEditable(false);  // read-only
		    
		    // --- Types of Transactions for Customer
		    JButton withdrawlBtn = new JButton("Withdraw");
		    JButton depositBtn = new JButton("Deposit");
		    JButton seeTransactionHistoryBtn = new JButton("View Transaction History");
		    
		    
			doTransactionMessage();
			
			// --- Panel 3: Employee Panel with list of available transactions ---
			JPanel employeePanel = new JPanel();
			employeePanel.setLayout(cardLayout);
			
			// --- Add both panels to the main panel ---
			mainPanel.add(clientPanel, "CLIENT");
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

		public void doTransactionMessage() {
			//GUI Interface
			mainPanel.setLayout(new GridLayout());
			JPanel upperPanel = new JPanel();
			
			mainPanel.add(upperPanel);
			
			sendTransactionMessage(null, null);
		}
		
		public void doSuccessMessage() {
			JOptionPane.showMessageDialog(null, "Successfully accessed");
		}

		public void doFailMessage() {
			JOptionPane.showMessageDialog(null, "Failed, Try Again or Logging Out");
		}

		public void doInvalidMessage() {
			JOptionPane.showMessageDialog(null, "Invalid Entry, Try Again");
		}

		public void doAccountUpdatedMessage() {
			JOptionPane.showMessageDialog(null, "Account Updated Successfully");
		}
	}
}
