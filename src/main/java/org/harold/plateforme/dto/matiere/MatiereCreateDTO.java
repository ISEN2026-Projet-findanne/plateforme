package org.harold.plateforme.dto.matiere;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de requête pour la création d'une matière.
 *
 * <p>Contient les données envoyées par React lors de la création
 * d'une nouvelle matière par l'administrateur.</p>
 *
 * <p>Une matière appartient obligatoirement à un groupe
 * de matières qui lui-même appartient à un semestre
 * d'une classe.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class MatiereCreateDTO {

    /**
     * Nom de la matière.
     * Obligatoire.
     */
    @NotBlank(message = "Le nom de la matière est obligatoire")
    private String nom;

    /**
     * Code unique de la matière (ex: MATH101).
     * Obligatoire et unique en base.
     */
    @NotBlank(message = "Le code de la matière est obligatoire")
    private String code;

    /**
     * Coefficient de la matière dans son groupe de matières.
     * Obligatoire et strictement positif.
     */
    @NotNull(message = "Le coefficient est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "Le coefficient doit être strictement positif")
    private Double coefficient;

    /**
     * Indique si la matière a des séances TP.
     * Par défaut à false.
     */
    private boolean aTp = false;

    /**
     * Indique si la matière a des séances CM.
     * Par défaut à false.
     */
    private boolean aCm = false;

    /**
     * Identifiant du groupe de matières auquel appartient la matière.
     * Obligatoire.
     */
    @NotNull(message = "Le groupe de matières est obligatoire")
    private Long groupeMatieresId;
}