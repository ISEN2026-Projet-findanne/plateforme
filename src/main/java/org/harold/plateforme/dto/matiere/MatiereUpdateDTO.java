package org.harold.plateforme.dto.matiere;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de requête pour la modification d'une matière.
 *
 * <p>Contient les données envoyées par React lors de la modification
 * d'une matière existante par l'administrateur.
 * L'identifiant de la matière est passé dans l'URL
 * via @PathVariable et non dans le body.</p>
 *
 * <p>Le groupe de matières n'est pas modifiable après création
 * car cela impacterait les coefficients et les calculs existants.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class MatiereUpdateDTO {

    /**
     * Nouveau nom de la matière.
     * Obligatoire.
     */
    @NotBlank(message = "Le nom de la matière est obligatoire")
    private String nom;

    /**
     * Nouveau coefficient de la matière.
     * Obligatoire et strictement positif.
     */
    @NotNull(message = "Le coefficient est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "Le coefficient doit être strictement positif")
    private Double coefficient;

    /**
     * Indique si la matière a des séances TP.
     * Obligatoire.
     */
    @NotNull(message = "Le statut TP est obligatoire")
    private Boolean aTp;

    /**
     * Indique si la matière a des séances CM.
     * Obligatoire.
     */
    @NotNull(message = "Le statut CM est obligatoire")
    private Boolean aCm;
}