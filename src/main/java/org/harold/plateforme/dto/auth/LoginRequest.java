package org.harold.plateforme.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Objet de requête pour l'authentification d'un utilisateur.
 *
 * <p>Contient les identifiants envoyés par React lors du login.</p>
 *
 * @author Harold
 * @version 1.0
 */
public class LoginRequest {

    /**
     * Email de l'utilisateur.
     * Doit être un email valide et non vide.
     */
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String email;

    /**
     * Mot de passe de l'utilisateur.
     * Doit être non vide.
     */
    @NotBlank(message = "Le mot de passe est obligatoire")
    private String motDePasse;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
}