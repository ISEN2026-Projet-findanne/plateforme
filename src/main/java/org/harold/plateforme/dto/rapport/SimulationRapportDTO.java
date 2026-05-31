package org.harold.plateforme.dto.rapport;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO contenant les données de simulation à inclure dans un rapport PDF.
 *
 * <p>Encapsule le résultat d'une simulation pédagogique
 * pour l'export en PDF.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class SimulationRapportDTO {

    /** Identifiant de l'étudiant concerné par la simulation. */
    private Long etudiantId;

    /** Identifiant de la matière simulée. */
    private Long matiereId;

    /** Note simulée utilisée pour le calcul. */
    private Double noteSimulee;

    /**
     * Type de simulation effectuée.
     * Valeurs possibles : MATIERE, SEMESTRE.
     */
    private String typeSimulation;
}