package org.harold.plateforme.dto.kpi;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO de résultat de simulation au niveau semestre.
 *
 * <p>Retourné au responsable pédagogique après une simulation
 * de rattrapage. Indique l'impact sur la matière, le groupe
 * de matières et le semestre.</p>
 *
 * <p>La simulation est une prévisualisation uniquement —
 * aucune donnée n'est sauvegardée en base.
 * Exportable en PDF.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class SimulationSemestreResultDTO {

    // ===== INFORMATIONS GENERALES =====

    /** Nom de l'étudiant concerné. */
    private String etudiantNom;

    /** Prénom de l'étudiant concerné. */
    private String etudiantPrenom;

    /** Numéro étudiant. */
    private String numeroEtudiant;

    /** Année académique concernée. */
    private String anneeAcademique;

    /** Numéro du semestre simulé (1 ou 2). */
    private Integer numeroSemestre;

    /** Note simulée utilisée pour le calcul. */
    private Double noteSimulee;

    // ===== NIVEAU MATIERE =====

    /** Nom de la matière simulée. */
    private String matiereNom;

    /**
     * Note matière actuelle avant simulation.
     * Permet à React d'afficher la comparaison avant/après.
     */
    private Double noteMatiereActuelle;

    /**
     * Note matière calculée avec la note simulée.
     * Calculée via (CC*coeff_CC + noteSimulee*coeff_EF + TP*coeff_TP)
     * / somme(coeffs).
     */
    private Double noteMatiereSimulee;

    /** Statut de validation actuel de la matière avant simulation. */
    private boolean matiereValideActuellement;

    /** Statut de validation de la matière avec la note simulée. */
    private boolean matiereValideAvecSimulation;

    // ===== NIVEAU GROUPE DE MATIERES =====

    /** Nom du groupe de matières concerné. */
    private String groupeMatieresNom;

    /**
     * Moyenne actuelle du groupe de matières avant simulation.
     * Permet à React d'afficher la comparaison avant/après.
     */
    private Double moyenneGroupeActuelle;

    /**
     * Moyenne du groupe de matières calculée avec la note simulée.
     * Calculée via somme(note_matiere * coeff) / somme(coeffs).
     */
    private Double moyenneGroupeSimulee;

    /** Statut de validation actuel du groupe avant simulation. */
    private boolean groupeValideActuellement;

    /** Statut de validation du groupe avec la note simulée. */
    private boolean groupeValideAvecSimulation;

    // ===== NIVEAU SEMESTRE =====

    /**
     * Moyenne semestrielle actuelle avant simulation.
     * Permet à React d'afficher la comparaison avant/après.
     */
    private Double moyenneSemestreActuelle;

    /**
     * Moyenne semestrielle calculée avec la note simulée.
     * Calculée via somme(Moyenne_groupe * coeff_groupe) / somme(coeff_groupes).
     */
    private Double moyenneSemestreSimulee;

    /** Statut de validation actuel du semestre avant simulation. */
    private boolean semestreValideActuellement;

    /** Statut de validation du semestre avec la note simulée. */
    private boolean semestreValideAvecSimulation;
}