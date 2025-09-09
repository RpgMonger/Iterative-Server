import java.io.*;
import java.net.*;

public class ClientWorker implements Runnable {

    private final String serverIP;
    private final int port;
    private final String command;
    private long turnaroundTime;

    public ClientWorker(String serverIP, int port, String command) {
        this.serverIP = serverIP;
        this.port = port;
        this.command = command;
    }

    public long getTurnaroundTime() {
        return turnaroundTime;
    }

    @Override
    public void run() {
        long startTime = System.nanoTime();
        try (
            Socket socket = new Socket(serverIP, port);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            out.println(command);

            // Read the response
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }

            // Optionally print the server response:
            System.out.println("Server Response:\n" + response);

        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        }
        long endTime = System.nanoTime();
        turnaroundTime = endTime - startTime;
    }
}
