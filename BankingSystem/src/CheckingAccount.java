import java.util.ArrayList;

public class CheckingAccount extends BankAccount {
	public CheckingAccount(ArrayList<Customer> owner) {
		id = ++count;
		status = true;
		type = AccountType.Checking;
		owners = owner;
		balance = 0;
		transactions = new ArrayList<Transaction>();
	}
	
	public boolean deposit(double amt) {
		// do not deposit if account is closed
		if (!status) return false;
		
		// ensure only positive amounts are being deposited
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
		// do not withdraw if account is closed
		if (!status) return false;
		
		// ensure only positive amounts are being withdrawn and
		// that the balance will not go below zero
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
