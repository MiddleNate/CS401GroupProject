import java.util.ArrayList;

public class BankAccount {
	private int id;
	private boolean status;
	private AccountType type;
	private ArrayList<Customer> owners;
	private double balance;
	private ArrayList<Transaction> transactions;

	public void addUser(Customer cust) {
		owners.add(cust);
	}
	
	public void setCount(int c) {
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

	public ArrayList<Customer> getUsers() {
		return owners;
	}
	
	public boolean isOpen() {
		return status;
	}

	public double getBalance() {
		return balance;
	}
	
	public ArrayList<Transaction> getTransactions() {
		return transactions;
	}
	
	public boolean closeAccount() {
		if (status == true) {
			status = false;
			return true;
		} else {
			return false;
		}
	}
}
