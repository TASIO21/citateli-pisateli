import java.util.concurrent.Semaphore;
import java.io.*;
import java.net.*;

public class SharedResourceServer {
    private static String sharedResource = "Initial Data";
    private static Semaphore readSemaphore = new Semaphore(1); // Для контроля чтения
    private static Semaphore writeSemaphore = new Semaphore(1); // Для контроля записи
    private static int readersCount = 0;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("Server started on port 5000...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Обработчик запросов от клиента
    static class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                String request = in.readLine();
                if (request.startsWith("READ")) {
                    handleRead(out);
                } else if (request.startsWith("WRITE")) {
                    handleWrite(request.substring(6), out);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void handleRead(PrintWriter out) throws IOException {
            try {
                readSemaphore.acquire();
                readersCount++;
                if (readersCount == 1) {
                    writeSemaphore.acquire();  // Блокируем писателей, если это первый читатель
                }
                readSemaphore.release();

                out.println(sharedResource);

                readSemaphore.acquire();
                readersCount--;
                if (readersCount == 0) {
                    writeSemaphore.release();  // Разблокируем писателей, если это последний читатель
                }
                readSemaphore.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void handleWrite(String data, PrintWriter out) {
            try {
                writeSemaphore.acquire();
                sharedResource = data;
                out.println("OK");
                writeSemaphore.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
