package org.harold.plateforme.exception;

public class ValidationException extends RuntimeException {

    private final String champ;

    public ValidationException(String message) {
        super(message);
        this.champ = null;
    }

    public ValidationException(String champ, String message) {
        super(message);
        this.champ = champ;
    }

    public String getChamp() { return champ; }
}