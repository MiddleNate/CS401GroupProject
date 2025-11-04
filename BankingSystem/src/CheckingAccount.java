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
	
	
}
