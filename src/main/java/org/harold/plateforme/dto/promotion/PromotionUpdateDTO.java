package org.harold.plateforme.dto.promotion;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de requête pour la modification d'une promotion.
 *
 * <p>Contient les données envoyées par React lors de la modification
 * d'une promotion existante par l'administrateur.
 * L'identifiant de la promotion est passé dans l'URL
 * via @PathVariable et non dans le body.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class PromotionUpdateDTO {

    /**
     * Nouveau nom de la promotion.
     * Obligatoire.
     */
    @NotBlank(message = "Le nom de la promotion est obligatoire")
    private String nom;

    /**
     * Nouvelle filière de la promotion.
     * Obligatoire.
     */
    @NotBlank(message = "La filière est obligatoire")
    private String filiere;

    /**
     * Nouvelle description de la promotion.
     * Optionnelle.
     */
    private String description;
}