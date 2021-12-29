package server.threads;

import common.enums.ConnectionTypeEnum;
import common.enums.ConnectionValidationEnum;
import server.services.ContactManagementService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

public class ServerConnectionProcessThread extends Thread {

    private final ContactManagementService contactManagementService = ContactManagementService.getInstance();
    private final Socket connection;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public ServerConnectionProcessThread(Socket connection) {
        this.connection = connection;
        try {
            dataInputStream = new DataInputStream(connection.getInputStream());
            dataOutputStream = new DataOutputStream(connection.getOutputStream());
        } catch (IOException e) {
            System.err.println("server.thread.ServerConnectionProcessThread -> ruServerConnectionProcessThread -> Error during connection with a client!");
        }
    }

    public void run() {
        try {
            ConnectionTypeEnum type = ConnectionTypeEnum.fromValueOrNull(dataInputStream.readUTF());
            if (type == null || type == ConnectionTypeEnum.CANCEL) {
                reject();
            }
            String username = dataInputStream.readUTF();
            String password = dataInputStream.readUTF();
            boolean usernameAvailable = !contactManagementService.contactExists(username);
            if (type == ConnectionTypeEnum.REGISTER && usernameAvailable) {
                register(username, password);
            } else if (type == ConnectionTypeEnum.LOGIN && !usernameAvailable) {
                login(username, password);
            } else {
                reject();
            }
        } catch (IOException ioe) {
            System.err.println("server.thread.ServerConnectionProcessThread -> run -> Error during the validation of connection with a client!");
        }
    }

    private void register(String username, String password) {
        try {
            boolean contactCreated = contactManagementService.addContact(connection, username, password);
            if (contactCreated) {
                UUID id = contactManagementService.findIdByUsername(username);
                new InputFromClientThread(id, username, dataInputStream).start();
                dataOutputStream.writeUTF(ConnectionValidationEnum.ACCEPT.name());
                System.out.println(username + " has been connected to the server!");
                contactManagementService.sendMessageToAllContacts("[Server] " + username + " has been connected", id);
            } else {
                reject();
            }
        } catch (IOException ioe) {
            System.err.println("server.thread.ServerConnectionProcessThread -> register -> Error during connection!");
        }
    }

    private void login(String username, String password) {
        try {
            boolean ok = contactManagementService.reconnectContact(connection, username, password);
            if (ok) {
                UUID id = contactManagementService.findIdByUsername(username);
                new InputFromClientThread(id, username, dataInputStream).start();
                dataOutputStream.writeUTF(ConnectionValidationEnum.ACCEPT.name());
                System.out.println(username + " has been reconnected to the server!");
                contactManagementService.sendMessageToAllContacts("[Server] " + username + " has been reconnected", id);
            } else {
                reject();
            }
        } catch (IOException ioe) {
            System.err.println("server.thread.ServerConnectionProcessThread -> login -> Error during connection!");
        }
    }

    private void reject() {
        try {
            dataOutputStream.writeUTF(ConnectionValidationEnum.REJECT.name());
        } catch (IOException ioe) {
            System.err.println("server.thread.ServerConnectionProcessThread -> close -> Error while rejecting a contact!");
        }
    }
}