package testing;
import static org.junit.Assert.*;

import org.junit.Test;

import shared.*;

public class UserTest {
	
	@Test
	public void testConstructor() {
		User user = new User("username", "password");
		assertEquals("username", user.getUsername());
		assertEquals("password", user.getPassword());
	}
	
	@Test
	public void testLoginLogout() throws Exception {
		User user = new User("username", "password");
		// user should not be logged in yet
		assertEquals(false, user.isLoggedIn());
		// test login
		user.tryLogin("username", "password");
		assertEquals(true, user.isLoggedIn());
		// test logout
		user.logout();
		assertEquals(false, user.isLoggedIn());
	}
	
	@Test
	public void testExceptions() throws Exception {
		User user = new User("username", "password");
		
		// test if already logged in
		user.tryLogin("username", "password");
		Exception alreadyLoggedException = assertThrows(Exception.class, () -> user.tryLogin("username", "password"));
		assertEquals("Already logged in", alreadyLoggedException.getMessage());
		
		// test for invalid password
		user.logout();
		Exception invalidInfoException = assertThrows(Exception.class, () -> user.tryLogin("username", "pass"));
		assertEquals("Invalid username or password", invalidInfoException.getMessage());
	}
}
