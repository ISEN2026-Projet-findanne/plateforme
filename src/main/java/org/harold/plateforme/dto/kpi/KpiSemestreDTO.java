package org.harold.plateforme.dto.kpi;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO représentant les 7 KPI d'un semestre.
 *
 * <p>Contient les statistiques complètes d'un semestre
 * pour une promotion ou un groupe donné.</p>
 *
 * <p>Accessible uniquement par le responsable pédagogique
 * et l'administrateur. L'enseignant n'a pas accès aux KPI
 * au niveau semestre.</p>
 *
 * <p>Les valeurs null indiquent que les données ne sont
 * pas encore disponibles.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class KpiSemestreDTO {

    // ===== INFORMATIONS SEMESTRE =====

    /** Numéro du semestre (1 ou 2). */
    private Integer numeroSemestre;

    /** Identifiant de l'année académique. */
    private Long anneeAcademiqueId;

    /** Libellé de l'année académique. */
    private String anneeAcademique;

    /** Nom de la promotion concernée. */
    private String promotionNom;

    /**
     * Nom du groupe concerné.
     * Null si KPI au niveau promotion.
     */
    private String groupeNom;

    // ===== 7 KPI =====

    /**
     * Moyenne générale du semestre.
     * Calculée sur les moyennes semestrielles de tous les étudiants.
     * Null si les données ne sont pas encore disponibles.
     */
    private Double moyenne;

    /**
     * Moyenne semestrielle minimale parmi les étudiants.
     * Null si les données ne sont pas encore disponibles.
     */
    private Double minimum;

    /**
     * Moyenne semestrielle maximale parmi les étudiants.
     * Null si les données ne sont pas encore disponibles.
     */
    private Double maximum;

    /**
     * Écart-type des moyennes semestrielles.
     * Mesure la dispersion des moyennes autour de la moyenne générale.
     * Null si les données ne sont pas encore disponibles.
     */
    private Double ecartType;

    /**
     * Médiane des moyennes semestrielles.
     * Null si les données ne sont pas encore disponibles.
     */
    private Double mediane;

    /**
     * Taux de réussite du semestre.
     * Pourcentage d'étudiants ayant validé tous leurs groupes
     * de matières avec une moyenne >= 10.
     * Null si les données ne sont pas encore disponibles.
     */
    private Double tauxReussite;

    /**
     * Taux d'échec du semestre.
     * Pourcentage d'étudiants ayant au moins un groupe
     * de matières avec une moyenne < 10.
     * Null si les données ne sont pas encore disponibles.
     */
    private Double tauxEchec;

    /** Nombre total d'étudiants concernés. */
    private Integer nbEtudiants;
}