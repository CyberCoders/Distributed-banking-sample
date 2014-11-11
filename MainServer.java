package distributedbanking;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainServer1 {

    public static void main(String[] args) {
        try {
            BranchServer1.instance();
        } catch (IOException ex) {
            Logger.getLogger(MainServer1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MainServer1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
