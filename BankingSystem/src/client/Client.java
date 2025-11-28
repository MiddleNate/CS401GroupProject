package client;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import shared.*;

public class Client {
	
	private static Socket connection;
	private static boolean exiting = false;
	private static ObjectInputStream in;
	private static ObjectOutputStream out;
	private static Message response;

	public static void main(String[] args) {

		GUI gui = new GUI();
		new Thread(gui).start();

		while (!exiting) {
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
			connection.close();
		} catch (Exception e) {
			System.out.println("Error closing connection: " + e);
		}
	}



	private void connect(String ip, int port) {
		try {
			connection = new Socket(ip, port);
			in = new ObjectInputStream(connection.getInputStream());
			out = new ObjectOutputStream(connection.getOutputStream());
			response = (Message) in.readObject();

		} catch (Exception e) {
			// TODO: remove console output
			System.out.println("error connecting: " + e + "\nExiting...");
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
	private static void sendTransactionMessage(String username, String password) {
		// create and send a message through the stream
		try {
			User user = new User(username,password);
			Transaction transaction = new Transaction();
			
			Message msg = new Message(MessageType.Transaction,user);
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
			frame.add(mainPanel);
			frame.setVisible(true);

		}

		public GUI() {

			this.frame = new JFrame();
			// --- Set up frame ---
			frame.setTitle("Switch Panel Example");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(350, 120);
			frame.setLocationRelativeTo(null);

			// --- Create card layout ---
			cardLayout = new CardLayout();
			mainPanel = new JPanel(cardLayout);
		

		public void doLoginScreen() {
			// --- Login Attributes ---
			TextArea tAOutput;
			// --- Set up frame ---
			frame.setTitle("Banking With Us");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(400, 300);
			frame.setLocationRelativeTo(null);

			// --- Panel 1: Login panel with textfields ---
			JPanel loginPanel = new JPanel();
			frame.setLayout(new BorderLayout());
			// --- Show 2 textFields & Display a Text Area ---
			tAOutput = new TextArea(5,50); // allocate TextField
		    tAOutput.setEditable(false);  // read-only
		    JTextField userNameTxtField = new JTextField(20);
			userNameTxtField.setText("Enter Username");

			JTextField passwordTxtField = new JTextField(20);
			passwordTxtField.setText("Enter Password");

			JButton loginBtn = new JButton("Login");
			
			// --- Add All attributes ---
			loginPanel.add(new JLabel("Welcome!"), BorderLayout.NORTH);
			loginPanel.add(userNameTxtField, BorderLayout.WEST);
			loginPanel.add(passwordTxtField,BorderLayout.EAST);
			loginPanel.add(loginBtn, BorderLayout.CENTER);
		    loginPanel.add(tAOutput,BorderLayout.SOUTH);

			// --- Add both panels to the main panel ---
			mainPanel.add(loginPanel, "LOGIN");
			
			// --- Add functionality to buttons ---
			
			// saves input as 2 string objects
			loginBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					sendLoginMessage(userNameTxtField.getText(),passwordTxtField.getText());
				}
			});

			// --- Panel 2: Client Panel with list of available transactions ---
			JPanel clientPanel = new JPanel();
			clientPanel.setLayout(cardLayout);
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
			mainPanel.setLayout(new FlowLayout());
			JPanel upperPanel = new JPanel();
			
			mainPanel.add(upperPanel);
			
			
			sendTransactionMessage();
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

		}}
