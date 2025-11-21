package shared;
import java.util.ArrayList;
import java.io.Serializable;

public abstract class BankAccount extends Throwable implements Serializable {
	private static final long serialVersionUID = 870L;
	protected int id;
	protected boolean status;
	protected AccountType type;
	protected ArrayList<String> owners;
	protected double balance;
	protected ArrayList<Transaction> transactions;
	protected static int count;

	public void addUser(String username) {
		owners.add(username);
	}
	
	public static void setCount(int c) {
		count = c;
	}
	
	public static int getCount() {
		return count;
	}
	
	public void removeOwner(String username) {
		for (int i = 0; i < owners.size(); i++) {
			if (owners.get(i).compareTo(username) == 0) {
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

	public ArrayList<String> getOwners() {
		return owners;
	}

	public double getBalance() {
		return balance;
	}
	
	public ArrayList<Transaction> getTransactions() {
		return transactions;
	}
	
	public void closeAccount() throws Exception {
		if (!status) throw new Exception("Account is already closed");
		if (balance != 0) throw new Exception("Account balance is not zero");
		
		status = false;
	}
	
	public void tryTransaction(Transaction transaction, User user) throws Exception {
		throw new Exception("Trying transaction on an abstract BankAccount");
	}
	
	public String toString() {
		return "Trying toString() on an abstract BankAccount\n";
	}
}
