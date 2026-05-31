package org.harold.plateforme.dto.etudiant;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO représentant le détail d'un groupe de matières pour un étudiant.
 *
 * <p>Contient la moyenne du groupe, son statut de validation
 * et le détail de chaque matière. La compensation est possible
 * uniquement entre matières du même groupe.</p>
 *
 * <p>Les valeurs null indiquent que les données ne sont
 * pas encore disponibles.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class GroupeMatieresDetailDTO {

    /** Identifiant du groupe de matières. */
    private Long id;

    /** Nom du groupe de matières (ex: Mathématiques, Informatique...). */
    private String nom;

    /**
     * Coefficient du groupe de matières dans le semestre.
     * Utilisé pour calculer la moyenne du semestre.
     */
    private Double coefficient;

    /**
     * Moyenne du groupe de matières.
     * Calculée via somme(note_matiere * coeff) / somme(coeffs).
     * Null si toutes les notes du groupe ne sont pas encore saisies.
     */
    private Double moyenne;

    /**
     * Statut de validation du groupe de matières.
     * Vrai si la moyenne du groupe >= 10 après compensation.
     * Null si moyenne non disponible.
     */
    private Boolean valide;

    /**
     * Écart entre la moyenne de l'étudiant dans ce groupe
     * et la moyenne de la classe pour ce groupe.
     * Positif si l'étudiant est au dessus de la moyenne.
     * Null si moyenne non disponible.
     */
    private Double ecartMoyenneClasse;

    /**
     * Liste des matières du groupe avec leurs notes détaillées.
     * Chaque matière contient la note, le rang et les KPI de la classe.
     */
    private List<NoteEtudiantDTO> matieres;
}