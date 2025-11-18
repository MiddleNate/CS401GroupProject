import java.util.ArrayList;

public class Customer extends User {
	private static final long serialVersionUID = 60L;
	private String customerName;
	private static int count = 0;
	private int customerID;
	private static int customerCount = 0;
	private int id;
	private int socialSecNumber;
	private ArrayList<Integer> accounts;

	public Customer(String username, String password, String customerName, int socialSecNumber) {
		super(username, password);
		this.customerName = customerName;
		this.customerID = count++;
		this.id = ++customerCount;
		this.socialSecNumber = socialSecNumber;
		this.accounts = new ArrayList<Integer>();
	}
	
	public static void setCustomerCount(int newCount) {
		customerCount = newCount;
	}
	
	public static int getCustomerCount() {
		return customerCount;
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
	
	public ArrayList<Integer> getAccounts() {
		return accounts;
	}
	
	public void addAccount(int acc) {
		accounts.add(acc);
	}
	
	public void removeAccount(int acc) {
		accounts.remove(Integer.valueOf(acc));
	}
}
