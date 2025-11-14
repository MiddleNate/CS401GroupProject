import static org.junit.Assert.*;

import org.junit.Test;

public class EmployeeTest {

	@Test
	public void test() {
		testConstructor();
	}

	public void testConstructor() { 
		Employee employee = new Employee("User1", "Password", "Name");
		assertEquals("User1", employee.getUsername());
		assertEquals("Password", employee.getPassword());
		assertEquals("Name", employee.getEmployeeName());
		assertEquals(0, employee.getEmployeeID());
	}
}
