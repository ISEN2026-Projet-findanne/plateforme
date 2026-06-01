package org.harold.plateforme.dto.groupe;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de requête pour l'assignation d'un enseignant à un groupe.
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class AssignationEnseignantDTO {

    /**
     * Identifiant de l'enseignant à assigner.
     * Obligatoire.
     */
    @NotNull(message = "L'enseignant est obligatoire")
    private Long enseignantId;

    /**
     * Identifiant de la matière concernée.
     * Obligatoire.
     */
    @NotNull(message = "La matière est obligatoire")
    private Long matiereId;

    /**
     * Identifiant du groupe concerné.
     * Obligatoire.
     */
    @NotNull(message = "Le groupe est obligatoire")
    private Long groupeClasseId;

    /**
     * Type d'enseignement.
     * Valeurs possibles : CM, TD, TP.
     * Obligatoire.
     */
    @NotBlank(message = "Le type d'enseignement est obligatoire")
    private String typeEnseignement;
}