package shared;
import java.util.ArrayList;

public class Customer extends User {
	private static final long serialVersionUID = 60L;
	private static int customerCount = 0;
	private int id;
	private ArrayList<Integer> accounts;

	public Customer(String username, String password) {
		super(username, password);
		this.id = ++customerCount;
		this.accounts = new ArrayList<Integer>();
	}
	
	public static void setCustomerCount(int newCount) {
		customerCount = newCount;
	}
	
	public static int getCustomerCount() {
		return customerCount;
	}

	public int getCustomerID() {
		return this.id;
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
