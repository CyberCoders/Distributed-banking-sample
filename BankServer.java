package distributedbanking;

import java.io.*;
import java.net.*;
import java.util.Iterator;
import account.*;

public class BranchServer1  implements Serializable {
    private static final long serialVersionUID = 1L;
    private AccountListServer1 accountList;    
    private static BranchServer1 branchServer;    
    private static BankDatabase database;
    private String thisServer = "STATE";
    private int[] communicateServers = {56,57};
    private String[] branchInfo = new String[]{"B1","B2"}; 
    private ChandyLamport myChandy = new ChandyLamport();
    ObjectOutputStream oosServer;
    ObjectInputStream oisServer;
    ObjectOutputStream oosClient;
    ObjectInputStream oisClient;
    
    private ServerSocket serverSocket;

    //constructor
    public BranchServer1() throws IOException, ClassNotFoundException{
        retrieve();
        accountList = AccountListServer1.instance();
        database = BankDatabase.instance();
        serverSocket = new ServerSocket(55);
        System.out.println("Listening....");
        while(true){
            Socket clientSocket = serverSocket.accept();
            System.out.println("Connected....");
            oisServer = new ObjectInputStream(clientSocket.getInputStream());
            
            Account receivedAcc = null;
            receivedAcc = (Account)oisServer.readObject();           
           			
            String id = receivedAcc.getId();
            String branchCode = id.substring(0,2).toUpperCase();  
            if(!branchCode.equals(thisServer)){                
                if(!receivedAcc.getName().equals("TRANSFER")){
                    int index = getIndex(branchCode);
                    if(index > -1){                    
                        boolean isSuccess = searchServer(communicateServers[index],receivedAcc);                                        
                    }
                    else
                        System.out.println("Index is out of range");
                }
                else
                    System.out.println("The account should only be from the same server for transfer");
            } 
            else{
                Account queryAccount;
                if(receivedAcc.getBranchID() != null){                   
                    openAccount(receivedAcc);                    
                    queryAccount = queryAccount(receivedAcc);
                    System.out.println((queryAccount.toString()));
                }
                else if(receivedAcc.getName().equals("DEPOSIT")){
                    depositAccount(receivedAcc);
                    queryAccount = queryAccount(receivedAcc);
                    System.out.println((queryAccount.toString()));
                }
                else if(receivedAcc.getName().equals("WITHDRAW")){
                    withdrawAccount(receivedAcc);
                    queryAccount = queryAccount(receivedAcc);
                    System.out.println((queryAccount.toString()));
                }
                else if(receivedAcc.getName().equals("QUERY")){
                    myChandy.setState(2);
                    queryAccount = queryAccount(receivedAcc);
                    System.out.println((queryAccount.toString()));
                }
                else{
                    
                    transferAccount(receivedAcc);
                    queryAccount = queryAccount(receivedAcc);
                    System.out.println((queryAccount.toString()));
                }
                
                save(); 
                
                oisServer.close();                                                         
                clientSocket.close();
            }
            System.out.println("Process state 0:deposit,1:withdraw,2:query,3:transfer: "+myChandy.checkState()+"\n");
            System.out.println("Local State: "+myChandy.collectLocalState()+"\n");
            System.out.println("Channel State: "+myChandy.collectChannelState()+"\n");            
            myChandy.reset();
        }        
        
    }
    
    public int getIndex(String brCode){
        for(int i = 0; i < branchInfo.length; i++){
            if(branchInfo[i].equals(brCode)){
                return i;
            }
        }
        return -1;
    }

    public boolean searchServer(int port, Account account) throws UnknownHostException, IOException{
        if(account.getName().equals("WITHDRAW"))
            myChandy.setChannelState(-account.getBalance());
        else
            myChandy.setChannelState(account.getBalance());
        oosClient = new ObjectOutputStream(clientSocket.getOutputStream()); 
        oosClient.writeObject(account);
        oosClient.flush();
        oosClient.close();
        return true;
    }
    
    public boolean openAccount(Account raccount){       
        addAccount(raccount);
        return true;
    }
    
    public boolean depositAccount(Account raccount){
        myChandy.setLocalState(raccount.getBalance());
        myChandy.setState(0);
        Account theAccount = accountList.search(raccount.getId());
        theAccount.setBalance(theAccount.getBalance() + raccount.getBalance());
        return true;
    }
    
    public boolean withdrawAccount(Account raccount){
        myChandy.setState(1);
        Account theAccount = accountList.search(raccount.getId());
        if(theAccount.getBalance() >= raccount.getBalance()){
            myChandy.setLocalState(-raccount.getBalance());
            theAccount.setBalance(theAccount.getBalance() - raccount.getBalance());
            return true;
        }
        else
            return false;
    }
    
    public Account queryAccount(Account raccount){
        Account theAccount = accountList.search(raccount.getId());
        return theAccount;        
    }
    
    public boolean transferAccount(Account raccount) throws UnknownHostException, IOException{
        myChandy.setState(3);
        Account fromAccount = accountList.search(raccount.getId());
        String id1;
        if(fromAccount.getBalance() >= raccount.getBalance()){
            myChandy.setLocalState(-raccount.getBalance());
            fromAccount.setBalance(fromAccount.getBalance() - raccount.getBalance());
            id1 = raccount.getId();
            String id2 = raccount.getAddress();
            String branchCode = id2.substring(0,2).toUpperCase(); 
            if(!branchCode.equals(thisServer)){
                int index = getIndex(branchCode);
                if(index > -1){            
                    raccount.setId(id2);
                    raccount.setName("DEPOSIT");
                    boolean isSuccess = searchServer(communicateServers[index],raccount);                                        
                }
                else
                    System.out.println("Index is out of range");
            }
            else{
                myChandy.setLocalState(raccount.getBalance());
                Account toAccount = accountList.search(raccount.getAddress());
                toAccount.setBalance(toAccount.getBalance() + raccount.getBalance());
            }
            raccount.setId(id1);
            return true;
        }
        return false;
    }    
    
    public static BranchServer instance() throws IOException, ClassNotFoundException{
        if(branchServer == null){
                return(branchServer = new BranchServer1());
        }else{
                return branchServer;
        }
    }

    public void addAccount(Account account){        
        accountList.insertAccount(account);
    }

    public Iterator getAllAccounts(){
        return accountList.getAccountsList();
    }
    
    private static void retrieve() {
        try {
          BankDatabase temp = BankDatabase.retrieve();
          if (temp != null) {
           System.out.println(" Retrieved from the file. \n" );
            database = temp;
          } else {
            System.out.println("File doesnt exist." );
            database = BankDatabase.instance();
          }
        } catch(Exception cnfe) {
          cnfe.printStackTrace();
        }
    }

    private static void save() {
        if (database.save()) {
          System.out.println(" Saved in the file \n" );
        } else {
          System.out.println(" Error in saving \n" );
        }
    }

}
