package org.harold.plateforme.dto.audit;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO de réponse représentant un log d'audit.
 *
 * <p>Retourné par Spring vers React pour la consultation
 * des logs d'audit par l'administrateur.
 * Trace toutes les modifications effectuées dans le système.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class AuditLogDTO {

    /** Identifiant unique du log. */
    private Long id;

    /** Identifiant de l'utilisateur ayant effectué l'action. */
    private Long utilisateurId;

    /** Nom de l'utilisateur ayant effectué l'action. */
    private String utilisateurNom;

    /** Prénom de l'utilisateur ayant effectué l'action. */
    private String utilisateurPrenom;

    /**
     * Nom de l'entité concernée.
     * Exemples : "Etudiant", "Note", "Promotion"
     */
    private String entite;

    /** Identifiant de l'enregistrement concerné. */
    private Long entiteId;

    /**
     * Type d'action effectuée.
     * Valeurs possibles : CREATE, UPDATE, DELETE.
     */
    private String action;

    /**
     * État de l'enregistrement avant modification en JSON.
     * Null pour les CREATE.
     */
    private String avantJson;

    /**
     * État de l'enregistrement après modification en JSON.
     * Null pour les DELETE.
     */
    private String apresJson;

    /** Adresse IP de l'utilisateur lors de l'action. */
    private String ipAddress;

    /** Date et heure de l'action. */
    private LocalDateTime createdAt;
}