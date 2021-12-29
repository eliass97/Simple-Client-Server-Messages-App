package server;

import common.enums.CommandTypeEnum;
import server.services.ContactManagementService;
import server.threads.ServerRequestProcessThread;
import server.threads.ServerSocketConnectionThread;

import java.util.Scanner;

public class Server {

    private final ContactManagementService contactManagementService = ContactManagementService.getInstance();
    private static final String URL = "jdbc:mysql://localhost:3306/Test?serverTimezone=UTC";
    private static final String USERNAME = "administrator";
    private static final String PASSWORD = "123456!a";

    public static void main(String[] args) {
        new Server().init();
    }

    private void init() {
        System.out.println("Connecting to the database...");
        if (contactManagementService.connectDatabase(URL, USERNAME, PASSWORD)) {
            System.out.println("Initiating the threads...");
            new ServerSocketConnectionThread().start();
            new ServerRequestProcessThread().start();
            console();
        }
    }

    private void console() {
        System.out.println("Server is online!");
        Scanner scan = new Scanner(System.in);
        while (true) {
            String keyboardInput;
            keyboardInput = scan.nextLine();
            if (keyboardInput.equals(CommandTypeEnum.SHOW_CONTACTS.getValue())) {
                contactManagementService.printAllContacts();
            }
            if (keyboardInput.equals(CommandTypeEnum.DISCONNECT.getValue())) {
                if (contactManagementService.disconnectDatabase()) {
                    break;
                }
            }
        }
    }
}