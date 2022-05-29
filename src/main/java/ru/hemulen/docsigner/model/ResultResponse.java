package ru.hemulen.docsigner.model;

public class ResultResponse {
    private String result;
    private Error error;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
