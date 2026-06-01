package org.harold.plateforme.dto.matiere;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de requête pour la création d'un groupe de matières.
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class GroupeMatieresCreateDTO {

    /**
     * Nom du groupe de matières.
     * Obligatoire et unique dans un semestre.
     */
    @NotBlank(message = "Le nom du groupe est obligatoire")
    private String nom;

    /**
     * Coefficient du groupe dans le semestre.
     * Obligatoire et strictement positif.
     */
    @NotNull(message = "Le coefficient est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "Le coefficient doit être strictement positif")
    private Double coefficient;

    /**
     * Identifiant du semestre auquel appartient ce groupe.
     * Obligatoire.
     */
    @NotNull(message = "Le semestre est obligatoire")
    private Long semestreId;
}