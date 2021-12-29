package server.handlers;

import common.enums.CommandTypeEnum;
import common.models.Request;

import java.util.UUID;

public class WhisperHandler extends Handler {

    private WhisperHandler() {

    }

    private static class Singleton {
        private static final WhisperHandler INSTANCE = new WhisperHandler();
    }

    public static WhisperHandler getInstance() {
        return Singleton.INSTANCE;
    }

    @Override
    public boolean canHandle(Request request) {
        return request.getMessage().startsWith(CommandTypeEnum.WHISPER.getValue());
    }

    @Override
    public void handle(Request request) {
        String[] messageParts = request.getMessage().split(" ");
        String recipientUsername;
        if (messageParts.length >= 3) {
            recipientUsername = messageParts[1];
            UUID recipientID = contactManagementService.findIdByUsername(recipientUsername);
            if (recipientID == null) {
                contactManagementService.sendMessageToContact("[Server] Invalid recipient username", request.getContactID());
            } else {
                String name = contactManagementService.findUsernameById(request.getContactID());
                String message = "[Private] " + name + ": " + request.getMessage()
                        .substring(CommandTypeEnum.WHISPER.getValue().length() + 1 + messageParts[1].length());
                boolean messageSent = contactManagementService.sendMessageToContact(message, recipientID);
                if (!messageSent) {
                    message = "[Server] " + recipientUsername + " is currently offline";
                    contactManagementService.sendMessageToContact(message, request.getContactID());
                }
            }
        }
    }
}