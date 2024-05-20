package ru.hemulen.signer.model;

public class Response {
    private String sign;
    private Error error;

    public Response() {
    }

    public String getSign() {
        return this.sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
