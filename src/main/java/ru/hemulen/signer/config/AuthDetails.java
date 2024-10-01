package ru.hemulen.signer.config;

/**
 * AuthDetails.
 *
 * @author Sergey_Rybakov
 */
public class AuthDetails {
    private final String accessToken;

    public AuthDetails(String accessToken) {
        this.accessToken = accessToken;
    }
}
