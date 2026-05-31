package org.harold.plateforme.dto.utilisateur;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO de réponse représentant un utilisateur.
 *
 * <p>Retourné par Spring vers React pour l'affichage
 * des utilisateurs dans les listes et les formulaires.
 * Ne contient jamais le mot de passe.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class UtilisateurDTO {

    /** Identifiant unique de l'utilisateur. */
    private Long id;

    /** Nom de l'utilisateur. */
    private String nom;

    /** Prénom de l'utilisateur. */
    private String prenom;

    /** Email de l'utilisateur. */
    private String email;

    /**
     * Rôle de l'utilisateur.
     * Valeurs possibles : ROLE_ADMIN, ROLE_ENSEIGNANT, ROLE_RESPONSABLE.
     */
    private String role;

    /** Indique si le compte est actif. */
    private boolean actif;

    /** Date et heure de création du compte. */
    private LocalDateTime createdAt;
}