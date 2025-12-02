package shared;
import java.io.Serializable;
import java.time.LocalDate;

public class Transaction implements Serializable {
	private static final long serialVersionUID = 190L;
	private int id;
	private static int count;
	private Double amount;
	private LocalDate date;
	private TransactionType type; 
	private User user;
	private int account;

	public static void setTransactionCount(int c) {
		count = c;
	}
	
	public static int getTransactionCount() {
		return count;
	}
	
	public Transaction(double amount, TransactionType type, User user, int account) {
		this.id = ++count;
		this.amount = amount;
		this.date = LocalDate.now();
		this.type = type;
		this.user = user;
		this.account = account;
	}
	
	public int getID() {
		return id;
	}
	
	public Double getAmount() {
		return amount;
	}
	
	public LocalDate getDate() {
		return date;
	}
	
	public TransactionType getType() {
		return type;
	}
	
	public User getUser() {
		return user;
	}
	
	public int getAccount() {
		return account;
	}
	
	public String toString() {
		String output = "Transaction ID: " + id + "\tType: " + type.name();
		// this is for formatting i swear its important
		switch (type) {
		case TransactionType.Deposit:
		case TransactionType.Interest:
		case TransactionType.Payment:
			output += "\t\t";
			break;
		case TransactionType.Withdrawal:
			output += "\t";
			break;
		}
		
		output += "Amount: " + amount + "\tDate: " + date + "\tUser: " + user.getUsername() + "\tAccount ID: " + account + "\n";
		return output;
	}
}
