package shared;

public class Employee extends User {
	private static final long serialVersionUID = 61L;
	private String employeeName;
	private static int employeeCount = 0;
	private int id;

	public Employee(String username, String password, String employeeName) {
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