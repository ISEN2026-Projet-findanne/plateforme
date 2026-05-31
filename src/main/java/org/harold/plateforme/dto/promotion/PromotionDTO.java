package org.harold.plateforme.dto.promotion;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO de réponse représentant une promotion.
 *
 * <p>Retourné par Spring vers React pour l'affichage
 * des promotions dans les listes et les formulaires.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class PromotionDTO {

    /** Identifiant unique de la promotion. */
    private Long id;

    /** Nom de la promotion (ex: HEI A3). */
    private String nom;

    /** Filière de la promotion (ex: Informatique). */
    private String filiere;

    /** Année de début de la promotion. */
    private Integer anneeDebut;

    /** Description de la promotion. */
    private String description;

    /** Nombre d'étudiants actifs dans la promotion. */
    private Integer nbEtudiants;
}