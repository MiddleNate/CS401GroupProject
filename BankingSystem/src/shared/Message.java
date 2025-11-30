package shared;
import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {
	private final static long serialVersionUID = 111L;
	
	private MessageType type;
	private User user;
	private BankAccount account;
	private ArrayList<BankAccount> accounts;
	private Transaction transaction;
	private String text;
	
	public Message(MessageType type, User user) {
		// return an invalid message if the type does not match the valid types for this constructor
		if (type != MessageType.Login 
				&& type != MessageType.Logout 
				&& type != MessageType.Success
				&& type != MessageType.InfoRequest
				&& type != MessageType.CreateCustomer) {
			this.type = MessageType.Invalid;
			this.user = null;
			this.account = null;
			this.accounts = null;
			this.transaction = null;
			this.text = null;
		} else {
			this.type = type;
			this.user = user;
			this.account = null;
			this.accounts = null;
			this.transaction = null;
			this.text = null;
		}
	}
	
	public Message(MessageType type, User user, String text) {
		// return an invalid message if the type does not match the valid types for this constructor
		if (type != MessageType.Success) {
			this.type = MessageType.Invalid;
			this.user = null;
			this.account = null;
			this.accounts = null;
			this.transaction = null;
			this.text = null;
		} else {
			this.type = type;
			this.user = user;
			this.account = null;
			this.accounts = null;
			this.transaction = null;
			this.text = text;
		}
	}
	
	public Message(MessageType type, ArrayList<BankAccount> accounts, String text) {
		// return an invalid message if the type does not match the the valid type for this constructor
		if (type != MessageType.Info) {
			this.type = MessageType.Invalid;
			this.user = null;
			this.account = null;
			this.accounts = null;
			this.transaction = null;
			this.text = null;
		} else {
			this.type = type;
			this.user = null;
			this.account = null;
			this.accounts = accounts;
			this.transaction = null;
			this.text = text;
		}
	}
	
	public Message(MessageType type, Transaction transaction) {
		// return an invalid message if the type does not match the the valid type for this constructor
		if (type != MessageType.Transaction) {
			this.type = MessageType.Invalid;
			this.user = null;
			this.account = null;
			this.accounts = null;
			this.transaction = null;
			this.text = null;
		} else {
			this.type = type;
			this.user = null;
			this.account = null;
			this.accounts = null;
			this.transaction = transaction;
			this.text = null;
		}
	}
	
	public Message(MessageType type) {
		// return an invalid message if the type does not match the the valid type for this constructor
		if (type != MessageType.Success
				&& type != MessageType.Fail) {
			this.type = MessageType.Invalid;
			this.user = null;
			this.account = null;
			this.accounts = null;
			this.transaction = null;
			this.text = null;
		} else {
			this.type = type;
			this.user = null;
			this.account = null;
			this.accounts = null;
			this.transaction = null;
			this.text = null;
		}
	}
	
	public Message(MessageType type, BankAccount account) {
		// return an invalid message if the type does not match the the valid type for this constructor
		if (type != MessageType.OpenAccount
				&& type != MessageType.CloseAccount
				&& type != MessageType.UpdateAccount) {
			this.type = MessageType.Invalid;
			this.user = null;
			this.account = null;
			this.accounts = null;
			this.transaction = null;
			this.text = null;
		} else {
			this.type = type;
			this.user = null;
			this.account = account;
			this.accounts = null;
			this.transaction = null;
			this.text = null;
		}
	}
	
	public Message(MessageType type, String text) {
		// return an invalid message if the type does not match the the valid type for this constructor
		if (type != MessageType.Fail
				&& type != MessageType.Success
				&& type != MessageType.Info) {
			this.type = MessageType.Invalid;
			this.user = null;
			this.account = null;
			this.accounts = null;
			this.transaction = null;
			this.text = null;
		} else {
			this.type = type;
			this.user = null;
			this.account = null;
			this.accounts = null;
			this.transaction = null;
			this.text = text;
		}
	}
	
	public Message(MessageType type, BankAccount account, User user) {
		// return an invalid message if the type does not match the the valid type for this constructor
		if (type != MessageType.AddToAccount
				&& type !=MessageType.RemoveFromAccount) {
			this.type = MessageType.Invalid;
			this.user = null;
			this.account  = null;
			this.accounts = null;
			this.transaction = null;
			this.text = null;
		} else {
			this.type = type;
			this.user = user;
			this.account = account;
			this.accounts = null;
			this.transaction = null;
			this.text = null;
		}
	}
	
	public MessageType getType() {
		return type;
	}
	
	public User getUser() {
		return user;
	}
	
	public BankAccount getAccount() {
		return account;
	}
	
	public ArrayList<BankAccount> getAccounts() {
		return accounts;
	}
	
	public Transaction getTransaction() {
		return transaction;
	}
	
	public String getText() {
		return text;
	}
}
