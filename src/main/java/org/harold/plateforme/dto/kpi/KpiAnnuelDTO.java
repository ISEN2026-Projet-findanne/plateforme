package org.harold.plateforme.dto.kpi;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO représentant les 7 KPI annuels.
 *
 * <p>Contient les statistiques complètes du bilan annuel
 * pour une promotion ou un groupe donné.</p>
 *
 * <p>Accessible uniquement par le responsable pédagogique
 * et l'administrateur. L'enseignant n'a pas accès aux KPI
 * au niveau annuel.</p>
 *
 * <p>Les valeurs null indiquent que les données ne sont
 * pas encore disponibles.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class KpiAnnuelDTO {

    // ===== INFORMATIONS ANNEE =====

    /** Identifiant de l'année académique. */
    private Long anneeAcademiqueId;

    /** Libellé de l'année académique (ex: 2024/2025). */
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
     * Moyenne générale annuelle.
     * Calculée via (Moyenne_S1 * 50%) + (Moyenne_S2 * 50%).
     * Null si S1 ou S2 non encore disponible.
     */
    private Double moyenne;

    /**
     * Moyenne annuelle minimale parmi les étudiants.
     * Null si données non encore disponibles.
     */
    private Double minimum;

    /**
     * Moyenne annuelle maximale parmi les étudiants.
     * Null si données non encore disponibles.
     */
    private Double maximum;

    /**
     * Écart-type des moyennes annuelles.
     * Mesure la dispersion des moyennes autour de la moyenne générale.
     * Null si données non encore disponibles.
     */
    private Double ecartType;

    /**
     * Médiane des moyennes annuelles.
     * Null si données non encore disponibles.
     */
    private Double mediane;

    /**
     * Taux de réussite annuel.
     * Pourcentage d'étudiants ayant validé S1 ET S2.
     * Null si données non encore disponibles.
     */
    private Double tauxReussite;

    /**
     * Taux d'échec annuel.
     * Pourcentage d'étudiants n'ayant pas validé S1 ou S2.
     * Null si données non encore disponibles.
     */
    private Double tauxEchec;

    /** Nombre total d'étudiants concernés. */
    private Integer nbEtudiants;
}