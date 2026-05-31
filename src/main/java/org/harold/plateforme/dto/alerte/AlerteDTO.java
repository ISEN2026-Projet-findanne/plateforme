package org.harold.plateforme.dto.alerte;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO de réponse représentant une alerte pédagogique.
 *
 * <p>Retourné par Spring vers React pour l'affichage
 * des alertes dans le dashboard et la cloche de notifications.
 * Généré automatiquement par le calcul du score de risque.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class AlerteDTO {

    /** Identifiant unique de l'alerte. */
    private Long id;

    /** Identifiant de l'étudiant concerné. */
    private Long etudiantId;

    /** Nom de l'étudiant concerné. */
    private String etudiantNom;

    /** Prénom de l'étudiant concerné. */
    private String etudiantPrenom;

    /** Numéro étudiant. */
    private String numeroEtudiant;

    /** Nom de la promotion de l'étudiant. */
    private String promotionNom;

    /** Libellé de l'année académique concernée. */
    private String anneeAcademique;

    /**
     * Score de risque entre 0 et 100.
     * Calculé sur la promo actuelle de l'étudiant.
     */
    private Double scoreRisque;

    /**
     * Niveau de risque de l'alerte.
     * Valeurs possibles : FAIBLE, MODERE, ELEVE, CRITIQUE.
     */
    private String niveau;

    /**
     * Indique si l'alerte a été lue.
     * Faux par défaut.
     */
    private boolean lue;

    /** Date et heure de création de l'alerte. */
    private LocalDateTime createdAt;

    /**
     * Détails du calcul du score de risque en JSON.
     * Contient le détail de chaque critère :
     * moyenne, échecs, tendance, remarques négatives.
     */
    private String detailsJson;
}