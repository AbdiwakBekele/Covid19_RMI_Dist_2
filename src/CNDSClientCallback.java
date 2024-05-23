import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CNDSClientCallback extends Remote {
    void notifyNewInfection(String participantName, String infectionTime) throws RemoteException;
}
