package org.harold.plateforme.dto.groupe;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de requête pour la modification d'un groupe TD/TP/CM.
 *
 * <p>La matière et l'année académique ne sont pas modifiables
 * après création.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class GroupeClasseUpdateDTO {

    /**
     * Nouveau nom du groupe.
     * Obligatoire.
     */
    @NotBlank(message = "Le nom du groupe est obligatoire")
    private String nom;

    /** Nouvelle capacité du groupe. Optionnelle. */
    private Integer capacite;
}