
public class Employee extends User {
	private String employeeName;
	private static int employeeID = 0;
	private int id;

	public Employee(String username, String password, boolean loggedIn, String employeeName) {
		super(username, password);
		employeeID++;
		this.employeeName = employeeName;
		this.id = employeeID;
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