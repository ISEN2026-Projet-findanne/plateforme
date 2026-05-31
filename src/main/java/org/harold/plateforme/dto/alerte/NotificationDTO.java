package org.harold.plateforme.dto.alerte;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO de réponse représentant une notification utilisateur.
 *
 * <p>Retourné par Spring vers React lors du polling REST.
 * Chaque notification est liée à une alerte et destinée
 * à un utilisateur précis (enseignant ou responsable).</p>
 *
 * <p>Affiché dans la cloche de notifications du header React
 * avec le badge indiquant le nombre de notifications non lues.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class NotificationDTO {

    /** Identifiant unique de la notification. */
    private Long id;

    /** Identifiant de l'alerte associée. */
    private Long alerteId;

    /** Nom de l'étudiant concerné par l'alerte. */
    private String etudiantNom;

    /** Prénom de l'étudiant concerné par l'alerte. */
    private String etudiantPrenom;

    /** Numéro étudiant concerné par l'alerte. */
    private String numeroEtudiant;

    /** Nom de la promotion de l'étudiant. */
    private String promotionNom;

    /**
     * Niveau de risque de l'alerte associée.
     * Valeurs possibles : FAIBLE, MODERE, ELEVE, CRITIQUE.
     * Permet à React d'afficher la couleur appropriée.
     */
    private String niveauRisque;

    /**
     * Score de risque entre 0 et 100.
     * Affiché dans la notification pour donner le contexte.
     */
    private Double scoreRisque;

    /**
     * Indique si la notification a été lue.
     * Faux par défaut.
     */
    private boolean lue;

    /** Date et heure de création de la notification. */
    private LocalDateTime createdAt;

    /**
     * Date et heure de lecture de la notification.
     * Null si pas encore lue.
     */
    private LocalDateTime lueLe;
}