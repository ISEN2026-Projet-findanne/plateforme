package org.harold.plateforme.dto.groupe;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de requête pour la création d'un groupe TD/TP/CM.
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class GroupeClasseCreateDTO {

    /**
     * Nom du groupe.
     * Obligatoire et unique pour une matière et une année.
     */
    @NotBlank(message = "Le nom du groupe est obligatoire")
    private String nom;

    /**
     * Type du groupe.
     * Valeurs possibles : TD, TP, CM.
     * Obligatoire.
     */
    @NotBlank(message = "Le type du groupe est obligatoire")
    private String type;

    /** Capacité maximale du groupe. Optionnelle. */
    private Integer capacite;

    /**
     * Identifiant de la matière concernée.
     * Obligatoire.
     */
    @NotNull(message = "La matière est obligatoire")
    private Long matiereId;

    /**
     * Identifiant de l'année académique.
     * Obligatoire.
     */
    @NotNull(message = "L'année académique est obligatoire")
    private Long anneeAcademiqueId;
}