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

	}
	public void doSuccessMessage() {

	}
	public void doFailMessage() {
	}
	public void doInvalidMessage() {
	}
	public void doAccountUpdatedMessage() {

	}
	
}
