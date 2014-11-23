package snow.weather.server;

import snow.Util;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ServerRunnable implements Runnable {

    private final ExecutorService executor;
    private ServerSocket serverSocket;

    public ServerRunnable() {
        executor = Executors.newFixedThreadPool(2);
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(Util.getServerPort());
            try (
                    ServerSocket socket = serverSocket
            ) {
                while (!Thread.currentThread().isInterrupted()) {
                    executor.execute(new ClientRequestHandler(socket.accept()));
                }
            }
        } catch (SocketException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            shutdownAndAwaitTermination();
        }
    }

    void shutdownAndAwaitTermination() {
        executor.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                executor.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!executor.awaitTermination(10, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            executor.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    public void stop() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }
}
