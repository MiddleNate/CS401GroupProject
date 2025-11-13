
public class Employee extends User {
	private String employeeName;
	private static int employeeCount = 0;
	private int id;

	public Employee(String username, String password, boolean loggedIn, String employeeName) {
		super(username, password);
		this.employeeName = employeeName;
		this.id = ++employeeCount;
	}
	
	public static void setEmployeeCount(int newCount) {
		employeeCount = newCount;
	}
	
	public static int getEmployeeCount() {
		return employeeCount;
	}
	
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	
	public String getEmployeeName() {
		return this.employeeName;
	}
	
	public int getEmployeeID() {
		return this.id;
	}
}