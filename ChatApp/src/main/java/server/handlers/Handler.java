package server.handlers;

import common.models.Request;
import server.services.ContactManagementService;

public abstract class Handler {

    protected final ContactManagementService contactManagementService = ContactManagementService.getInstance();

    public abstract boolean canHandle(Request request);

    public abstract void handle(Request request);
}
