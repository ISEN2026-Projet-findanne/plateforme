package org.harold.plateforme.dto.kpi;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO représentant une comparaison de KPI entre promotions ou entre années.
 *
 * <p>Utilisé pour deux types de comparaisons :</p>
 * <ul>
 *   <li>Inter-promotions : comparer les KPI d'une même matière
 *       ou d'un même semestre entre plusieurs promotions
 *       pour une même année académique.</li>
 *   <li>Inter-années : comparer l'évolution des KPI d'une même
 *       promotion sur plusieurs années académiques.</li>
 * </ul>
 *
 * <p>Accessible par le responsable pour tous les niveaux.
 * L'enseignant y accède uniquement pour ses matières.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class ComparaisonPromoDTO {

    // ===== INFORMATIONS COMPARAISON =====

    /**
     * Type de comparaison effectuée.
     * Valeurs possibles : INTER_PROMOTIONS, INTER_ANNEES.
     */
    private String typeComparaison;

    /**
     * Niveau de comparaison.
     * Valeurs possibles : MATIERE, SEMESTRE, ANNUEL.
     */
    private String niveauComparaison;

    /**
     * Nom de la matière comparée.
     * Null si comparaison au niveau semestre ou annuel.
     */
    private String matiereNom;

    /**
     * Code de la matière comparée.
     * Null si comparaison au niveau semestre ou annuel.
     */
    private String matiereCode;

    /**
     * Numéro du semestre comparé (1 ou 2).
     * Null si comparaison au niveau matière ou annuel.
     */
    private Integer numeroSemestre;

    // ===== RESULTATS DE COMPARAISON =====

    /**
     * Liste des entrées de comparaison.
     * Chaque entrée représente une promotion ou une année académique
     * avec ses KPI associés.
     */
    private List<EntreeComparaisonDTO> entrees;
}