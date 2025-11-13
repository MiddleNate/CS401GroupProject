import java.util.ArrayList;

public class Customer extends User {
	private String customerName;
	private static int count = 0;
	private int customerID;
	private int socialSecNumber;
	private ArrayList<BankAccount> accounts;

	public Customer(String username, String password, String customerName, int socialSecNumber) {
		super(username, password);
		customerID++;
		this.customerName = customerName;
		this.customerID = count++;
		this.socialSecNumber = socialSecNumber;
		this.accounts = new ArrayList<BankAccount>();
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerName() {
		return this.customerName;
	}

	public int getCustomerID() {
		return this.customerID;
	}

	public int getSocialSecNumber() {
		return this.socialSecNumber;
	}
}
