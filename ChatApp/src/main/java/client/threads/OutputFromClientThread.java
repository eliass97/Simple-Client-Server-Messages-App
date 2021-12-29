package client.threads;

import common.enums.CommandTypeEnum;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class OutputFromClientThread extends Thread {

    private final DataOutputStream dataOutputStream;
    private final CountDownLatch latch;

    public OutputFromClientThread(DataOutputStream dataOutputStream, CountDownLatch latch) {
        this.dataOutputStream = dataOutputStream;
        this.latch = latch;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        try {
            String input;
            do {
                input = scanner.nextLine();
                if (!input.isEmpty()) {
                    dataOutputStream.writeUTF(input);
                    dataOutputStream.flush();
                }
            } while (!input.equalsIgnoreCase(CommandTypeEnum.DISCONNECT.getValue()));
        } catch (IOException ioe) {
            System.err.println("Error during user input!");
        }
        scanner.close();
        latch.countDown();
    }
}