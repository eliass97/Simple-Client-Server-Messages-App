package server.threads;

import common.enums.CommandTypeEnum;
import common.models.Request;
import server.services.RequestManagementService;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public class InputFromClientThread extends Thread {

    private final RequestManagementService requestManagementService = RequestManagementService.getInstance();
    private final DataInputStream dataInputStream;
    private final String username;
    private final UUID id;

    public InputFromClientThread(UUID id, String username, DataInputStream dataInputStream) {
        this.username = username;
        this.id = id;
        this.dataInputStream = dataInputStream;
    }

    public void run() {
        boolean isConnected = true;
        do {
            try {
                String receivedMessage = dataInputStream.readUTF();
                System.out.println(username + ": " + receivedMessage);
                requestManagementService.addRequest(new Request(id, receivedMessage));
                if (receivedMessage.equals("/disconnect")) {
                    isConnected = false;
                }
            } catch (IOException ioe) {
                System.err.println("server.thread.InputFromClientThread -> run -> Connection with " + username + " lost unexpectedly");
                requestManagementService.addRequest(new Request(id, CommandTypeEnum.DISCONNECT.getValue()));
                isConnected = false;
            }
        } while (isConnected);
    }
}