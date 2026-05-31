package org.harold.plateforme.dto.utilisateur;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de requête pour la modification d'un utilisateur.
 *
 * <p>Contient les données envoyées par React lors de la modification
 * d'un compte utilisateur existant par l'administrateur.
 * L'identifiant est passé dans l'URL via @PathVariable.</p>
 *
 * <p>Le mot de passe n'est pas modifiable ici — une fonctionnalité
 * dédiée sera prévue en V2 (reset mot de passe).</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class UtilisateurUpdateDTO {

    /**
     * Nouveau nom de l'utilisateur.
     * Obligatoire.
     */
    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    /**
     * Nouveau prénom de l'utilisateur.
     * Obligatoire.
     */
    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    /**
     * Nouvel email de l'utilisateur.
     * Obligatoire et doit être valide.
     */
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String email;

    /**
     * Nouveau rôle de l'utilisateur.
     * Obligatoire.
     * Valeurs possibles : ROLE_ADMIN, ROLE_ENSEIGNANT, ROLE_RESPONSABLE.
     */
    @NotBlank(message = "Le rôle est obligatoire")
    private String role;

    /**
     * Statut du compte.
     * Obligatoire.
     * Permet à l'admin d'activer ou désactiver un compte.
     */
    @NotNull(message = "Le statut actif est obligatoire")
    private Boolean actif;
}