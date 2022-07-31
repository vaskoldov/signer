package ru.hemulen.docsigner.model;

import java.util.ArrayList;

public class StatusResponse {
    private String requestMessageId;
    private ArrayList<MessageResponse> responses;
    private Error error;

    public StatusResponse() {
        responses = new ArrayList<>();
    }

    public void setResponse(MessageResponse response) {
        this.responses.add(response);
    }

    public MessageResponse getResponse(int i) {
        return this.responses.get(i);
    }

    public ArrayList<MessageResponse> getResponses() {
        return responses;
    }

    public String getRequestMessageId() {
        return requestMessageId;
    }

    public void setRequestMessageId(String requestMessageId) {
        this.requestMessageId = requestMessageId;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
