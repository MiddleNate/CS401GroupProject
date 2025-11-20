import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Client {
	
	private static Socket connection;
	private static boolean exiting = false;
	private static ObjectInputStream in;
	private static ObjectOutputStream out;

	public static void main(String[] args) {
		GUI gui = new GUI();
		new Thread(gui).start();
		
		try {
			in = new ObjectInputStream(connection.getInputStream());
			out = new ObjectOutputStream(connection.getOutputStream());
		} catch (Exception e) {
			// TODO: remove console output
			System.out.println("Error creating streams: " + e);
		}
		
		
		while (!exiting) {
			// listen for replies and redraw gui if a reply is received
			
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
		} catch (Exception e) {
			// TODO: remove console output
			System.out.println("error connecting: " + e + "\nExiting...");
		}
	}
	
	private void sendLoginMessage(String username, String password) {
		// create and send a message through the stream
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

			// --- Set up frame ---
			frame.setTitle("Switch Panel Example");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(400, 300);
			frame.setLocationRelativeTo(null);

			// --- Create card layout ---
			cardLayout = new CardLayout();
			mainPanel = new JPanel(cardLayout);

			doLoginScreen();

		}

		public void doLoginScreen() {
			// --- Login Attributes ---
			String savedUserName;
			String savedPassword;
			// --- Set up frame ---
			frame.setTitle("Switch Panel Example");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(400, 300);
			frame.setLocationRelativeTo(null);

			// --- Create card layout ---
			cardLayout = new CardLayout();
			mainPanel = new JPanel(cardLayout);

			// --- Panel 1: Login panel with textfields ---
			JPanel loginPanel = new JPanel();
			loginPanel.setLayout(new BorderLayout());
			// --- Show 2 textFields ---
			JTextField userNameTxtField = new JTextField(20);
			JTextField passwordTxtField = new JTextField(20);
			JButton submitBtn = new JButton("Enter");

			// --- Add All attributes ---
			loginPanel.add(new JLabel("Logo Here"));
			loginPanel.add(new JLabel("Welcome! Please Login"));
			loginPanel.add(userNameTxtField);
			loginPanel.add(passwordTxtField);
			loginPanel.add(submitBtn);

			// --- Add both panels to the main panel ---
			mainPanel.add(loginPanel, "LOGIN");
			// --- Add functionality to buttons ---
			// saves input as 2 string objects
			submitBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPreformed(ActionEvent e) {
					savedUserName = userNameTxtField.getText();
					savedPassword = passwordTxtField.getText();

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

		}

		public void doBankAccounts() {

		}

		public void doBankAccountDetails() {

		}

		public void doTransactionMessage() {
			JOptionPane transactionMsg = new JOptionPane.showMessageDialog(null, "Transaction successful");
		}

		public void doSuccessMessage() {
			JOptionPane successMsg = new JOptionPane.showMessageDialog(null, "Successfully accessed");
			// call client v. employee panel, based on instance of client v. employee
		}

		public void doFailMessage() {
			JOptionPane failedMsg = new JOptionPane.showMessageDialog(null, "Failed Message");
		}

		public void doInvalidMessage() {
			JOptionPane invalidMsg = new JOptionPane.showMessageDialog(null, "Invalid Input");
		}

		public void doAccountUpdatedMessage() {
			JOptionPane accountUpdateMsg = new JOptionPane.showMessageDialog(null, "Account Updated successfully");
		}

	}
}
