import java.util.*;

public class Transaction {
	private int id;
	private Double amount;
	private Date date;
	private TransactionType type; 
	private User user;
	
	//Default constructor
	public Transaction() {
		this.amount = 0.0;
		this.date = null;
		this.type = null;
		this.user = null;
	}
	// Constructor
	public Transaction(Double amount, TransactionType type, Date date, User user ) {
		this.amount = amount;
		this.date = date;
		this.type = type;
		this.user = user;	
	}
}
