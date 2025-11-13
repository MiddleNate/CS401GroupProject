import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {
	private final static long serialVersionUID = 111L;
	
	private MessageType type;
	private User user;
	private BankAccount account;
	private ArrayList<BankAccount> accounts;
	private Transaction transaction;
	
	public Message(MessageType type, User user) {
		// return an invalid message if the type does not match the valid types for this constructor
		if (type != MessageType.Login 
				&& type != MessageType.Logout 
				&& type != MessageType.InfoRequest
				&& type != MessageType.CreateCustomer) {
			this.type = MessageType.Invalid;
			this.user = null;
			account = null;
			accounts = null;
			transaction = null;
		} else {
			this.type = type;
			this.user = user;
			account = null;
			accounts = null;
			transaction = null;
		}
	}
	
	public Message(MessageType type, ArrayList<BankAccount> accounts) {
		// return an invalid message if the type does not match the the valid type for this constructor
		if (type != MessageType.Info) {
			this.type = MessageType.Invalid;
			user = null;
			account = null;
			this.accounts = null;
			transaction = null;
		} else {
			this.type = type;
			user = null;
			account = null;
			this.accounts = accounts;
			transaction = null;
		}
	}
	
	public Message(MessageType type, Transaction transaction) {
		// return an invalid message if the type does not match the the valid type for this constructor
		if (type != MessageType.Transaction) {
			this.type = MessageType.Invalid;
			user = null;
			account = null;
			accounts = null;
			this.transaction = null;
		} else {
			this.type = type;
			user = null;
			account = null;
			this.accounts = null;
			this.transaction = transaction;
		}
	}
	
	public Message(MessageType type) {
		// return an invalid message if the type does not match the the valid type for this constructor
		if (type != MessageType.Success
				&& type != MessageType.Fail) {
			this.type = MessageType.Invalid;
			user = null;
			account = null;
			accounts = null;
			transaction = null;
		} else {
			this.type = type;
			user = null;
			account = null;
			accounts = null;
			transaction = null;
		}
	}
	
	public Message(MessageType type, BankAccount account) {
		// return an invalid message if the type does not match the the valid type for this constructor
		if (type != MessageType.OpenAccount
				&& type != MessageType.CloseAccount
				&& type != MessageType.UpdateAccount) {
			this.type = MessageType.Invalid;
			user = null;
			this.account = null;
			accounts = null;
			transaction = null;
		} else {
			this.type = MessageType.Invalid;
			user = null;
			this.account = account;
			accounts = null;
			transaction = null;
		}
	}
}
