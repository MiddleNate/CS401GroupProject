import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class CheckingAccountTest {

	@Test
	public void test() throws Exception {
		testDepositAndWithdraw();
		testExceptions();
	}

	public void testDepositAndWithdraw() throws Exception {
		ArrayList<String> customers = new ArrayList<String>();
		String customer  = "username";
		customers.add(customer);
		CheckingAccount checkingAccount = new CheckingAccount(customers);
		
		// balance should start at 0
		assertTrue(checkingAccount.getBalance() == 0.0);
		
		// test for depositing negative amounts
		Exception negativeException = assertThrows(Exception.class, () -> checkingAccount.deposit(-10));
		assertEquals("Cannot deposit negative amounts", negativeException.getMessage());
		
		// test for withdrawing negative amounts
		Exception withdrawLimitException = assertThrows(Exception.class, () -> checkingAccount.withdraw(-10));
		assertEquals("Cannot withdraw negative amounts", withdrawLimitException.getMessage());
		
		// test for overdraft
		Exception overDraftException = assertThrows(Exception.class, () -> checkingAccount.withdraw(1));
		assertEquals("Balance would go below zero", overDraftException.getMessage());
		
		// depositing 100.50 should make the balance 100.50
		checkingAccount.deposit(100.50);
		assertTrue(checkingAccount.getBalance() == 100.50);
		
		// withdrawing 50.50 should make the balance 50
		checkingAccount.withdraw(50.50);
		assertTrue(checkingAccount.getBalance() == 50);
	}
	
	public void testExceptions() {
		ArrayList<String> customers = new ArrayList<String>();
		String customer  = "username";
		customers.add(customer);
		CheckingAccount checkingAccount = new CheckingAccount(customers);
		
		// test for depositing negative amounts
		Exception negativeException = assertThrows(Exception.class, () -> checkingAccount.deposit(-10));
		assertEquals("Cannot deposit negative amounts", negativeException.getMessage());
		
		// test for withdrawing negative amounts
		Exception withdrawLimitException = assertThrows(Exception.class, () -> checkingAccount.withdraw(-10));
		assertEquals("Cannot withdraw negative amounts", withdrawLimitException.getMessage());
		
		// test for overdraft
		Exception overDraftException = assertThrows(Exception.class, () -> checkingAccount.withdraw(1));
		assertEquals("Balance would go below zero", overDraftException.getMessage());
	}
}
