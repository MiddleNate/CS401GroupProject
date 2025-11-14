import static org.junit.Assert.*;

import org.junit.Test;

public class UserTest {

	@Test
	public void test() throws Exception {
		testConstructor();
		testIfLoggedIn();
	}

	public void testConstructor() {
		User user = new User("User1", "password");
		assertEquals("User1", user.getUsername());
		assertEquals("password", user.getPassword());
	}
	
	public void testIfLoggedIn() throws Exception {
		User user = new User("User1", "password");
		assertEquals(false, user.isLoggedIn());
		user.tryLogin("User1", "password");
		assertEquals(true, user.isLoggedIn());
	}
}
