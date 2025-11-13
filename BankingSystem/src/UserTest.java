import static org.junit.Assert.*;

import org.junit.Test;

public class UserTest {

	@Test
	public void test() {
		testConstructor();
	}

	public void testConstructor() {
		User user = new User("User1", "password");
		assertEquals("User1", user.getUsername());
		assertEquals("password", user.getPassword());
	}
	
	public void testIfLoggedIn() {
		
	}
}
