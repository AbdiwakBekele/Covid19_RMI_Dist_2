import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface CNDSInterface extends Remote {
    String registerParticipant() throws RemoteException;

    void unregisterParticipant(String participantName) throws RemoteException;

    void notifyInfection(String participantName, int yesAnswers) throws RemoteException;

    List<String> getInfectedParticipants() throws RemoteException;

    String getInfectionTime(String participantName) throws RemoteException;

    void removeInfectedParticipant(String participantName) throws RemoteException;

    void registerCallback(CNDSClientCallback callback) throws RemoteException; // New method

    void unregisterCallback(CNDSClientCallback callback) throws RemoteException; // New method
}
