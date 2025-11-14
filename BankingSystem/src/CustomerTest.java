import static org.junit.Assert.*;

import org.junit.Test;

public class CustomerTest {

	@Test
	public void test() {
		testConstructor();
	}

	public void testConstructor() { 
		Customer customer = new Customer("User1", "Password", "Name", 999);
		assertEquals("User1", customer.getUsername());
		assertEquals("Password", customer.getPassword());
		assertEquals("Name", customer.getCustomerName());
		assertEquals(999, customer.getSocialSecNumber());
		assertEquals(0, customer.getCustomerID());
	}
}
