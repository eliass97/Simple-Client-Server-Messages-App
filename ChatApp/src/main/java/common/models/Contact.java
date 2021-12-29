package common.models;

import java.io.*;
import java.net.Socket;
import java.util.UUID;

public class Contact {

    private Socket connection;
    private OutputStream outputStream;
    private InputStream inputStream;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private String username;
    private String password;
    private boolean isOnline;
    private UUID id;

    public Contact(Socket connection, UUID id, String username, String password) {
        this.connection = connection;
        try {
            outputStream = this.connection.getOutputStream();
            inputStream = this.connection.getInputStream();
            dataInputStream = new DataInputStream(inputStream);
            dataOutputStream = new DataOutputStream(outputStream);
            this.id = id;
            this.username = username;
            this.password = password;
            isOnline = true;
        } catch (IOException ioe) {
            System.err.println("server.Contact -> constructor -> Failed to initiate the streams for a connected contact!");
            isOnline = false;
        }
    }

    public Contact(UUID id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
        isOnline = false;
    }

    public void reconnect(Socket connection) {
        try {
            this.connection = connection;
            outputStream = this.connection.getOutputStream();
            inputStream = this.connection.getInputStream();
            dataInputStream = new DataInputStream(inputStream);
            dataOutputStream = new DataOutputStream(outputStream);
            isOnline = true;
        } catch (IOException ioe) {
            System.err.println("server.Contact -> reconnect -> Failed to initiate the streams for a reconnected contact!");
            isOnline = false;
        }
    }

    public void close() {
        isOnline = false;
        try {
            inputStream.close();
            outputStream.close();
            connection.close();
        } catch (IOException ioe) {
            System.err.println("server.Contact -> close -> Failed to close the connection or the streams!");
        } catch (NullPointerException npe) {
            System.err.println("server.Contact -> close -> Client shut down during login!");
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UUID getID() {
        return id;
    }

    public void setID(UUID ID) {
        this.id = ID;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public Socket getConnection() {
        return connection;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public DataInputStream getDataInputStream() {
        return dataInputStream;
    }

    public DataOutputStream getDataOutputStream() {
        return dataOutputStream;
    }

    public String toString() {
        if (isOnline()) {
            return id + " | " + username + " | " + password + " | " + "Online";
        } else {
            return id + " | " + username + " | " + password + " | " + "Offline";
        }
    }
}