package org.harold.plateforme.dto.matiere;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de requête pour la modification d'un type d'évaluation.
 *
 * <p>Le type (CC, EF, TP, RATTRAPAGE) et la matière
 * ne sont pas modifiables après création.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class TypeEvaluationUpdateDTO {

    /**
     * Nouveau coefficient du type d'évaluation.
     * Obligatoire et strictement positif.
     */
    @NotNull(message = "Le coefficient est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "Le coefficient doit être strictement positif")
    private Double coefficient;

    /**
     * Nouveau libellé descriptif.
     * Optionnel.
     */
    private String libelle;
}