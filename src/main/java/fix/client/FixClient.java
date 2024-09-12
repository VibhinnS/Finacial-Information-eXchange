package fix.client;

import fix.message.MessageType;
import fix.session.FixSessionManager;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class FixClient {
    private static final String HOST = "localhost";
    private static final int PORT = 5000;

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Example FIX message to send
            Map<String, String> fixMessage = new HashMap<>();
            fixMessage.put("8", "FIX.4.2"); // BeginString
            fixMessage.put("35", "D");      // MessageType = New Order - Single
            fixMessage.put("49", "CLIENT"); // SenderCompID
            fixMessage.put("56", "SERVER"); // TargetCompID

            String messageToSend = FixMessageParser.buildFixMessage(fixMessage);
            out.println(messageToSend);

            // Read server response
            String response = in.readLine();
            System.out.println("Server response: " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
