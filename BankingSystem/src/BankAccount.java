import java.util.ArrayList;
import java.io.Serializable;

abstract class BankAccount implements Serializable {
	private static final long serialVersionUID = 870L;
	protected int id;
	protected boolean status;
	protected AccountType type;
	protected ArrayList<Customer> owners;
	protected double balance;
	protected ArrayList<Transaction> transactions;
	protected static int count;

	public void addUser(Customer cust) {
		owners.add(cust);
	}
	
	public static void setCount(int c) {
		count = c;
	}

	public void removeUser(Customer cust) {
		for (int i = 0; i < owners.size(); i++) {
			if (owners.get(i).equals(cust)) {
				owners.remove(i);
			}
		}
	}

	public int getID() {
		return id;
	}
	
	public boolean isOpen() {
		return status;
	}
	
	public AccountType getType() {
		return type;
	}

	public ArrayList<Customer> getOwners() {
		return owners;
	}

	public double getBalance() {
		return balance;
	}
	
	public ArrayList<Transaction> getTransactions() {
		return transactions;
	}
	
	public boolean closeAccount() {
		if (status == true && balance == 0) {
			status = false;
			return true;
		} else {
			return false;
		}
	}
}
