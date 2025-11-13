import java.util.*;
import java.time.LocalDate;

public class Transaction {
	private int id;
	private static int count;
	private Double amount;
	private LocalDate date;
	private TransactionType type; 
	private User user;
	private BankAccount account;

	public static void setTransactionCount(int c) {
		count = c;
	}
	public static int getTransactionCount() {
		return count;
	}
	public Transaction(double amount, TransactionType type, User user, BankAccount account) {
		this.id = ++count;
		this.amount = amount;
		this.date = LocalDate.now();
		this.type = type;
		this.user = user;
		this.account = account;
	}
}
