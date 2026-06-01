package org.harold.plateforme.dto.groupe;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de requête pour l'assignation d'un étudiant à un groupe.
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class AssignationEtudiantDTO {

    /**
     * Identifiant de l'étudiant à assigner.
     * Obligatoire.
     */
    @NotNull(message = "L'étudiant est obligatoire")
    private Long etudiantId;

    /**
     * Identifiant du groupe auquel assigner l'étudiant.
     * Obligatoire.
     */
    @NotNull(message = "Le groupe est obligatoire")
    private Long groupeClasseId;
}