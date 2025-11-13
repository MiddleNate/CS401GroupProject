import java.util.*;

public class Transaction {
	protected int id;
	protected static int count;
	protected double amount;
	protected Date date;
	protected TransactionType type; 
	protected User user;
	protected BankAccount account;
	
	public static void setTransactionCount(int c) {
		count = c;
	}
	
	public static int getTransactionCount() {
		return count;
	}
	
	// Constructor
	public Transaction(double amount, TransactionType type, User user, BankAccount account) {
		this.id = ++count;
		this.amount = amount;
		this.date = new Date();
		this.type = type;
		this.user = user;
		this.account = account;
	}
}
