package distributedbanking;
import account.Account;
import java.io.*;
import java.util.*;

public class BankDatabase implements Serializable {
    private static final long serialVersionUID = 1L;
    private AccountListServer accountList;
    private static BankDatabase database;

    public BankDatabase(){
        accountList = AccountListServer.instance();
    }

    public static BankDatabase instance() throws IOException, ClassNotFoundException{
        if(database == null){
                return(database = new BankDatabase());
        }else{
                return database;
        }
    }

    public void addAccount(Account account){
        accountList.insertAccount(account);
    }

    public Iterator getAllAccounts(){
        return accountList.getAccountsList();
    }

    public static  boolean save() {
        try {
          FileOutputStream file = new FileOutputStream("BankData");
          ObjectOutputStream output = new ObjectOutputStream(file);
          output.writeObject(database);

          return true;
        } catch(IOException ioe) {
          ioe.printStackTrace();
          return false;
        }
      }
    public static BankDatabase retrieve() {
        try {
          FileInputStream file = new FileInputStream("BankData");
          ObjectInputStream input = new ObjectInputStream(file);
          input.readObject();
          return database;
          } catch(IOException ioe) {
          ioe.printStackTrace();
          return null;
          } catch(ClassNotFoundException cnfe) {
          cnfe.printStackTrace();
          return null;
          }
    }
    private void writeObject(java.io.ObjectOutputStream output) {
            try {
          output.defaultWriteObject();
          output.writeObject(database);
        } catch(IOException ioe) {
          System.out.println(ioe);
        }
      }
    private void readObject(java.io.ObjectInputStream input) {
        try {
          input.defaultReadObject();
          if (database == null) {
            database = (BankDatabase) input.readObject();
          } else {
            input.readObject();
          }
        } catch(IOException ioe) {
          ioe.printStackTrace();
        } catch(Exception e) {
          e.printStackTrace();
        }
      }
}
