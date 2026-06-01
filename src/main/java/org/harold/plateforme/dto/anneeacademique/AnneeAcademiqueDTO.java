package org.harold.plateforme.dto.anneeacademique;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO de réponse représentant une année académique.
 *
 * <p>Retourné par Spring vers React pour l'affichage
 * des années académiques dans les listes et formulaires.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class AnneeAcademiqueDTO {

    /** Identifiant unique de l'année académique. */
    private Long id;

    /**
     * Libellé court de l'année académique.
     * Exemple : 2024/2025.
     * Unique en base.
     */
    private String annee;

    /** Libellé long de l'année académique. */
    private String libelle;

    /**
     * Indique si c'est l'année académique active.
     * Une seule peut être active à la fois.
     */
    private boolean active;
}