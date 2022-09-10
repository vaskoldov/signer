package ru.hemulen.docsigner.exception;

public class IncorrectParameterException extends Exception{
    public IncorrectParameterException() {
    }

    public IncorrectParameterException(String message) {
        super(message);
    }
}
