import java.util.ArrayList;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.ChronoUnit;
import java.time.Clock;

public class LOCAccount extends BankAccount {
	private static final long serialVersionUID = 970L;
	private double creditLimit;
	private double interestRate;
	private double minimumDue;
	private LocalDate lastUpdated;
	private double paidSinceUpdated;
	// for testing with certain dates
	private static Clock clock = Clock.systemDefaultZone();
	
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
		lastUpdated = LocalDate.now(clock).with(TemporalAdjusters.firstDayOfMonth());
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
	
	@Override public double getBalance() {
		update();
		return balance;
	}
	
	// for testing with certain dates
	public static void setClock(Clock c) {
		clock = c;
	}
	
	public void update() {
		// do not update if the account is closed
		if (!status) return;
		
		LocalDate currentMonth = LocalDate.now(clock).with(TemporalAdjusters.firstDayOfMonth());
		// check if at least one month has passed since the last updated date
		if (currentMonth.isAfter(lastUpdated)) {
			// figure out how many months have passed (how many times we need to update)
			int numUpdates = (int) ChronoUnit.MONTHS.between(lastUpdated, currentMonth);
			for (int i = 0; i < numUpdates; i++) {
				// only charge interest if the minimum has not been paid
				// if multiple months have elapsed, this will only apply 
				// to the first month since paidSinceUpdated gets reset to 0
				// only applies if the balance is greater than 0
				if (paidSinceUpdated < minimumDue && balance > 0) {
					// also logs the transaction as it is applied
					transactions.add(new Transaction((balance * 1 + interestRate), TransactionType.Interest, null, this));
					balance *= 1 + interestRate;
				}
				paidSinceUpdated = 0;
				lastUpdated = currentMonth;
			}
		}
		// if no updates are needed, do nothing
	}
	
	public void tryTransaction(Transaction transaction) throws Exception {
		update();
		
		// check that the type is either deposit or withdrawal
		if (transaction.getType() != TransactionType.Withdrawal
				&& transaction.getType() != TransactionType.Payment) {
			throw new Exception("Invalid transaction type");
		// check that the account is not closed
		} else if (!status) {
			throw new Exception("Account is closed");
		} else {
			if (transaction.getType() == TransactionType.Withdrawal) {
				try {
					withdraw(transaction.getAmount());
					// if an exception was not thrown, log the transaction
					transactions.add(new Transaction(transaction.getAmount(),
							TransactionType.Withdrawal,
							transaction.getUser(),
							this));
				} catch (Exception e) {
					throw e;
				}
			} else if (transaction.getType() == TransactionType.Payment) {
				try {
					withdraw(transaction.getAmount());
					// if an exception was not thrown, log the transaction
					transactions.add(new Transaction(transaction.getAmount(),
							TransactionType.Payment,
							transaction.getUser(),
							this));
				} catch (Exception e) {
						throw e;
				}
			}
		}
	}
	
	public void withdraw(double amt) throws Exception {
		update();
		
		// since the balance represents debt owed, withdraw will add
		// to the balance instead of subtracting
		
		// check that the provided amount was positive and that the
		// withdrawal will not exceed the credit limit
		if (amt < 0) throw new Exception("Cannot withdraw negative amount");
		if (amt + balance > creditLimit) throw new Exception("Withdrawal would exceed credit limit");

		// truncate any extra decimal places
		amt = Math.floor(amt * 100) / 100;
		balance += amt;
	}
	
	public void pay(double amt) throws Exception {
		update();
		
		// since the balance represents debt owed, paying will subtract
		// from the balance instead of adding
		
		// check that the provided amount was positive
		// and will not pay more than the balance
		if (amt < 0) throw new Exception("Cannot pay negative amount");
		if (balance - amt < 0) throw new Exception("Balance would go below zero");
		
		// truncate any extra decimal places
		amt = Math.floor(amt * 100) / 100;
		balance -= amt;
		paidSinceUpdated += amt;
	}
}
