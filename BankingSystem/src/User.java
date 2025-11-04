
public class User {
	private String username;
	private String password;
	private boolean loggedIn;

	public User(String username, String password) {
		this.username = username;
		this.password = password;
		this.loggedIn = false;
	}

	public String getUsername() {
		return this.username;
	}

	public String getPassword() {
		return this.password;
	}

	public boolean isLoggedIn() {
		return this.loggedIn;
	}

	public void login(String username, String password) {
		
	}
}