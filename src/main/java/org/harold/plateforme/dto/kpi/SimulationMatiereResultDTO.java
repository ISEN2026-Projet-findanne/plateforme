package org.harold.plateforme.dto.kpi;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO de résultat de simulation au niveau matière.
 *
 * <p>Retourné à l'enseignant après une simulation de rattrapage.
 * Indique uniquement l'impact sur la note de la matière.</p>
 *
 * <p>La simulation est une prévisualisation uniquement —
 * aucune donnée n'est sauvegardée en base.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class SimulationMatiereResultDTO {

    /** Nom de l'étudiant concerné. */
    private String etudiantNom;

    /** Prénom de l'étudiant concerné. */
    private String etudiantPrenom;

    /** Nom de la matière simulée. */
    private String matiereNom;

    /** Note EF actuelle avant simulation. */
    private Double noteEFActuelle;

    /** Note simulée utilisée pour le calcul. */
    private Double noteSimulee;

    /**
     * Note matière calculée avec la note simulée.
     * Calculée via (CC*coeff_CC + noteSimulee*coeff_EF + TP*coeff_TP)
     * / somme(coeffs).
     */
    private Double noteMatiereSimulee;

    /**
     * Note matière actuelle avant simulation.
     * Permet à React d'afficher la comparaison avant/après.
     */
    private Double noteMatiereActuelle;

    /**
     * Statut de validation de la matière avec la note simulée.
     * Vrai si noteMatiereSimulee >= 10.
     */
    private boolean valideAvecSimulation;

    /**
     * Statut de validation actuel avant simulation.
     * Permet à React d'afficher si la simulation change le statut.
     */
    private boolean valideActuellement;
}