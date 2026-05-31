package org.harold.plateforme.dto.kpi;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO représentant les 8 KPI d'une matière.
 *
 * <p>Contient les statistiques complètes d'une matière
 * pour une promotion ou un groupe donné.</p>
 *
 * <p>Accessible par l'enseignant pour ses matières uniquement
 * et par le responsable pour toutes les matières.</p>
 *
 * <p>Les valeurs null indiquent que les données ne sont
 * pas encore disponibles.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class KpiMatiereDTO {

    // ===== INFORMATIONS MATIERE =====

    /** Identifiant de la matière. */
    private Long matiereId;

    /** Nom de la matière. */
    private String matiereNom;

    /** Code de la matière. */
    private String matiereCode;

    /** Identifiant de l'année académique. */
    private Long anneeAcademiqueId;

    /** Libellé de l'année académique. */
    private String anneeAcademique;

    /** Nom de la promotion concernée. */
    private String promotionNom;

    /** Nom du groupe concerné (null si KPI au niveau promotion). */
    private String groupeNom;

    // ===== 8 KPI =====

    /**
     * Moyenne générale de la matière.
     * Null si les notes ne sont pas encore disponibles.
     */
    private Double moyenne;

    /**
     * Note minimale obtenue dans la matière.
     * Null si les notes ne sont pas encore disponibles.
     */
    private Double minimum;

    /**
     * Note maximale obtenue dans la matière.
     * Null si les notes ne sont pas encore disponibles.
     */
    private Double maximum;

    /**
     * Écart-type des notes de la matière.
     * Mesure la dispersion des notes autour de la moyenne.
     * Null si les notes ne sont pas encore disponibles.
     */
    private Double ecartType;

    /**
     * Médiane des notes de la matière.
     * Valeur centrale de la distribution des notes.
     * Null si les notes ne sont pas encore disponibles.
     */
    private Double mediane;

    /**
     * Taux de réussite de la matière.
     * Pourcentage d'étudiants ayant une note >= 10.
     * Null si les notes ne sont pas encore disponibles.
     */
    private Double tauxReussite;

    /**
     * Taux d'échec de la matière.
     * Pourcentage d'étudiants ayant une note < 10.
     * Null si les notes ne sont pas encore disponibles.
     */
    private Double tauxEchec;

    /**
     * Taux de rattrapage de la matière.
     * Pourcentage d'étudiants convoqués en rattrapage.
     * Applicable uniquement au niveau matière.
     * Null si les notes ne sont pas encore disponibles.
     */
    private Double tauxRattrapage;

    /** Nombre total d'étudiants concernés. */
    private Integer nbEtudiants;
}