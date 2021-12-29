package server.services;

import common.models.Request;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RequestManagementService {

    private final Queue<Request> requests = new ConcurrentLinkedQueue<>();

    private RequestManagementService() {

    }

    private static class Singleton {
        private static final RequestManagementService INSTANCE = new RequestManagementService();
    }

    public static RequestManagementService getInstance() {
        return Singleton.INSTANCE;
    }

    public Queue<Request> getRequests() {
        return requests;
    }

    public void addRequest(Request request) {
        requests.add(request);
    }
}