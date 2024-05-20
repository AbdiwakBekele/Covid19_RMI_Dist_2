import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class CNDSServer extends UnicastRemoteObject implements CNDSInterface {
    private List<String> infectedParticipants;

    public CNDSServer() throws RemoteException {
        infectedParticipants = new ArrayList<>();
    }

    @Override
    public void registerParticipant(String participantName) throws RemoteException {
        // Implementation
    }

    @Override
    public void unregisterParticipant(String participantName) throws RemoteException {
        // Implementation
    }

    @Override
    public void notifyInfection(String participantName) throws RemoteException {
        // Implementation
    }

    @Override
    public String[] getInfectedParticipants() throws RemoteException {
        // Implementation
        return null;
    }
    
     public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("CNDS", new CNDSServer());
            System.out.println("Server started...");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
