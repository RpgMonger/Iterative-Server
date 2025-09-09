import java.io.*;
import java.net.*;
import java.util.*;

public class MultiClient {

    private static final String[] VALID_COMMANDS = {"date", "uptime", "memory", "netstat", "users", "processes"};

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter server IP address: ");
        String serverIP = scanner.nextLine().trim();

        System.out.print("Enter server port: ");
        int port = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Enter command (date, uptime, memory, netstat, users, processes): ");
        String command = scanner.nextLine().trim().toLowerCase();

        if (!Arrays.asList(VALID_COMMANDS).contains(command)) {
            System.out.println("Invalid command. Exiting.");
            return;
        }

        System.out.print("Enter number of client requests to send (1, 5, 10, 15, 20, 25): ");
        int numClients = Integer.parseInt(scanner.nextLine().trim());

        ClientWorker[] workers = new ClientWorker[numClients];
        Thread[] threads = new Thread[numClients];

        // Start all threads
        for (int i = 0; i < numClients; i++) {
            workers[i] = new ClientWorker(serverIP, port, command);
            threads[i] = new Thread(workers[i]);
            threads[i].start();
        }

        // Wait for all threads to finish
        long totalTime = 0;
        for (int i = 0; i < numClients; i++) {
            try {
                threads[i].join();
                totalTime += workers[i].getTurnaroundTime();
                System.out.printf("Client %2d Turnaround Time: %.2f ms\n", i + 1, workers[i].getTurnaroundTime() / 1_000_000.0);
            } catch (InterruptedException e) {
                System.err.println("Thread interrupted: " + e.getMessage());
            }
        }

        double avgTime = totalTime / (double) numClients / 1_000_000.0;
        System.out.printf("\nTotal Turnaround Time: %.2f ms\n", totalTime / 1_000_000.0);
        System.out.printf("Average Turnaround Time: %.2f ms\n", avgTime);
    }
}
