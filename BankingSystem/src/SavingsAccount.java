import java.util.ArrayList;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.ChronoUnit;

public class SavingsAccount extends BankAccount {
	private static final long serialVersionUID = 910L;
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
	
	public void setWithdrawlLimit(double limit) {
		withdrawlLimit = limit;
	}
	
	public double getWithdrawlLimit() {
		return withdrawlLimit;
	}
	
	public double getInterest() {
		return interestRate;
	}
	
	public void setInterest(double interest) {
		interestRate = interest;
	}
	
	public LocalDate getLastUpdated() {
		return lastUpdated;
	}
	
	public double getWithdrawnSinceUpdated() {
		return withdrawnSinceUpdated;
	}
	
	@Override public double getBalance() {
		update();
		return balance;
	}
	
	public void update() {
		// do not update if the account is closed
		if (!status) return;
		
		LocalDate currentMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
		// check if at least one month has passed since the last updated date
		if (currentMonth.isAfter(lastUpdated)) {
			// figure out how many months have passed (how many times we need to update)
			int numUpdates = (int) ChronoUnit.MONTHS.between(lastUpdated, currentMonth);
			for (int i = 0; i < numUpdates; i++) {
				// for each update, add the interest rate to the balance and log the transaction
				transactions.add(new Transaction((balance * 1 + interestRate), TransactionType.Interest, null, this));
				balance *= 1 + interestRate;
			}
			// reset the amount withdrawn since the last update
			withdrawnSinceUpdated = 0;
			lastUpdated = currentMonth;
		}
		// if no updates are needed, do nothing
	}
	
	public boolean deposit(double amt) {
		// do not deposit if the account is closed
		if (!status) return false;
		
		update();
		if (amt > 0) {
			// truncate any extra decimal places
			amt = Math.floor(amt * 100) / 100;
			balance += amt;
			return true;
		} else {
			return false;
		}
	}
	
	public boolean withdraw(double amt) {
		// do not deposit if the account is closed
		if (!status) return false;
		
		update();
		if (amt > 0
				&& (withdrawnSinceUpdated + amt) < withdrawlLimit
				&& balance - amt > 0) {
			// truncate any extra decimal places
			amt = Math.floor(amt * 100) / 100;
			balance -= amt;
			// add the amount towards the withdrawl limit
			withdrawnSinceUpdated += amt;
			return true;
		} else {
			return false;
		}
	}
}
