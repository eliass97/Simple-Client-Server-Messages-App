package server.handlers;

import common.enums.CommandTypeEnum;
import common.models.Request;

public class ChangePasswordHandler extends Handler {

    private ChangePasswordHandler() {

    }

    private static class Singleton {
        private static final ChangePasswordHandler INSTANCE = new ChangePasswordHandler();
    }

    public static ChangePasswordHandler getInstance() {
        return Singleton.INSTANCE;
    }

    @Override
    public boolean canHandle(Request request) {
        return request.getMessage().startsWith(CommandTypeEnum.CHANGE_PASSWORD.getValue());
    }

    @Override
    public void handle(Request request) {
        if (request.getMessage().length() < CommandTypeEnum.CHANGE_PASSWORD.getValue().length() + 1) {
            contactManagementService.sendMessageToContact("[Server] New password was not provided", request.getContactID());
        } else {
            String newPassword = getRequestedPassword(request);
            if (!contactManagementService.isPasswordAcceptable(newPassword)) {
                contactManagementService.sendMessageToContact("[Server] Password was not accepted", request.getContactID());
            } else {
                boolean passwordChanged = contactManagementService.changeContactPass(request.getContactID(), newPassword);
                if (passwordChanged) {
                    contactManagementService.sendMessageToContact("[Server] Password changed", request.getContactID());
                } else {
                    contactManagementService.sendMessageToContact("[Server] Failed to change password", request.getContactID());
                }
            }
        }
    }

    private String getRequestedPassword(Request request) {
        return request.getMessage().substring(CommandTypeEnum.CHANGE_PASSWORD.getValue().length() + 1);
    }
}