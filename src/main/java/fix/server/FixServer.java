package fix.server;

import fix.message.FixMessageParser;
import fix.session.FixSessionManager;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class FixServer {
    private static final int PORT = 5000;
    private FixSessionManager sessionManager = new FixSessionManager();
    private ExecutorService executorService = Executors.newFixedThreadPool(10); 
    private AtomicInteger connectionCount = new AtomicInteger(0);

    public static void main(String[] args) {
        new FixServer().start();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("FIX Server started on port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                connectionCount.incrementAndGet(); // Increment connection count
                System.out.println("Active connections: " + connectionCount.get());
                executorService.submit(() -> handleClient(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String sessionId = clientSocket.getInetAddress().getHostAddress();
            sessionManager.createSession(sessionId);

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Received: " + message);
                var parsedMessage = FixMessageParser.parseFixMessage(message);
                if (parsedMessage.containsKey("35") && "0".equals(parsedMessage.get("35"))) {  // Heartbeat message
                    sessionManager.handleHeartbeat(sessionId);
                }

                out.println("Response: " + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connectionCount.decrementAndGet(); // Decrement connection count
            System.out.println("Active connections: " + connectionCount.get());
        }
    }
}
