package ru.hemulen.docsigner.entity;

public class DocumentResponseEntity {
    private Content content;
    private Error error;

    public DocumentResponseEntity(Content content) {
        this.content = content;
    }

    public DocumentResponseEntity(Error error) {
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
