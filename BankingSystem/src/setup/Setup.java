package setup;

import shared.*;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.*;


// helper class to put some starting credentials and accounts in the files so that they can be loaded by the server

// customers:
// username "customer 1" password "customerpass1"
// username "customer 2" password "customerpass2"

// employees:
// username "employee" password "employeepass"

// accounts:
// id: 1 type: checking owners: customer 1
// id: 2 type: savings owners: customer 1 and customer 2
// id: 3 type: loc owners: customer 2

public class Setup {
	public static void main(String[] args) {
		// setup users.txt, accounts.txt, and counts.txt
		Map<Integer, BankAccount> accounts = new HashMap<Integer, BankAccount>();
		Map<String, User> users = new HashMap<String, User>();
		
		User cust1 = new Customer("customer1", "customerpass1", "customer name 1", 12345678);
		User cust2 = new Customer("customer2", "customerpass2", "customer name 2", 23456789);
		users.put("customer1", cust1);
		users.put("customer2", cust2);
		
		User empl = new Employee("employee", "employeepass", "employee name");
		users.put("employee", empl);
		
		ArrayList<String> owners = new ArrayList<String>();
		owners.add("customer1");
		
		BankAccount acc1 = new CheckingAccount(owners);
		
		owners.add("customer2");
		BankAccount acc2 = new SavingsAccount(owners, 0.05, 5000);
		
		owners.remove(0);
		BankAccount acc3 = new LOCAccount(owners, 500, 0.5, 20);
		
		accounts.put(acc1.getID(), acc1);
		accounts.put(acc2.getID(), acc2);
		accounts.put(acc3.getID(), acc3);
		
		// clears the files
		try {
			PrintWriter printer = new PrintWriter("accounts.txt");
			printer.close();
			printer = new PrintWriter("users.txt");
			printer.close();
			printer = new PrintWriter("counts.txt");
			printer.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		
		
		try {
			FileOutputStream accountFile = new FileOutputStream("accounts.txt");
			FileOutputStream userFile = new FileOutputStream("users.txt");
			
			ObjectOutputStream accountStream = new ObjectOutputStream(accountFile);
			ObjectOutputStream userStream = new ObjectOutputStream(userFile);
			
			accountStream.writeObject(accounts);
			userStream.writeObject(users);
			
			PrintWriter countWriter = new PrintWriter("counts.txt");
			countWriter.write(Integer.toString(Customer.getCustomerCount()) + "\n");
			countWriter.write(Integer.toString(Employee.getEmployeeCount()) + "\n");
			countWriter.write(Integer.toString(BankAccount.getCount()) + "\n");
			countWriter.write(Integer.toString(Transaction.getTransactionCount()) + "\n");
			
			accountStream.close();
			userStream.close();
			countWriter.close();
			accountFile.close();
			userFile.close();			
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
