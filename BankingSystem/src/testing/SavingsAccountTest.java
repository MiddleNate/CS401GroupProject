package testing;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.Test;

import shared.*;

public class SavingsAccountTest {
	
	@Test
	public void testDepositWithdraw() throws Exception {
		ArrayList<String> customers = new ArrayList<String>();
		String customer  = "username";
		customers.add(customer);
		// savings account starts with 5% monthly interest and 500 withdrawal limit
		SavingsAccount savingsAccount = new SavingsAccount(customers, 0.05, 500);
		
		// balance should start at 0
		assertTrue(savingsAccount.getBalance() == 0.0);
		
		// depositing 1000 should make the balance 1000
		savingsAccount.deposit(1000);
		assertTrue(savingsAccount.getBalance() == 1000);
		
		// successful withdraw
		savingsAccount.withdraw(100);
		assertTrue(savingsAccount.getBalance() == 900);
	}
	
	@Test
	public void testInterest() throws Exception {
		ArrayList<String> customers = new ArrayList<String>();
		String customer = "username";
		customers.add(customer);
		// savings account starts with 5% monthly interest and 500 withdrawal limit
		SavingsAccount.setClock(Clock.fixed(Instant.parse("2025-11-01T14:00:00.00Z"), ZoneId.of("UTC")));
		SavingsAccount savingsAccount = new SavingsAccount(customers, 0.05, 500);
		
		// interest is charged every 1st of the month
		savingsAccount.deposit(1000);
		// one month interest: 1000 + 50 = 1050.0
		SavingsAccount.setClock(Clock.fixed(Instant.parse("2025-12-01T14:00:00.00Z"), ZoneId.of("UTC")));
		assertTrue(savingsAccount.getBalance() == 1050.0);
		// two months at once interest: 1050 * 1.05 * 1.05 = 1157.62
		SavingsAccount.setClock(Clock.fixed(Instant.parse("2026-02-01T14:00:00.00Z"), ZoneId.of("UTC")));
		assertTrue(savingsAccount.getBalance() == 1157.62);
	}
	
	@Test
	public void testExceptions() { 
		ArrayList<String> customers = new ArrayList<String>();
		String customer  = "username";
		customers.add(customer);
		// savings account starts with 5% monthly interest and 500 withdrawal limit
		SavingsAccount savingsAccount = new SavingsAccount(customers, 0.05, 500);
		
		// test for negative values
		Exception negativeException = assertThrows(Exception.class, () -> savingsAccount.withdraw(-10));
		assertEquals("Cannot withdraw negative amounts", negativeException.getMessage());
		
		// test for withdrawal limit
		Exception withdrawLimitException = assertThrows(Exception.class, () -> savingsAccount.withdraw(501));
		assertEquals("Transaction would exceed withdrawal limit", withdrawLimitException.getMessage());
		
		// test for overdraft
		Exception overDraftException = assertThrows(Exception.class, () -> savingsAccount.withdraw(1));
		assertEquals("Balance would go below zero", overDraftException.getMessage());
	}
}
