package org.harold.plateforme.dto.matiere;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO de réponse représentant un type d'évaluation.
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class TypeEvaluationDTO {

    /** Identifiant unique du type d'évaluation. */
    private Long id;

    /** Identifiant de la matière concernée. */
    private Long matiereId;

    /** Nom de la matière concernée. */
    private String matiereNom;

    /**
     * Type d'évaluation.
     * Valeurs possibles : CC, EXAMEN_FINAL, TP, RATTRAPAGE.
     */
    private String type;

    /** Coefficient de ce type d'évaluation dans la matière. */
    private Double coefficient;

    /** Libellé descriptif du type d'évaluation. */
    private String libelle;
}