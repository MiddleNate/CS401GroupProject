import static org.junit.Assert.*;
import java.util.ArrayList;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.Test;

public class SavingsAccountTest {

	@Test
	public void test() {
		testDepositWithdraw();
		testInterest();
	}
	
	public void testDepositWithdraw() {
		ArrayList<Customer> customers = new ArrayList<Customer>();
		Customer customer  = new Customer("User1", "Password", "Name", 999);
		customers.add(customer);
		
		// savings account starts with 5% monthly interest and 500 withdrawal limit
		SavingsAccount savingsAccount = new SavingsAccount(customers, 0.05, 500);
		
		// balance should start at 0
		assertTrue(savingsAccount.getBalance() == 0.0);
		
		// depositing 1000 should make the balance 1000
		savingsAccount.deposit(1000);
		assertTrue(savingsAccount.getBalance() == 1000);
		
		savingsAccount.withdraw(1001);
		assertTrue(savingsAccount.getBalance() == 1000);
		
		// can't withdraw the limit
		savingsAccount.withdraw(500);
		assertTrue(savingsAccount.getBalance() == 1000);
		
		// successful withdraw
		savingsAccount.withdraw(100);
		assertTrue(savingsAccount.getBalance() == 900);
		
	}

	public void testInterest() {
		ArrayList<Customer> customers = new ArrayList<Customer>();
		Customer customer  = new Customer("User1", "Password", "Name", 999);
		customers.add(customer);
		// savings account starts with 5% monthly interest and 500 withdrawal limit
		Clock fixedClock = Clock.fixed(Instant.parse("2020-01-01T14:00:00.00Z"), ZoneId.of("UTC"));
		SavingsAccount.setClock(fixedClock);
		SavingsAccount savingsAccount = new SavingsAccount(customers, 0.05, 500);
		
		savingsAccount.deposit(1000);
		SavingsAccount.setClock(Clock.fixed(Instant.parse("2020-02-02T14:00:00.00Z"), ZoneId.of("UTC")));
		System.out.println(savingsAccount.getBalance());
		System.out.println(savingsAccount.getBalance());
		assertTrue(savingsAccount.getBalance() == 1050.0);
		
	}
}
