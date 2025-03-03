import java.io.*;
import java.net.*;

public class WriterClient {
    public static void main(String[] args) {
        int count = 1;
        while (true) {
            try (Socket socket = new Socket("localhost", 5000);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                String newData = "New Data " + count;
                out.println("WRITE:" + newData); // Отправляем запрос на запись
                String response = in.readLine();
                System.out.println("Писатель отправил: " + newData + ", сервер ответил: " + response);
                count++;

            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(3000); // Пауза перед следующим изменением
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
