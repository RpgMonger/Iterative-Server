import java.io.*;
import java.net.*;

public class ConcurrentServer {

    private static final int PORT = 2201;

    public static void main(String[] args) {
        System.out.println("Concurrent Server is running on port " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    static class ClientHandler implements Runnable {
        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
            ) {
                String command = in.readLine();
                if (command != null) {
                    String output = executeCommand(command.trim().toLowerCase());
                    out.println(output);
                }
            } catch (IOException e) {
                System.err.println("ClientHandler error: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException ignored) {}
            }
        }

        private String executeCommand(String request) {
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
                    systemCommand = "ps -e";
                    break;
                default:
                    return "Invalid command: " + request;
            }

            return runSystemCommand(systemCommand);
        }

        private String runSystemCommand(String cmd) {
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
}
