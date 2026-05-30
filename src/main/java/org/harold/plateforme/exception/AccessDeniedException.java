package org.harold.plateforme.exception;

public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String message) {
        super(message);
    }

    public AccessDeniedException() {
        super("Vous n'avez pas les droits nécessaires pour effectuer cette action");
    }
}