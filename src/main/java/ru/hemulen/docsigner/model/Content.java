package ru.hemulen.docsigner.model;

public class Content {
    private String clientId;
    private String signContent; // Подпись организации в кодировке base64

    public Content() {
    }

    public Content(Document document) {
        this.clientId = document.getClientId();
        this.signContent = document.getSignContent();
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getSignContent() {
        return signContent;
    }

    public void setSignContent(String signContent) {
        this.signContent = signContent;
    }
}
