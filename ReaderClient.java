import java.io.*;
import java.net.*;

public class ReaderClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 5000);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println("READ");
            String response = in.readLine();
            System.out.println("Reader received: " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
