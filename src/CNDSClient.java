import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;

public class CNDSClient extends UnicastRemoteObject implements CNDSClientCallback {

    private static CNDSInterface serverStub;
    private static String participantName;

    protected CNDSClient() throws RemoteException {
        super();
    }

    @Override
    public void notifyNewInfection(String participantName, String infectionTime) throws RemoteException {
        System.out.println("Notification: " + participantName + " has reported COVID-19 symptoms at " + infectionTime);
    }

    private boolean leaveCNDS() {
        try {
            if ("Participant1".equals(participantName)) {
                System.out.println("The first participant cannot leave the CNDS.");
                return false;
            } else {
                serverStub.unregisterParticipant(participantName);
                System.out.println("You have left CNDS.");
                return true;
            }
        } catch (Exception e) {
            System.err.println("Error trying to leave CNDS: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void notifyCovid(Scanner scanner) {
        int yesCount = 0;
        String[] questions = {
                "Have you experienced a fever in the last 48 hours?",
                "Have you had any respiratory issues or coughing?",
                "Have you been in contact with a confirmed COVID-19 case?"
        };

        for (String question : questions) {
            System.out.println(question + " (yes/no)");
            String answer = scanner.nextLine().trim().toLowerCase();
            if ("yes".equals(answer)) {
                yesCount++;
            }
        }

        if (yesCount >= 2) {
            try {
                serverStub.notifyInfection(participantName, yesCount);
                System.out.println("You have reported symptoms and your status has been sent to the server.");
            } catch (Exception e) {
                System.err.println("Error trying to send infection notification: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("You have not reported enough symptoms. Your status has not been sent to the server.");
        }
    }

    private void displayInfectedParticipants() {
        try {
            List<String> infected = serverStub.getInfectedParticipants();
            if (infected.isEmpty()) {
                System.out.println("There are no reported infections at this time.");
            } else {
                System.out.println("List of infected participants:");
                for (String infectedParticipant : infected) {
                    System.out.println(infectedParticipant);
                }
            }
        } catch (Exception e) {
            System.err.println("Error trying to display infected participants: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void removeFromInfectedList() {
        try {
            serverStub.removeInfectedParticipant(participantName);
            System.out.println("You have been removed from the infected list.");
        } catch (Exception e) {
            System.err.println("Error trying to remove from infected list: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            serverStub = (CNDSInterface) registry.lookup("CNDS");
            participantName = serverStub.registerParticipant();
            System.out.println("You have joined as " + participantName);

            CNDSClient client = new CNDSClient();
            serverStub.registerCallback(client); // Register the client callback with the server

            Scanner scanner = new Scanner(System.in);
            String choice;
            Boolean choiceStatus = false;
            do {
                System.out.println("\n1. Notify COVID-19 symptoms");
                System.out.println("2. View list of infected participants");
                System.out.println("3. Leave CNDS");
                System.out.println("4. Exit");
                System.out.print("Enter choice: ");
                choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        client.notifyCovid(scanner);
                        break;
                    case "2":
                        client.displayInfectedParticipants();
                        break;
                    case "3":
                        client.removeFromInfectedList();
                        break;
                    case "4":
                        choiceStatus = client.leaveCNDS();
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter 1, 2, 3 or 4");
                        break;
                }
            } while (!choiceStatus);
            System.out.println("Exiting...");

            scanner.close();
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
