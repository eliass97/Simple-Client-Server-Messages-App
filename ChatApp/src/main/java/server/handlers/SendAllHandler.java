package server.handlers;

import common.models.Request;

public class SendAllHandler extends Handler {

    private SendAllHandler() {

    }

    private static class Singleton {
        private static final SendAllHandler INSTANCE = new SendAllHandler();
    }

    public static SendAllHandler getInstance() {
        return Singleton.INSTANCE;
    }

    @Override
    public boolean canHandle(Request request) {
        return !request.getMessage().startsWith("/");
    }

    @Override
    public void handle(Request request) {
        String name = contactManagementService.findUsernameById(request.getContactID());
        if (name != null) {
            String message = "[All] " + name + ": " + request.getMessage();
            contactManagementService.sendMessageToAllContacts(message, request.getContactID());
        }
    }
}