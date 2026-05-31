package org.harold.plateforme.dto.kpi;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO représentant une entrée dans une comparaison de KPI.
 *
 * <p>Chaque entrée représente soit une promotion (comparaison
 * inter-promotions) soit une année académique (comparaison
 * inter-années) avec ses KPI associés.</p>
 *
 * <p>Les valeurs null indiquent que les données ne sont
 * pas encore disponibles pour cette entrée.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class EntreeComparaisonDTO {

    /**
     * Nom de la promotion.
     * Renseigné pour une comparaison inter-promotions.
     * Null pour une comparaison inter-années.
     */
    private String promotionNom;

    /**
     * Libellé de l'année académique (ex: 2024/2025).
     * Renseigné pour une comparaison inter-années.
     * Null pour une comparaison inter-promotions.
     */
    private String anneeAcademique;

    /**
     * Moyenne pour cette entrée.
     * Null si données non disponibles.
     */
    private Double moyenne;

    /**
     * Note ou moyenne minimale pour cette entrée.
     * Null si données non disponibles.
     */
    private Double minimum;

    /**
     * Note ou moyenne maximale pour cette entrée.
     * Null si données non disponibles.
     */
    private Double maximum;

    /**
     * Écart-type pour cette entrée.
     * Null si données non disponibles.
     */
    private Double ecartType;

    /**
     * Médiane pour cette entrée.
     * Null si données non disponibles.
     */
    private Double mediane;

    /**
     * Taux de réussite pour cette entrée.
     * Null si données non disponibles.
     */
    private Double tauxReussite;

    /**
     * Taux d'échec pour cette entrée.
     * Null si données non disponibles.
     */
    private Double tauxEchec;

    /**
     * Taux de rattrapage pour cette entrée.
     * Applicable uniquement au niveau matière.
     * Null si données non disponibles ou niveau semestre/annuel.
     */
    private Double tauxRattrapage;

    /** Nombre total d'étudiants concernés pour cette entrée. */
    private Integer nbEtudiants;
}