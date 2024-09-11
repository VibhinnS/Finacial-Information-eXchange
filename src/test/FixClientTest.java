package fix.client;

import java.io.*;
import java.net.Socket;

public class FixClientTest {
    private static final String HOST = "localhost";
    private static final int PORT = 5000;
    private static final int NUM_CLIENTS = 500; 

    public static void main(String[] args) {
        for (int i = 0; i < NUM_CLIENTS; i++) {
            new Thread(() -> {
                try (Socket socket = new Socket(HOST, PORT);
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {


                    String fixMessage = "35=A|49=SenderCompID|56=TargetCompID|11=123456|54=1"; 
                    out.println(fixMessage);

                    String response = in.readLine();
                    System.out.println("Received response: " + response);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
