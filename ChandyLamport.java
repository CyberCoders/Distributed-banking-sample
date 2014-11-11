package distributedbanking;

public class ChandyLamport {
    private int noOfMsgRcvd;
    private int noOfMsgSend;
    private double amountInBranch ;
    private double amountInTransit ;
    private int processState;


    public ChandyLamport(){
        reset();
    }//end of constructor

    public void reset(){
        this.amountInBranch = 0.0;
        this.amountInTransit = 0.0;
        this.noOfMsgRcvd = 0;
        this.noOfMsgSend = 0;
        this.processState = 0;
    }

    public int checkState(){
        return this.processState;
    }

    public void setState(int state){
        this.processState = state;
    }

    public void setLocalState(double amtInBranch){
        this.amountInBranch = amtInBranch;
    }
	
	public void setChannelState(double amtInTransit){
        this.amountInTransit += amtInTransit;
    }
    
    public void setMessageSend(){
        this.noOfMsgSend += 1;
    }

    public void setMessageRcvd(){
        this.noOfMsgRcvd += 1;
    }
    
    public int getMessageSend(){
        return this.noOfMsgSend;
    }

    public int getMessageRcvd(){
        return this.noOfMsgRcvd;
    }

    public double collectLocalState(){
        return this.amountInBranch;
    }

    public double collectChannelState(){
        return this.amountInTransit;
    }

}
