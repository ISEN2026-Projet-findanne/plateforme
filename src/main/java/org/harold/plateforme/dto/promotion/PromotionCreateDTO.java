package org.harold.plateforme.dto.promotion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de requête pour la création d'une promotion.
 *
 * <p>Contient les données envoyées par React lors de la création
 * d'une nouvelle promotion par l'administrateur.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class PromotionCreateDTO {

    /**
     * Nom de la promotion.
     * Obligatoire et unique en base.
     */
    @NotBlank(message = "Le nom de la promotion est obligatoire")
    private String nom;

    /**
     * Filière de la promotion.
     * Obligatoire.
     */
    @NotBlank(message = "La filière est obligatoire")
    private String filiere;

    /**
     * Année de début de la promotion.
     * Obligatoire.
     */
    @NotNull(message = "L'année de début est obligatoire")
    private Integer anneeDebut;

    /**
     * Description de la promotion.
     * Optionnelle.
     */
    private String description;
}