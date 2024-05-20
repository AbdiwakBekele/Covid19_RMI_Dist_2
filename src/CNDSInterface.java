import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CNDSInterface extends Remote {
    void registerParticipant(String participantName) throws RemoteException;
    void unregisterParticipant(String participantName) throws RemoteException;
    void notifyInfection(String participantName) throws RemoteException;
    String[] getInfectedParticipants() throws RemoteException;
}
