package server.threads;

import common.models.Request;
import server.handlers.*;
import server.services.ContactManagementService;
import server.services.RequestManagementService;

import java.util.List;
import java.util.Optional;

public class ServerRequestProcessThread extends Thread {

    private final ContactManagementService contactManagementService = ContactManagementService.getInstance();
    private final RequestManagementService requestManagementService = RequestManagementService.getInstance();
    private final List<Handler> handlers = List.of(
            ChangeUsernameHandler.getInstance(),
            DisconnectHandler.getInstance(),
            WhisperHandler.getInstance(),
            ChangePasswordHandler.getInstance(),
            SendAllHandler.getInstance()
    );
    private static final int REQUEST_POLLING_INTERVAL = 1000;

    @SuppressWarnings("InfiniteLoopStatement")
    public void run() {
        while (true) {
            if (requestManagementService.getRequests().isEmpty()) {
                loopPause();
            } else {
                Request request = requestManagementService.getRequests().remove();
                Optional<Handler> handler = handlers.stream().filter(c -> c.canHandle(request)).findFirst();
                if (handler.isEmpty()) {
                    contactManagementService.sendMessageToContact("[Server] Unknown command", request.getContactID());
                } else {
                    handler.get().handle(request);
                }
            }
        }
    }

    private void loopPause() {
        try {
            Thread.sleep(REQUEST_POLLING_INTERVAL);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}