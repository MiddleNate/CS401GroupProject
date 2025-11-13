import java.util.*;
import java.time.LocalDate;

public class Transaction {
	private int id;
	private Double amount;
	private LocalDate date;
	private TransactionType type; 
	private User user;
	
	public Transaction(Double amount, TransactionType type, User user ) {
		this.amount = amount;
		this.date = LocalDate.now();
		this.type = type;
		this.user = user;
	}
}
