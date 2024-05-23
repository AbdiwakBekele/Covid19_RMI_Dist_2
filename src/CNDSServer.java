import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class CNDSServer extends UnicastRemoteObject implements CNDSInterface {
    private final List<String> participants = new CopyOnWriteArrayList<>();
    private final Map<String, String> infectedParticipants = new ConcurrentHashMap<>();
    private final List<CNDSClientCallback> callbacks = new CopyOnWriteArrayList<>();
    private int participantCounter = 0;
    private static final int MAX_PARTICIPANTS = 100;

    public CNDSServer() throws RemoteException {
        super();
    }

    @Override
    public String registerParticipant() throws RemoteException {
        if (participants.size() >= MAX_PARTICIPANTS) {
            throw new RemoteException("Maximum number of participants reached.");
        }
        String participantName = "Participant" + (++participantCounter);
        participants.add(participantName);
        System.out.println(participantName + " has joined the CNDS.");
        return participantName;
    }

    @Override
    public void unregisterParticipant(String participantName) throws RemoteException {
        if (participants.contains(participantName)) {
            participants.remove(participantName);
            infectedParticipants.remove(participantName); // Also remove from infected list
            System.out.println(participantName + " has left the CNDS.");
        } else {
            throw new RemoteException("Participant not found or already removed: " + participantName);
        }
    }

    @Override
    public void notifyInfection(String participantName, int yesAnswers) throws RemoteException {
        if (yesAnswers >= 2) {
            String infectionTime = LocalDateTime.now().toString();
            infectedParticipants.put(participantName, infectionTime);
            System.out.println(
                    "Notification: " + participantName + " has reported COVID-19 symptoms at " + infectionTime);
            broadcastNewInfection(participantName, infectionTime); // Broadcast to all clients
        }
    }

    @Override
    public List<String> getInfectedParticipants() throws RemoteException {
        List<String> infectedDetails = new CopyOnWriteArrayList<>();
        infectedParticipants.forEach((key, value) -> infectedDetails.add(key + " - Infected at: " + value));
        return infectedDetails;
    }

    @Override
    public String getInfectionTime(String participantName) throws RemoteException {
        return infectedParticipants.getOrDefault(participantName, "");
    }

    @Override
    public void removeInfectedParticipant(String participantName) throws RemoteException {
        if (infectedParticipants.containsKey(participantName)) {
            infectedParticipants.remove(participantName);
            System.out.println("Participant " + participantName + " has been removed from the infected list.");
        } else {
            throw new RemoteException("Participant not found in the infected list: " + participantName);
        }
    }

    @Override
    public void registerCallback(CNDSClientCallback callback) throws RemoteException {
        if (!callbacks.contains(callback)) {
            callbacks.add(callback);
        }
    }

    @Override
    public void unregisterCallback(CNDSClientCallback callback) throws RemoteException {
        callbacks.remove(callback);
    }

    private void broadcastNewInfection(String participantName, String infectionTime) throws RemoteException {
        for (CNDSClientCallback callback : callbacks) {
            callback.notifyNewInfection(participantName, infectionTime);
        }
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
