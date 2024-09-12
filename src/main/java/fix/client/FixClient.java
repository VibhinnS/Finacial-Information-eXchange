package fix.client;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

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
        }
    }
}
