package distributedbanking;

import java.util.*;
import java.lang.*;
import java.io.*;
import account.*;

public class AccountListServer implements Serializable {
private static final long serialVersionUID = 1L;
private List accounts = new LinkedList();

private static AccountListServer accountList;
private AccountListServer() {
}
public static AccountListServer instance() {
	if (accountList == null) {
	  return (accountList = new AccountListServer1());
	} else {
	  return accountList;
	}
}
public Account search(String AccountId) {
		for (Iterator iterator = accounts.iterator(); iterator.hasNext(); ) {
		  Account Account = (Account) iterator.next();
		  if (Account.getId().equals(AccountId)) {
			return Account;
		  }
		}
		return null;
}
public boolean insertAccount(Account account) {
	accounts.add(account);
	return true;
}

public Iterator getAccountsList(){
	return accounts.iterator();
}

private void writeObject(java.io.ObjectOutputStream output) {
	try {
	  output.defaultWriteObject();
	  output.writeObject(accountList);
	} catch(IOException ioe) {
	  System.out.println(ioe);
	}
  }
private void readObject(java.io.ObjectInputStream input) {
	try {
	  if (accountList != null) {
		return;
	  } else {
		input.defaultReadObject();
		if (accountList == null) {
			accountList = (AccountListServer1) input.readObject();
		} else {
		  input.readObject();
		}
	  }
	} catch(IOException ioe) {
	  System.out.println(" Catalog \n" + ioe);
	} catch(ClassNotFoundException cnfe) {
	  cnfe.printStackTrace();
	}
  }
public String toString() {
	return accountList.toString();
}
}
