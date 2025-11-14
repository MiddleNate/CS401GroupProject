import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class CheckingAccountTest {

	@Test
	public void test() {
		testDepositAndWithdraw();
	}

	public void testDepositAndWithdraw() {
		// customer has a checking account
		ArrayList<Customer> customers = new ArrayList<Customer>();
		Customer customer  = new Customer("User1", "Password", "Name", 999);
		customers.add(customer);
		CheckingAccount checkingAccount = new CheckingAccount(customers);
		
		// balance should start at 0
		assertTrue(checkingAccount.getBalance() == 0.0);
		
		// depositing 100.50 should make the balance 100.50
		checkingAccount.deposit(100.50);
		assertTrue(checkingAccount.getBalance() == 100.50);
		
		// withdrawing 50.50 should make the balance 50
		checkingAccount.withdraw(50.50);
		assertTrue(checkingAccount.getBalance() == 50);
		
		// withdrawing more than what the account balance is should not change the balance
		checkingAccount.withdraw(51);
		assertTrue(checkingAccount.getBalance() == 50);
	}
}
