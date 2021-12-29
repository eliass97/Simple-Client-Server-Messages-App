package server.handlers;

import common.enums.CommandTypeEnum;
import common.models.Request;

public class ChangeUsernameHandler extends Handler {

    private ChangeUsernameHandler() {

    }

    private static class Singleton {
        private static final ChangeUsernameHandler INSTANCE = new ChangeUsernameHandler();
    }

    public static ChangeUsernameHandler getInstance() {
        return Singleton.INSTANCE;
    }

    @Override
    public boolean canHandle(Request request) {
        return request.getMessage().startsWith(CommandTypeEnum.CHANGE_USERNAME.getValue());
    }

    @Override
    public void handle(Request request) {
        if (request.getMessage().length() < CommandTypeEnum.CHANGE_USERNAME.getValue().length() + 1) {
            contactManagementService.sendMessageToContact("[Server] New username was not provided", request.getContactID());
        } else {
            String newUsername = getRequestedUsername(request);
            if (!contactManagementService.isUsernameAcceptable(newUsername)) {
                contactManagementService.sendMessageToContact("[Server] Username was not accepted", request.getContactID());
            } else {
                if (contactManagementService.contactExists(newUsername)) {
                    contactManagementService.sendMessageToContact("[Server] This username is already in use", request.getContactID());
                } else {
                    boolean usernameChanged = contactManagementService.changeContactName(request.getContactID(), newUsername);
                    if (usernameChanged) {
                        contactManagementService.sendMessageToContact("[Server] Username changed", request.getContactID());
                    } else {
                        contactManagementService.sendMessageToContact("[Server] Failed to change username", request.getContactID());
                    }
                }
            }
        }
    }

    private String getRequestedUsername(Request request) {
        return request.getMessage().substring(CommandTypeEnum.CHANGE_USERNAME.getValue().length() + 1);
    }
}