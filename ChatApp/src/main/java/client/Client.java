package client;

import client.threads.InputFromServerThread;
import client.threads.OutputFromClientThread;
import common.enums.ConnectionTypeEnum;
import common.enums.ConnectionValidationEnum;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class Client {

    private static final String serverIP = "localhost";
    private static final int serverPort = 10101;
    private OutputStream outputStream;
    private InputStream inputStream;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private Socket connection;
    private String username, password, connectionType;
    private final CountDownLatch latch = new CountDownLatch(2);

    public static void main(String[] args) {
        new Client().init();
    }

    private void init() {
        initializeConnection();
        getUserCredentials();
        loginOrRegister();
        closeConnection();
    }

    private void initializeConnection() {
        try {
            connection = new Socket(serverIP, serverPort);
            inputStream = connection.getInputStream();
            outputStream = connection.getOutputStream();
            dataInputStream = new DataInputStream(inputStream);
            dataOutputStream = new DataOutputStream(outputStream);
        } catch (UnknownHostException unk) {
            System.err.println("Host is unreachable!");
        } catch (IOException ioe) {
            System.err.println("Error during the connection with the server!");
        }
    }

    private void getUserCredentials() {
        Scanner scan = new Scanner(System.in);
        System.out.print("Login or Register (L/R): ");
        connectionType = scan.nextLine();
        System.out.print("Enter username: ");
        username = scan.nextLine();
        System.out.print("Enter password: ");
        password = scan.nextLine();
    }

    private void loginOrRegister() {
        try {
            if (connectionType.equalsIgnoreCase("L")) {
                dataOutputStream.writeUTF(ConnectionTypeEnum.LOGIN.getValue());
                sendUsernameAndPassword(username, password);
                validateServerReply();
            } else if (connectionType.equalsIgnoreCase("R")) {
                dataOutputStream.writeUTF(ConnectionTypeEnum.REGISTER.getValue());
                sendUsernameAndPassword(username, password);
                validateServerReply();
            } else {
                dataOutputStream.writeUTF(ConnectionTypeEnum.CANCEL.getValue());
            }
        } catch (IOException ioe) {
            System.err.println("Error during the connection with the server!");
        }
    }

    private void sendUsernameAndPassword(String username, String password) {
        try {
            dataOutputStream.writeUTF(username);
            dataOutputStream.flush();
            dataOutputStream.writeUTF(password);
            dataOutputStream.flush();
        } catch (IOException ioe) {
            System.err.println("Error during the connection with the server!");
        }
    }

    private void validateServerReply() {
        try {
            ConnectionValidationEnum reply = ConnectionValidationEnum.valueOf(dataInputStream.readUTF());
            if (reply == ConnectionValidationEnum.ACCEPT) {
                startCommunicationThreads();
            } else {
                System.out.println("Connection rejected!");
            }
        } catch (IOException ioe) {
            System.err.println("Error during the connection with the server!");
        }
    }

    private void startCommunicationThreads() {
        new InputFromServerThread(dataInputStream, latch).start();
        new OutputFromClientThread(dataOutputStream, latch).start();
        if (connectionType.equalsIgnoreCase("R")) {
            System.out.println("Welcome!");
        } else {
            System.out.println("Welcome back!");
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        try {
            inputStream.close();
            outputStream.close();
            connection.close();
        } catch (NullPointerException npe) {
            System.err.println("Failed to connect to the server!");
        } catch (IOException ioe2) {
            System.err.println("Failed to close the connection properly!");
        }
    }
}