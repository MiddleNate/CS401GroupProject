import java.util.ArrayList;

public class CheckingAccount extends BankAccount {
	private static final long serialVersionUID = 900L;

	public CheckingAccount(ArrayList<Customer> owner) {
		id = ++count;
		status = true;
		type = AccountType.Checking;
		owners = owner;
		balance = 0;
		transactions = new ArrayList<Transaction>();
	}
	
	@Override
	public void tryTransaction(Transaction transaction) throws Exception {
		// check that the type is either deposit or withdrawal
		if (transaction.getType() != TransactionType.Deposit
				&& transaction.getType() != TransactionType.Withdrawal) {
			throw new Exception("Invalid transaction type");
		// check that the account is not closed
		} else if (!status) {
			throw new Exception("Account is closed");
		} else {
			if (transaction.getType() == TransactionType.Deposit) {
				try {
					deposit(transaction.getAmount());
					// if an exception was not thrown, log the transaction
					transactions.add(new Transaction(transaction.getAmount(),
							TransactionType.Deposit,
							transaction.getUser(),
							this));
				} catch (Exception e) {
					throw e;
				}
			} else if (transaction.getType() == TransactionType.Withdrawal) {
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
			}
		}
	}
	
	public void deposit(double amt) throws Exception {
		// ensure only positive amounts are being deposited
		if (amt < 0) throw new Exception("Cannot deposit negative amounts");

		
		// truncate any extra decimal places
		amt = Math.floor(amt * 100) / 100;
		balance += amt;
	}
	
	public void withdraw(double amt) throws Exception{
		// ensure only positive amounts are being withdrawn and
		// that the balance will not go below zero
		if (amt < 0) throw new Exception("Cannot withdraw negative amounts");
		if (balance - amt < 0) throw new Exception("Balance would go below zero");
		
		// truncate any extra decimal places
		amt = Math.floor(amt * 100) / 100;
		balance -= amt;
	}
}
