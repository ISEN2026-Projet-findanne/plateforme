package org.harold.plateforme.dto.matiere;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO de réponse représentant un groupe de matières.
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class GroupeMatieresDTO {

    /** Identifiant unique du groupe de matières. */
    private Long id;

    /** Nom du groupe de matières (ex: Mathématiques). */
    private String nom;

    /** Coefficient du groupe dans le semestre. */
    private Double coefficient;

    /** Identifiant du semestre auquel appartient ce groupe. */
    private Long semestreId;

    /** Numéro du semestre (1 ou 2). */
    private Integer numeroSemestre;

    /** Identifiant de la classe. */
    private Long classeId;

    /** Nom de la classe. */
    private String classeNom;
}