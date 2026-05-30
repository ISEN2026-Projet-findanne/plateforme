package org.harold.plateforme.exception;

import java.time.LocalDateTime;

public class ErrorResponse {

    private int status;
    private String message;
    private String details;
    private LocalDateTime timestamp;

    public ErrorResponse(int status, String message, String details) {
        this.status = status;
        this.message = message;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }

    public int getStatus() { return status; }
    public String getMessage() { return message; }
    public String getDetails() { return details; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
// Ce que j'ai fait ici :
//
//C'est l'objet qui sera retourné au client React en cas d'erreur
//status → le code HTTP (404, 403, 400, 500...)
//message → message lisible pour l'utilisateur
//details → informations supplémentaires sur l'erreur
//timestamp → date et heure de l'erreur, auto-remplie à la création
/** Ici il s'agit d'une classe de gestion de réponses d'erreur d'erreur */