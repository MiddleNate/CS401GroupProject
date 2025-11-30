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
		
		try {
			connection = new Socket("localhost", 7855);
			in = new ObjectInputStream(connection.getInputStream());
			out = new ObjectOutputStream(connection.getOutputStream());
			
		while (!exiting) {
			Message response = (Message) in.readObject();
			onResponse(response, gui);
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

	private static void onResponse(Message response, GUI gui) {
		switch(response.getType()) {
		case Success:
			System.out.println("Swag" + "= " + response);
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
			Message msg = new Message(MessageType.Logout);
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
//	private static void sendInfoRequestMessage(User user) {
//		try {
//			Message msg = new Message(MessageType.InfoRequest,user);
//			out.writeObject(msg); 
//			out.flush();
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
//	}


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
		public void doCustomerInterface() {
			// --- Panel 2: Client Panel with list of available transactions ---
			JPanel clientPanel = new JPanel();
			clientPanel.setLayout(cardLayout);
			frame.setLayout(new GridLayout(2,2,2,2));
		    
			//4 button layout
			JButton withdrawlBtn = new JButton();
			JButton depositBtn = new JButton();
			JButton seeTransHistoryBtn = new JButton();
			
			JButton logoutBtn = new JButton();
			
			// --- Add attributes ---
			clientPanel.add(new JLabel("Welcome"));
			clientPanel.add(withdrawlBtn);
			clientPanel.add(seeTransHistoryBtn);
			clientPanel.add(depositBtn);
			clientPanel.add(logoutBtn);
			mainPanel.add(clientPanel, "CUSTOMER");
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
			JButton btn = new JButton();
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
					
		    		// TODO : configure the string to BankAccount object
		    		Transaction withdrawl = new Transaction(withdrawlAmount,TransactionType.Withdrawal,user,(BankAccount)bankAccTxt.getText());
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
					
		    		// TODO : configure the string to BankAccount object
		    		Transaction deposit = new Transaction(depositAmount,TransactionType.Deposit,user,(BankAccount)bankAccTxt.getText());
		    		sendTransactionMessage(deposit);
		    	}
		    });
		    seeTransactionHistoryBtn.addActionListener(new ActionListener() {
		    	public void actionPerformed(ActionEvent e) {
					TextField bankAccTxt = new TextField();
					// TODO: add to panel
					add(bankAccTxt, BorderLayout.SOUTH);
					
					sendInfoRequestMessage(user);
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
