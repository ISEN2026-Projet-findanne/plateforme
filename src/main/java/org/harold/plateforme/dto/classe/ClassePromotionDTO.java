package org.harold.plateforme.dto.classe;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de requête pour l'attribution d'une classe à une promotion.
 *
 * <p>Contient les données envoyées par React lors de l'attribution
 * d'une classe existante à une promotion pour une année académique
 * donnée par l'administrateur.</p>
 *
 * <p>Une même classe peut être attribuée à plusieurs promotions
 * sur plusieurs années académiques.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class ClassePromotionDTO {

    /**
     * Identifiant de la classe à attribuer.
     * Obligatoire.
     */
    @NotNull(message = "La classe est obligatoire")
    private Long classeId;

    /**
     * Identifiant de la promotion concernée.
     * Obligatoire.
     */
    @NotNull(message = "La promotion est obligatoire")
    private Long promotionId;

    /**
     * Identifiant de l'année académique concernée.
     * Obligatoire.
     */
    @NotNull(message = "L'année académique est obligatoire")
    private Long anneeAcademiqueId;
}