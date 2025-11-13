import java.util.*;
import java.time.LocalDate;

public class Transaction {
<<<<<<< Updated upstream
	protected int id;
	protected Double amount;
	protected Date date;
	protected TransactionType type; 
	protected User user;
=======
	private int id;
	private Double amount;
	private LocalDate date;
	private TransactionType type; 
	private User user;
>>>>>>> Stashed changes
	
	//Default constructor
	public Transaction() {
		this.amount = 0.0;
		this.date = null;
		this.type = null;
		this.user = null;
	}
<<<<<<< Updated upstream
	// Constructor
	public Transaction(Double amount, TransactionType type, Date date, User user ) {
=======
	
	public Transaction(Double amount, TransactionType type, LocalDate date, User user ) {
>>>>>>> Stashed changes
		this.amount = amount;
		this.date = date;
		this.type = type;
		this.user = user;	
	}
}
