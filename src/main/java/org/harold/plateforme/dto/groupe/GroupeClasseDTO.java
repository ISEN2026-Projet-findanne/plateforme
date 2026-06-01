package org.harold.plateforme.dto.groupe;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO de réponse représentant un groupe TD/TP/CM.
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class GroupeClasseDTO {

    /** Identifiant unique du groupe. */
    private Long id;

    /** Nom du groupe (ex: TD1, TP2). */
    private String nom;

    /**
     * Type du groupe.
     * Valeurs possibles : TD, TP, CM.
     */
    private String type;

    /** Capacité maximale du groupe. */
    private Integer capacite;

    /** Nombre d'étudiants actuellement dans le groupe. */
    private Integer nbEtudiants;

    /** Identifiant de la matière concernée. */
    private Long matiereId;

    /** Nom de la matière concernée. */
    private String matiereNom;

    /** Identifiant de l'année académique. */
    private Long anneeAcademiqueId;

    /** Libellé de l'année académique. */
    private String anneeAcademique;
}