import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class CNDSClient {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            CNDSInterface cnds = (CNDSInterface) registry.lookup("CNDS");
            
            // Use remote methods
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
    
    
    
}
