import java.util.ArrayList;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public class SavingsAccount extends BankAccount {
	private double interestRate;
	private double withdrawlLimit;
	private LocalDate lastUpdated;
	private double withdrawnSinceUpdated;
	
	public SavingsAccount(ArrayList<Customer> owner, double interest, double limit) {
		id = ++count;
		status = true;
		type = AccountType.Savings;
		owners = owner;
		balance = 0;
		transactions = new ArrayList<Transaction>();
		interestRate = interest;
		withdrawlLimit = limit;
		lastUpdated = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
		withdrawnSinceUpdated = 0;
	}
	
	
}
