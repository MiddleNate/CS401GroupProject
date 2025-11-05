import java.util.ArrayList;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.ChronoUnit;

public class LOCAccount extends BankAccount {
	private double creditLimit;
	private double interestRate;
	private double minimumDue;
	private LocalDate lastUpdated;
	private double paidSinceUpdated;
	
	public LOCAccount(ArrayList<Customer> owner, double limit, double interest, double minimum) {
		id = ++count;
		status = true;
		type = AccountType.LineOfCredit;
		owners = owner;
		balance = 0;
		transactions = new ArrayList<Transaction>();
		creditLimit = limit;
		interestRate = interest;
		minimumDue = minimum;
		lastUpdated = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
		paidSinceUpdated = 0;
	}
}
