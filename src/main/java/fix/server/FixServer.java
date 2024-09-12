package fix.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

class FixMessageParser {
    public static Map<String, String> parseFixMessage(String fixMessage) {
        Map<String, String> messageMap = new HashMap<>();
        String[] keyValuePairs = fixMessage.split("\\|");
        for (String keyValue : keyValuePairs) {
            String[] pair = keyValue.split("=");
            if (pair.length == 2) {
                messageMap.put(pair[0], pair[1]);
            }
        }
        return messageMap;
    }

    public static String buildFixMessage(Map<String, String> fields) {
        StringBuilder builder = new StringBuilder();
        fields.forEach((key, value) -> builder.append(key).append("=").append(value).append("|"));
        return builder.toString();
    }
}

class FixSessionManager {
    private final Map<String, FixSession> sessions = new HashMap<>();

    public FixSession createSession(String sessionId) {
        FixSession session = new FixSession(sessionId);
        sessions.put(sessionId, session);
        return session;
    }

    public FixSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    public void handleHeartbeat(String sessionId) {
        FixSession session = sessions.get(sessionId);
        if (session != null) {
            session.updateLastHeartbeat();
        }
    }
}

class FixSession {
    private final String sessionId;
    private long lastHeartbeat;

    public FixSession(String sessionId) {
        this.sessionId = sessionId;
        this.lastHeartbeat = System.currentTimeMillis();
    }

    public void updateLastHeartbeat() {
        this.lastHeartbeat = System.currentTimeMillis();
    }

    public boolean isAlive() {
        return (System.currentTimeMillis() - lastHeartbeat) < 60000;
    }
}


public class FixServer {
    private static final int PORT = 5000;
    private final FixSessionManager sessionManager = new FixSessionManager();
    private final ExecutorService executorService = Executors.newFixedThreadPool(10); 
    private final AtomicInteger connectionCount = new AtomicInteger(0);

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
                Map<String, String> parsedMessage = FixMessageParser.parseFixMessage(message);
                if (parsedMessage.containsKey("35") && "0".equals(parsedMessage.get("35"))) {  // Heartbeat message
                    sessionManager.handleHeartbeat(sessionId);
                }

                out.println("Response: " + message);
            }
        } catch (IOException e) {
        } finally {
            connectionCount.decrementAndGet(); // Decrement connection count
            System.out.println("Active connections: " + connectionCount.get());
        }
    }
}
