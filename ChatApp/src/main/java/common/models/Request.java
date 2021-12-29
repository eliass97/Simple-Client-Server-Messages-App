package common.models;

import java.util.UUID;

public class Request {

    private UUID contactId;
    private String message;

    public Request(UUID contactId, String message) {
        this.contactId = contactId;
        this.message = message;
    }

    public UUID getContactID() {
        return contactId;
    }

    public void setContactID(UUID contactId) {
        this.contactId = contactId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}