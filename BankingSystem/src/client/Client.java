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
	private static String response;

	public static void main(String[] args) {

		GUI gui = new GUI();
		new Thread(gui).start();

		while (!exiting) {
			// listen for replies, put the reply in response, redraw gui
			
		}
		
		try {
			connection.close();
		} catch (Exception e) {
			// TODO: remove console output
			System.out.println("Error closing connection: " + e);
		}
	}
	
	private void connect(String ip, int port) {
		try {
			connection = new Socket(ip, port);
			in = new ObjectInputStream(connection.getInputStream());
			out = new ObjectOutputStream(connection.getOutputStream());
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

			doLoginScreen();

		}

		public void doLoginScreen() {
			// --- Login Attributes ---
			TextArea tAOutput;
			// --- Set up frame ---
			frame.setTitle("Banking With Us");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(400, 300);
			frame.setLocationRelativeTo(null);

			// --- Create card layout ---
			cardLayout = new CardLayout();
			mainPanel = new JPanel(cardLayout);

			// --- Panel 1: Login panel with textfields ---
			JPanel loginPanel = new JPanel();
			loginPanel.setLayout(new FlowLayout());
			// --- Show 2 textFields ---
			tAOutput = new TextArea(50,50); // allocate TextField
		    tAOutput.setEditable(false);  // read-only
		    JTextField userNameTxtField = new JTextField(20);
			JTextField passwordTxtField = new JTextField(20);
			JButton loginBtn = new JButton("Login");

			// --- Add All attributes ---
			loginPanel.add(new JLabel("Welcome!"));
		    loginPanel.add(tAOutput);
			loginPanel.add(userNameTxtField);
			loginPanel.add(passwordTxtField);
			loginPanel.add(loginBtn);

			// --- Add both panels to the main panel ---
			mainPanel.add(loginPanel, "LOGIN");
			// --- Add functionality to buttons ---
			tAOutput.setText("Login to Start");
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

		}

		public void doBankAccountDetails() {

		}

		public void doTransactionMessage() {
			mainPanel.setLayout(new FlowLayout());
			JPanel upperPanel = new JPanel();
			
			
			mainPanel.add(upperPanel);
			JButton exitBtn = new JButton("Exit");

		}

		public void doSuccessMessage() {
			JOptionPane.showMessageDialog(null, "Successfully accessed");
			// call client v. employee panel, based on instance of client v. employee
		}

		public void doFailMessage() {
			JPanel failMessage = new JPanel();
			failMessage.setLayout(cardLayout);
			JButton exitBtn = new JButton("exit");

		}

		public void doInvalidMessage() {
			JOptionPane.showMessageDialog(null, "Invalid Input");
			JButton backBtn = new JButton("Back");

		}

		public void doAccountUpdatedMessage() {
			JOptionPane.showMessageDialog(null, "Account Updated successfully");
			JButton backBtn = new JButton("Back");

		}
		
	}
}
