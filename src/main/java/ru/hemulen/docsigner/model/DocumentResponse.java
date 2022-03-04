package ru.hemulen.docsigner.model;

public class DocumentResponse {
    private Content content;
    private Error error;

    public DocumentResponse(Content content) {
        this.content = content;
    }

    public DocumentResponse(Error error) {
        this.error = error;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
