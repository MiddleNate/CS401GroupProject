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
		// in a LOC account, the balance represents the amount owed
		balance = 0;
		transactions = new ArrayList<Transaction>();
		creditLimit = limit;
		interestRate = interest;
		minimumDue = minimum;
		lastUpdated = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
		paidSinceUpdated = 0;
	}
	
	public double getLimit() {
		return creditLimit;
	}
	
	public void setCreditLimit(double limit) {
		creditLimit = limit;
	}
	
	public double getInterest() {
		return interestRate;
	}
	
	public void setInterest(double interest) {
		interestRate = interest;
	}
	
	public double getMinimumDue() {
		return minimumDue;
	}
	
	public void setMinimumDue(double minimum) {
		minimumDue = minimum;
	}
	
	public double getPaidSinceUpdated() {
		return paidSinceUpdated;
	}
	
	public boolean withdraw(double amt) {
		// do not allow withdrawal if the account is closed
		if (!status) return false;
		
		// since the balance represents debt owed, withdraw will add
		// to the balance instead of subtracting
		
		// check that the provided amount was positive and that the
		// withdrawal will not exceed the credit limit
		if (amt > 0 && amt + balance <= creditLimit) {
			// truncate any extra decimal places
			amt = Math.floor(amt * 100) / 100;
			balance += amt;
			return true;
		} else {
			return false;
		}
	}
	
	public boolean pay(double amt) {
		// do not allow payment if the amount is closed
		if (!status) return false;
		
		// since the balance represents debt owed, paying with subtract
		// from the balance instead of adding
		
		// check that the provided amount was positive
		// and will not pay more than the balance
		if (amt > 0 && balance - amt >= 0) {
			// truncate any extra decimal places
			amt = Math.floor(amt * 100) / 100;
			balance -= amt;
			return true;
		} else {
			return false;
		}
	}
}
