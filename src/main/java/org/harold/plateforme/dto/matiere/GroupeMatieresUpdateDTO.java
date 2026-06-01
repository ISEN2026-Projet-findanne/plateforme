package org.harold.plateforme.dto.matiere;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de requête pour la modification d'un groupe de matières.
 *
 * <p>Le semestre n'est pas modifiable après création
 * car cela impacterait les calculs existants.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class GroupeMatieresUpdateDTO {

    /**
     * Nouveau nom du groupe de matières.
     * Obligatoire et unique dans le semestre.
     */
    @NotBlank(message = "Le nom du groupe est obligatoire")
    private String nom;

    /**
     * Nouveau coefficient du groupe dans le semestre.
     * Obligatoire et strictement positif.
     */
    @NotNull(message = "Le coefficient est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "Le coefficient doit être strictement positif")
    private Double coefficient;
}