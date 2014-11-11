package distributedbankingui;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import account.*;

public class BranchClient extends Thread {
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private String accountNo = "";
    private String toDo = "";
    private double amount = 0.0;
    private Account finalAcc; /*= null;*/
    private String toAccountNumber = "";
      
    public BranchClient(String accno,String taccno, String todo, double amt){
        this.accountNo = accno;
        this.toAccountNumber = taccno;
        this.amount = amt;
        this.toDo = todo;
        
    }

    public void connectServer() throws UnknownHostException, IOException, ClassNotFoundException{
        Socket clientSocket = new Socket("WORKING-PC",55);
        oos = new ObjectOutputStream(clientSocket.getOutputStream()); 
        
        Account account;
        if(toDo.equals("OPEN")){
            account = new Account("ACOUNT_NAME", "CITY", 2000);
            account.setBranchId("STATE");  
            account.setId("STATE0005");            
        }
        else if(toDo.equals("DEPOSIT")){
            account = new Account(this.toDo,"",this.amount);
            account.setId(this.accountNo);
        }
        else if(toDo.equals("WITHDRAW")){
            account = new Account(this.toDo,"",this.amount);
            account.setId(this.accountNo);
        }
        else if(toDo.equals("QUERY")){
            account = new Account(this.toDo,"",this.amount);
            account.setId(this.accountNo);
        }
        else{
            account = new Account(this.toDo,this.toAccountNumber,this.amount);
            account.setId(this.accountNo);
        }
        oos.writeObject(account);            
        oos.flush();
        oos.close();
        clientSocket.close();
    } 
    
    public void run(){
        try {
            connectServer();
        } catch (UnknownHostException ex) {
            Logger.getLogger(BranchClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BranchClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BranchClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public Account getFinalAcc() {
        return this.finalAcc;
    }
    public void setFinalAcc(Account finalAcc) {
        this.finalAcc = finalAcc;
    }
}
