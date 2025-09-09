import java.io.*;
import java.net.*;
import java.util.*;

public class IterativeServer {

    private static final int PORT = 2201; // Change if needed

    public static void main(String[] args) {
        System.out.println("Iterative Server is starting on port " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                try (
                    Socket clientSocket = serverSocket.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
                ) {
                    System.out.println("Connected to client: " + clientSocket.getInetAddress());

                    String command = in.readLine();
                    if (command != null) {
                        System.out.println("Received command: " + command);
                        String response = executeCommand(command.trim().toLowerCase());
                        out.println(response);
                    }
                } catch (IOException e) {
                    System.err.println("Client connection error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Could not start server: " + e.getMessage());
        }
    }

    private static String executeCommand(String request) {
        String systemCommand;

        switch (request) {
            case "date":
                systemCommand = "date";
                break;
            case "uptime":
                systemCommand = "uptime";
                break;
            case "memory":
                systemCommand = "free -h";
                break;
            case "netstat":
                systemCommand = "netstat -tunapl";
                break;
            case "users":
                systemCommand = "who";
                break;
            case "processes":
                systemCommand = "ps -ef";
                break;
            default:
                return "Invalid command: " + request;
        }

        return runSystemCommand(systemCommand);
    }

    private static String runSystemCommand(String cmd) {
        StringBuilder output = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            process.waitFor();
        } catch (IOException | InterruptedException e) {
            return "Error running command: " + e.getMessage();
        }

        return output.toString().trim();
    }
}
