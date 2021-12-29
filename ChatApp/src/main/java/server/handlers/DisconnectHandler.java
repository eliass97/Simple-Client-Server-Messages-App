package server.handlers;

import common.enums.CommandTypeEnum;
import common.models.Request;

public class DisconnectHandler extends Handler {

    private DisconnectHandler() {

    }

    private static class Singleton {
        private static final DisconnectHandler INSTANCE = new DisconnectHandler();
    }

    public static DisconnectHandler getInstance() {
        return Singleton.INSTANCE;
    }

    @Override
    public boolean canHandle(Request request) {
        return request.getMessage().equals(CommandTypeEnum.DISCONNECT.getValue());
    }

    @Override
    public void handle(Request request) {
        contactManagementService.sendMessageToContact(CommandTypeEnum.DISCONNECT.getValue(), request.getContactID());
        contactManagementService.disconnectContact(request.getContactID());
        String message = "[Server] " + contactManagementService.findUsernameById(request.getContactID()) + " has been disconnected";
        contactManagementService.sendMessageToAllContacts(message, request.getContactID());
    }
}