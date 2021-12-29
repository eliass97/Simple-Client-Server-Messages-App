package client.threads;

import common.enums.CommandTypeEnum;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class InputFromServerThread extends Thread {

    private final DataInputStream dataInputStream;
    private final CountDownLatch latch;

    public InputFromServerThread(DataInputStream dataInputStream, CountDownLatch latch) {
        this.dataInputStream = dataInputStream;
        this.latch = latch;
    }

    public void run() {
        boolean isConnected = true;
        do {
            try {
                String receivedMessage = dataInputStream.readUTF();
                if (receivedMessage.equalsIgnoreCase(CommandTypeEnum.DISCONNECT.getValue())) {
                    isConnected = false;
                } else {
                    System.out.println(receivedMessage);
                }
            } catch (IOException ioe) {
                System.err.println("Error during server message receiving!");
                isConnected = false;
            }
        } while (isConnected);
        latch.countDown();
    }
}