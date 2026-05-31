package org.harold.plateforme.dto.utilisateur;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de requête pour la création d'un utilisateur.
 *
 * <p>Contient les données envoyées par React lors de la création
 * d'un nouveau compte utilisateur par l'administrateur.</p>
 *
 * <p>Le mot de passe sera hashé par Spring Security
 * avant d'être sauvegardé en base.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class UtilisateurCreateDTO {

    /**
     * Nom de l'utilisateur.
     * Obligatoire.
     */
    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    /**
     * Prénom de l'utilisateur.
     * Obligatoire.
     */
    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    /**
     * Email de l'utilisateur.
     * Obligatoire, unique en base et doit être valide.
     */
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String email;

    /**
     * Mot de passe de l'utilisateur.
     * Obligatoire, minimum 8 caractères.
     * Sera hashé par Spring Security avant sauvegarde.
     */
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    private String motDePasse;

    /**
     * Rôle de l'utilisateur.
     * Obligatoire.
     * Valeurs possibles : ROLE_ADMIN, ROLE_ENSEIGNANT, ROLE_RESPONSABLE.
     */
    @NotBlank(message = "Le rôle est obligatoire")
    private String role;
}