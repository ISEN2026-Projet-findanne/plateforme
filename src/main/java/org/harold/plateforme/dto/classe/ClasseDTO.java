package org.harold.plateforme.dto.classe;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO de réponse représentant une classe.
 *
 * <p>Retourné par Spring vers React pour l'affichage
 * des classes dans les listes et les formulaires.</p>
 *
 * <p>Une classe est une entité statique et réutilisable
 * qui définit les matières et coefficients fixes.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class ClasseDTO {

    /** Identifiant unique de la classe. */
    private Long id;

    /** Nom de la classe (ex: Licence 1 Informatique). */
    private String nom;

    /** Niveau de la classe (ex: N1, N2, N3...). */
    private String niveau;

    /** Description de la classe. */
    private String description;
}