import java.io.*;
import java.net.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SharedResourceServer {
    private static String data = "Initial Data";
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("Сервер запущен на порту 5000...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private final Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
            ) {
                String request = in.readLine();

                if (request.equals("READ")) {
                    lock.readLock().lock();
                    try {
                        out.println(data);
                        System.out.println("Читатель получил данные: " + data);
                    } finally {
                        lock.readLock().unlock();
                    }
                } else if (request.startsWith("WRITE:")) {
                    lock.writeLock().lock();
                    try {
                        data = request.substring(6);
                        out.println("OK");
                        System.out.println("Писатель изменил данные: " + data);
                    } finally {
                        lock.writeLock().unlock();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
