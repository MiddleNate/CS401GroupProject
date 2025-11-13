import java.util.ArrayList;

public class Customer extends User {
	private String customerName;
	private static int customerID = 0;
	private int id;
	private int socialSecNumber;
	private ArrayList<BankAccount> accounts;

	public Customer(String username, String password, String customerName, int socialSecNumber) {
		super(username, password);
		customerID++;
		this.customerName = customerName;
		this.id = customerID;
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
		return this.id;
	}

	public int getSocialSecNumber() {
		return this.socialSecNumber;
	}
}
