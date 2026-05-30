package org.harold.plateforme.dto.auth;

import lombok.Getter;

/**
 * Objet de réponse après une authentification réussie.
 *
 * <p>Contient les tokens JWT et les informations de base
 * de l'utilisateur connecté retournés à React.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
public class LoginResponse {

    /** Token JWT principal pour les requêtes API. */
    private String accessToken;

    /** Token de rafraîchissement pour renouveler l'access token. */
    private String refreshToken;

    /** Type du token, toujours "Bearer". */
    private String tokenType = "Bearer";

    /** Identifiant de l'utilisateur connecté. */
    private Long id;

    /** Nom de l'utilisateur connecté. */
    private String nom;

    /** Prénom de l'utilisateur connecté. */
    private String prenom;

    /** Email de l'utilisateur connecté. */
    private String email;

    /** Rôle de l'utilisateur connecté. */
    private String role;

    /**
     * Crée une réponse de login avec toutes les informations nécessaires.
     *
     * @param accessToken     le token JWT principal
     * @param refreshToken    le token de rafraîchissement
     * @param id              l'identifiant de l'utilisateur
     * @param nom             le nom de l'utilisateur
     * @param prenom          le prénom de l'utilisateur
     * @param email           l'email de l'utilisateur
     * @param role            le rôle de l'utilisateur
     */
    public LoginResponse(String accessToken, String refreshToken,
                         Long id, String nom, String prenom,
                         String email, String role) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role = role;
    }
}