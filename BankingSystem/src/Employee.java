
public class Employee extends User {
	private String employeeName;
	private static int count = 0;
	private int employeeID;

	public Employee(String username, String password, String employeeName) {
		super(username, password);
		this.employeeName = employeeName;
		this.employeeID = count++;
	}
	
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	
	public String getEmployeeName() {
		return this.employeeName;
	}
	
	public int getEmployeeID() {
		return this.employeeID;
	}
}