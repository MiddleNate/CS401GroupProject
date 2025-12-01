package testing;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.Test;

import shared.*;

public class LOCAccountTest {
	
	@Test
	public void testWithdrawPay() throws Exception {
		ArrayList<String> customers = new ArrayList<String>();
		String customer  = "username";
		customers.add(customer);
		// 500 withdrawal limit, 19.87% interest, 50 minimum due
		LOCAccount locAccount = new LOCAccount(customers, 500, 0.1987, 50);
		
		// balance should start at 0
		assertTrue(locAccount.getBalance() == 0.0);
		
		// test for negative values
		Exception negativeWithdrawException = assertThrows(Exception.class, () -> locAccount.withdraw(-10));
		assertEquals("Cannot withdraw negative amount", negativeWithdrawException.getMessage());
		
		Exception negativePayException = assertThrows(Exception.class, () -> locAccount.pay(-10));
		assertEquals("Cannot pay negative amount", negativePayException.getMessage());
		
		// test for withdrawal limit
		Exception withdrawLimitException = assertThrows(Exception.class, () -> locAccount.withdraw(501));
		assertEquals("Withdrawal would exceed credit limit", withdrawLimitException.getMessage());
		
		// test for over paying (not necessarily bad)
		Exception overDraftException = assertThrows(Exception.class, () -> locAccount.pay(1));
		assertEquals("Balance would go below zero", overDraftException.getMessage());
		
		// spending money on an LOC should make the balance higher
		locAccount.withdraw(500);
		assertTrue(locAccount.getBalance() == 500);
		
		// paying it off should make the balance lower
		locAccount.pay(50);
		assertTrue(locAccount.getBalance() == 450);
	}
	
	@Test
	public void testInterest() throws Exception {
		ArrayList<String> customers = new ArrayList<String>();
		String customer  = "username";
		customers.add(customer);
		// 500 withdrawal limit, 20% interest, 50 minimum due
		LOCAccount.setClock(Clock.fixed(Instant.parse("2025-11-01T14:00:00.00Z"), ZoneId.of("UTC")));
		LOCAccount locAccount = new LOCAccount(customers, 500, 0.2, 50);
		
		
		locAccount.withdraw(500);
		// one month interest: 500 + 100 = 600
		LOCAccount.setClock(Clock.fixed(Instant.parse("2025-12-01T14:00:00.00Z"), ZoneId.of("UTC")));
		assertTrue(locAccount.getBalance() == 600);
		// two months at once interest: 600 * 1.2 * 1.2 = 864
		LOCAccount.setClock(Clock.fixed(Instant.parse("2026-02-01T14:00:00.00Z"), ZoneId.of("UTC")));
		assertTrue(locAccount.getBalance() == 864);
		// interest not charged if minimum payment is met
		locAccount.pay(50);
		LOCAccount.setClock(Clock.fixed(Instant.parse("2026-03-01T14:00:00.00Z"), ZoneId.of("UTC")));
		System.out.println(locAccount.getBalance());
		assertTrue(locAccount.getBalance() == 814);
	}
	
	@Test
	public void testExceptions() {
		ArrayList<String> customers = new ArrayList<String>();
		String customer  = "username";
		customers.add(customer);
		// 500 withdrawal limit, 19.87% interest, 50 minimum due
		LOCAccount locAccount = new LOCAccount(customers, 500, 0.1987, 50);
		
		// test for negative values
		Exception negativeWithdrawException = assertThrows(Exception.class, () -> locAccount.withdraw(-10));
		assertEquals("Cannot withdraw negative amount", negativeWithdrawException.getMessage());
		
		Exception negativePayException = assertThrows(Exception.class, () -> locAccount.pay(-10));
		assertEquals("Cannot pay negative amount", negativePayException.getMessage());
		
		// test for withdrawal limit
		Exception withdrawLimitException = assertThrows(Exception.class, () -> locAccount.withdraw(501));
		assertEquals("Withdrawal would exceed credit limit", withdrawLimitException.getMessage());
		
		// test for over paying (not necessarily bad)
		Exception overDraftException = assertThrows(Exception.class, () -> locAccount.pay(1));
		assertEquals("Balance would go below zero", overDraftException.getMessage());
	}
}
