import java.util.*;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.*;

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
		//call client v. employee panel, based on instance of client v. employee
	}
	public void doFailMessage() {
		JOptionPane failedMsg = new JOptionPane.showMessageDialog(null, "Failed Message");
	}
	public void doInvalidMessage() {
		JOptionPane invalidMsg = new JOptionPane.showMessageDialog(null,"Invalid Input");
	}
	public void doAccountUpdatedMessage() {
		JOptionPane accountUpdateMsg = new JOptionPane.showMessageDialog(null,"Account Updated successfully");
	}
	
}
