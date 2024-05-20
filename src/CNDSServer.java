import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class CNDSServer extends UnicastRemoteObject implements CNDSInterface {
    private final List<String> participants = new CopyOnWriteArrayList<>();
    private final Map<String, String> infectedParticipants = new HashMap<>();
    private int participantCounter = 0;
    private static final int MAX_PARTICIPANTS = 100;
    
   
    public CNDSServer() throws RemoteException {
        super();
    }

    @Override
    public synchronized String registerParticipant() throws RemoteException {
        if (participants.size() >= MAX_PARTICIPANTS) {
            throw new RemoteException("Maximum number of participants reached.");
        }
        String participantName = "Participant" + (++participantCounter);
        participants.add(participantName);
        System.out.println(participantName + " has joined the CNDS.");
        return participantName;
    }

    @Override
    public synchronized void unregisterParticipant(String participantName) throws RemoteException {
        if (participants.contains(participantName)) {
            participants.remove(participantName);
            System.out.println(participantName + " has left the CNDS.");
        } else {
            throw new RemoteException("Participant not found or already removed: " + participantName);
        }
    }

    @Override
    public void notifyInfection(String participantName, int yesAnswers) throws RemoteException {
        if (yesAnswers >= 2) {
            String infectionTime = java.time.LocalDateTime.now().toString();
            infectedParticipants.put(participantName, infectionTime);
            System.out.println("Notification: " + participantName + " has reported COVID-19 symptoms at " + infectionTime);
        }
    }

    @Override
    public List<String> getInfectedParticipants() throws RemoteException {
        List<String> infectedDetails = new CopyOnWriteArrayList<>();
        for (Map.Entry<String, String> entry : infectedParticipants.entrySet()) {
            infectedDetails.add(entry.getKey() + " - Infected at: " + entry.getValue());
        }
        return infectedDetails;
    }
    
    @Override
    public String getInfectionTime(String participantName) throws RemoteException {
        return infectedParticipants.getOrDefault(participantName, "");
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
