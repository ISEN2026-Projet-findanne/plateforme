package org.harold.plateforme.dto.classe;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de requête pour la création d'une classe.
 *
 * <p>Contient les données envoyées par React lors de la création
 * d'une nouvelle classe par l'administrateur.</p>
 *
 * <p>Une classe est une entité statique et réutilisable.
 * Son attribution à une promotion se fait via
 * ClassePromotionCreateDTO.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class ClasseCreateDTO {

    /**
     * Nom de la classe.
     * Obligatoire et unique en base.
     */
    @NotBlank(message = "Le nom de la classe est obligatoire")
    private String nom;

    /**
     * Niveau de la classe (ex: N1, N2, N3...).
     * Obligatoire.
     */
    @NotBlank(message = "Le niveau est obligatoire")
    private String niveau;

    /**
     * Description de la classe.
     * Optionnelle.
     */
    private String description;
}