import java.util.*;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI implements Runnable{
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
		List<String>loginInputs = new ArrayList<>();
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
        mainPanel.add(loginPanel,"LOGIN");
        
        // --- Add functionality to buttons ---
        //saves input as 2 string objects in a list, send loginMsg to server
        submitBtn.addActionListener(new ActionListener() {
        		@Override
        		public void actionPerformed(ActionEvent e) {
        			loginInputs.add(userNameTxtField.getText());
        			loginInputs.add(passwordTxtField.getText());	
        			//add loginMsg into list
        		}
        });
        
        
        // --- Panel 2: Client Panel with list of available transactions ---
        JPanel clientPanel = new JPanel();
        clientPanel.setLayout(cardLayout);
        
        // --- Panel 3: Employee Panel with list of available transactions ---
        JPanel employeePanel = new JPanel();
        employeePanel.setLayout(cardLayout);
        
        // --- Add both panels to the main panel ---
        mainPanel.add(clientPanel,"CLIENT");
        mainPanel.add(employeePanel,"EMPLOYEE");



	}
	public void doOpenOrCloseAccount() {
		
	}
	public void doBankAccounts() {
		
	}
	public void doBankAccountDetails() {
	
	}
	public void doTransactionMessage() {
	    JOptionPane.showMessageDialog(null, "Transaction successful");
	}
	public void doSuccessMessage() {
		JOptionPane.showMessageDialog(null, "Successfully accessed");
		//call client v. employee panel, based on instance of client v. employee
	}
	public void doFailMessage() {
		JOptionPane.showMessageDialog(null, "Failed Message");
	}
	public void doInvalidMessage() {
		JOptionPane.showMessageDialog(null,"Invalid Input");
	}
	public void doAccountUpdatedMessage() {
		JOptionPane.showMessageDialog(null,"Account Updated successfully");
	}
	
}
