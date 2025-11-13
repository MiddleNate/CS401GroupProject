
public class User extends Throwable implements Serializable{
	private static final long serialVersionUID = 250L;
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

	public void tryLogin(String username, String password) throws Exception {
		if (loggedIn) {
			throw new Exception("Already logged in");
		}
		else if (username.compareTo(this.username) != 0 && password.compareTo(this.password) != 0) {
			throw new Exception("Invalid username or password");
		} else {
			loggedIn = true;
		}
	}
}