package server.services;

import common.models.Contact;

import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ContactManagementService {

    private final Map<UUID, Contact> contacts = new ConcurrentHashMap<>();
    private Connection databaseConnection = null;

    private ContactManagementService() {

    }

    private static class Singleton {
        private static final ContactManagementService INSTANCE = new ContactManagementService();
    }

    public static ContactManagementService getInstance() {
        return Singleton.INSTANCE;
    }

    public boolean connectDatabase(String url, String username, String password) {
        try {
            databaseConnection = DriverManager.getConnection(url, username, password);
            String query = "select * from contact";
            PreparedStatement preparedStmt = databaseConnection.prepareStatement(query);
            ResultSet result = preparedStmt.executeQuery();
            while (result.next()) {
                Contact newContact = new Contact(UUID.fromString(result.getString("id")), result.getString("username"), result.getString("userpass"));
                contacts.put(newContact.getID(), newContact);
            }
            return true;
        } catch (Exception e) {
            System.err.println("Unexpected error during connection with the database!");
            return false;
        }
    }

    public boolean disconnectDatabase() {
        try {
            databaseConnection.close();
            return true;
        } catch (Exception e) {
            System.err.println("Error while terminating connection with database!");
            return false;
        }
    }

    public boolean addContact(Socket connection, String username, String password) {
        if (isUsernameAcceptable(username) && isPasswordAcceptable(password)) {
            UUID id = UUID.randomUUID();
            contacts.put(id, new Contact(connection, id, username, password));
            try {
                String query = "insert into contact (id, username, userpass) values (?, ?, ?)";
                PreparedStatement preparedStmt = databaseConnection.prepareStatement(query);
                preparedStmt.setString(1, id.toString());
                preparedStmt.setString(2, username);
                preparedStmt.setString(3, password);
                preparedStmt.execute();
                return true;
            } catch (Exception e) {
                System.err.println("Unexpected error during connection with the database!");
                return false;
            }
        }
        return false;
    }

    public boolean reconnectContact(Socket connection, String username, String password) {
        UUID id = findIdByUsername(username);
        if (id == null) {
            return false;
        }
        if (contacts.get(id).getPassword().equals(password) && !contacts.get(id).isOnline()) {
            contacts.get(id).reconnect(connection);
            return true;
        } else {
            return false;
        }
    }

    public void disconnectContact(UUID id) {
        contacts.get(id).close();
    }

    public boolean changeContactName(UUID id, String newUsername) {
        if (isUsernameAcceptable(newUsername) && contacts.get(id) != null) {
            try {
                contacts.get(id).setUsername(newUsername);
                String query = "update contact set username = ? where id = ?";
                PreparedStatement preparedStmt = databaseConnection.prepareStatement(query);
                preparedStmt.setString(1, newUsername);
                preparedStmt.setString(2, id.toString());
                preparedStmt.executeUpdate();
                return true;
            } catch (Exception e) {
                System.err.println("Unexpected error during connection with the database!");
                return false;
            }
        }
        return false;
    }

    public boolean changeContactPass(UUID id, String newPassword) {
        if (isPasswordAcceptable(newPassword) && contacts.get(id) != null) {
            try {
                contacts.get(id).setPassword(newPassword);
                String query = "update contact set userpass = ? where id = ?";
                PreparedStatement preparedStmt = databaseConnection.prepareStatement(query);
                preparedStmt.setString(1, newPassword);
                preparedStmt.setString(2, id.toString());
                preparedStmt.executeUpdate();
                return true;
            } catch (Exception e) {
                System.err.println("Unexpected error during connection with the database!");
                return false;
            }
        }
        return false;
    }

    public String findUsernameById(UUID id) {
        if (id == null) {
            return null;
        }
        Optional<Contact> contact = contacts.values().stream()
                .filter(c -> c.getID().equals(id))
                .findFirst();
        return contact.map(Contact::getUsername).orElse(null);
    }

    public UUID findIdByUsername(String username) {
        if (username == null) {
            return null;
        }
        Optional<Contact> contact = contacts.values().stream()
                .filter(c -> c.getUsername().equalsIgnoreCase(username))
                .findFirst();
        return contact.map(Contact::getID).orElse(null);
    }

    public boolean contactExists(String username) {
        if (username == null) {
            return false;
        }
        return contacts.values().stream().anyMatch(c -> c.getUsername().equalsIgnoreCase(username));
    }

    public void sendMessageToAllContacts(String message, UUID senderId) {
        for (Contact c : contacts.values()) {
            if (c.getID().equals(senderId) || !c.isOnline()) {
                continue;
            }
            try {
                c.getDataOutputStream().writeUTF(message);
            } catch (IOException e) {
                System.err.println("service.ContactManagementService -> sendMessageToAllContacts -> Error while sending message to " + c.getUsername());
            }
        }
    }

    public boolean sendMessageToContact(String message, UUID recipientId) {
        try {
            if (contacts.get(recipientId).isOnline()) {
                contacts.get(recipientId).getDataOutputStream().writeUTF(message);
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            System.err.println("service.ContactManagementService -> sendMessageToContact -> Error while sending message to " + contacts.get(recipientId).getUsername());
            return false;
        }
    }

    public boolean isUsernameAcceptable(String username) {
        return !username.contains(" ");
    }

    public boolean isPasswordAcceptable(String password) {
        return password.length() >= 8 && !password.contains(" ");
    }

    public void printAllContacts() {
        for (Contact c : contacts.values()) {
            System.out.println(c.toString());
        }
    }
}