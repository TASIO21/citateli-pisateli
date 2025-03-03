import java.io.*;
import java.net.*;

public class ReaderClient {
    public static void main(String[] args) {
        while (true) {
            try (Socket socket = new Socket("localhost", 5000);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                out.println("READ"); // Отправляем запрос на чтение
                String response = in.readLine();
                System.out.println("Читатель получил: " + response);

            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(2000); // Пауза перед следующим чтением
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
