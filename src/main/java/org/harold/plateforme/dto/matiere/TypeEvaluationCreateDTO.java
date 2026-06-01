package org.harold.plateforme.dto.matiere;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de requête pour la création d'un type d'évaluation.
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class TypeEvaluationCreateDTO {

    /**
     * Identifiant de la matière concernée.
     * Obligatoire.
     */
    @NotNull(message = "La matière est obligatoire")
    private Long matiereId;

    /**
     * Type d'évaluation.
     * Valeurs possibles : CC, EXAMEN_FINAL, TP, RATTRAPAGE.
     * Obligatoire et unique par matière.
     */
    @NotBlank(message = "Le type d'évaluation est obligatoire")
    private String type;

    /**
     * Coefficient de ce type d'évaluation.
     * Obligatoire et strictement positif.
     */
    @NotNull(message = "Le coefficient est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "Le coefficient doit être strictement positif")
    private Double coefficient;

    /** Libellé descriptif. Optionnel. */
    private String libelle;
}