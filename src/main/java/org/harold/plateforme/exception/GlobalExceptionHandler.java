package org.harold.plateforme.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Gestionnaire global des exceptions de l'application.
 *
 * <p>Intercepte toutes les exceptions levées dans les controllers
 * et retourne une réponse HTTP appropriée au client React.</p>
 *
 * @author Harold
 * @version 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Gère les erreurs de ressource introuvable.
     *
     * @param ex    l'exception levée
     * @return      réponse HTTP 404 avec le détail de l'erreur
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        HttpStatus.NOT_FOUND.value(),
                        ex.getMessage(),
                        "Ressource introuvable"));
    }

    /**
     * Gère les erreurs d'accès refusé.
     *
     * @param ex    l'exception levée
     * @return      réponse HTTP 403 avec le détail de l'erreur
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(
                        HttpStatus.FORBIDDEN.value(),
                        ex.getMessage(),
                        "Accès refusé"));
    }

    /**
     * Gère les erreurs de validation métier.
     *
     * @param ex    l'exception levée
     * @return      réponse HTTP 400 avec le champ concerné
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            ValidationException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getMessage(),
                        ex.getChamp() != null
                                ? "Champ invalide : " + ex.getChamp()
                                : "Données invalides"));
    }

    /**
     * Gère les erreurs d'import CSV.
     *
     * <p>Retourne la liste détaillée des erreurs ligne par ligne.</p>
     *
     * @param ex    l'exception levée
     * @return      réponse HTTP 400 avec la liste des erreurs
     */
    @ExceptionHandler(CsvImportException.class)
    public ResponseEntity<Map<String, Object>> handleCsvImport(
            CsvImportException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", ex.getMessage());
        response.put("erreurs", ex.getErreurs());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * Gère les erreurs d'authentification (mauvais email ou mot de passe).
     *
     * @param ex    l'exception levée
     * @return      réponse HTTP 401 avec message générique
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(
                        HttpStatus.UNAUTHORIZED.value(),
                        "Email ou mot de passe incorrect",
                        "Authentification échouée"));
    }

    /**
     * Gère les erreurs de validation des champs annotés avec @Valid.
     *
     * <p>Retourne un map des champs en erreur avec leur message respectif.</p>
     *
     * @param ex    l'exception levée par Spring lors de la validation
     * @return      réponse HTTP 400 avec le détail des champs invalides
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex) {
        Map<String, String> champsErreurs = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            champsErreurs.put(fieldName, errorMessage);
        });
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", "Erreur de validation des données");
        response.put("erreurs", champsErreurs);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * Gère toutes les exceptions non prévues.
     *
     * <p>Filet de sécurité pour éviter d'exposer les détails
     * techniques au client en cas d'erreur inattendue.</p>
     *
     * @param ex    l'exception levée
     * @return      réponse HTTP 500 avec message générique
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobal(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Une erreur interne est survenue",
                        ex.getMessage()));
    }

    /**
     * Gère les erreurs de ressource dupliquée.
     *
     * @param ex    l'exception levée
     * @return      réponse HTTP 409 avec le détail de la duplication
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResource(
            DuplicateResourceException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        HttpStatus.CONFLICT.value(),
                        ex.getMessage(),
                        "Ressource dupliquée"));
    }
}