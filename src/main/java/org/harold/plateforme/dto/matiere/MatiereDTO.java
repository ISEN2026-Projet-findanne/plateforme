package org.harold.plateforme.dto.matiere;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO de réponse représentant une matière.
 *
 * <p>Retourné par Spring vers React pour l'affichage
 * des matières dans les listes et les formulaires.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class MatiereDTO {

    /** Identifiant unique de la matière. */
    private Long id;

    /** Nom de la matière (ex: Analyse des signaux). */
    private String nom;

    /** Code unique de la matière (ex: MATH101). */
    private String code;

    /** Coefficient de la matière dans son groupe de matières. */
    private Double coefficient;

    /** Indique si la matière a des séances TP. */
    private boolean aTp;

    /** Indique si la matière a des séances CM. */
    private boolean aCm;

    /** Identifiant du groupe de matières auquel appartient la matière. */
    private Long groupeMatieresId;

    /** Nom du groupe de matières auquel appartient la matière. */
    private String groupeMatieresNom;
}