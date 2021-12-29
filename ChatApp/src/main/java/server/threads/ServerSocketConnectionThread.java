package server.threads;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketConnectionThread extends Thread {

    private static final int SERVER_PORT = 10101;

    @SuppressWarnings("InfiniteLoopStatement")
    public void run() {
        ServerSocket provider = null;
        try {
            provider = new ServerSocket(SERVER_PORT);
            while (true) {
                Socket connection = provider.accept();
                new ServerConnectionProcessThread(connection).start();
            }
        } catch (IOException ioe) {
            System.err.println("server.thread.ServerSocketConnectionThread -> run -> Error during the creation of connection with a client!");
        } finally {
            try {
                assert provider != null;
                provider.close();
            } catch (IOException ioe2) {
                System.err.println("server.thread.ServerSocketConnectionThread -> run -> Failed to close the server socket!");
            }
        }
    }
}