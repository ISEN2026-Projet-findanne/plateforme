package org.harold.plateforme.dto.etudiant;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO de la fiche d'un étudiant destinée à l'enseignant.
 *
 * <p>Contient uniquement les informations relatives à la matière
 * de l'enseignant : notes de l'étudiant, rang dans la classe
 * et KPI de la classe pour cette matière.</p>
 *
 * <p>L'enseignant n'a accès qu'aux données de ses propres matières
 * et de ses propres groupes.</p>
 *
 * <p>Les valeurs null indiquent que les données ne sont
 * pas encore disponibles.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class FicheEtudiantEnseignantDTO {

    // ===== INFORMATIONS DE BASE =====

    /** Identifiant unique de l'étudiant. */
    private Long id;

    /** Nom de l'étudiant. */
    private String nom;

    /** Prénom de l'étudiant. */
    private String prenom;

    /** Numéro étudiant unique. */
    private String numeroEtudiant;

    /** Nom du groupe TD/TP de l'étudiant pour cette matière. */
    private String groupeNom;

    // ===== INFORMATIONS MATIERE =====

    /** Identifiant de la matière. */
    private Long matiereId;

    /** Nom de la matière. */
    private String matiereNom;

    /** Code de la matière. */
    private String matiereCode;

    /** Année académique concernée. */
    private String anneeAcademique;

    // ===== NOTES DE L'ETUDIANT =====

    /**
     * Note de contrôle continu (CC).
     * Null si pas encore saisie.
     */
    private Double noteCC;

    /**
     * Note d'examen final (EF).
     * Null si pas encore saisie.
     */
    private Double noteEF;

    /**
     * Note de TP.
     * Null si la matière n'a pas de TP.
     */
    private Double noteTP;

    /**
     * Note de rattrapage.
     * Null si l'étudiant n'est pas convoqué en rattrapage.
     */
    private Double noteRattrapage;

    /**
     * Note finale calculée de la matière.
     * Null si toutes les notes ne sont pas encore saisies.
     */
    private Double noteFinale;

    // ===== STATUT =====

    /**
     * Statut de validation de la matière.
     * Vrai si note finale >= 10.
     * Null si note finale non disponible.
     */
    private Boolean valide;

    /**
     * Indique si l'étudiant est convoqué en rattrapage.
     * Vrai si note finale < 10.
     */
    private boolean enRattrapage;

    // ===== RANG ET ECARTS =====

    /**
     * Rang de l'étudiant dans la classe pour cette matière.
     * Null si note finale non disponible.
     */
    private Integer rangClasse;

    /**
     * Rang de l'étudiant dans son groupe TD/TP pour cette matière.
     * Null si note finale non disponible.
     */
    private Integer rangGroupe;

    /**
     * Écart entre la note de l'étudiant et la moyenne de la classe.
     * Positif si l'étudiant est au dessus de la moyenne.
     * Null si note finale non disponible.
     */
    private Double ecartMoyenneClasse;

    /**
     * Écart entre la note de l'étudiant et la moyenne de son groupe.
     * Positif si l'étudiant est au dessus de la moyenne.
     * Null si note finale non disponible.
     */
    private Double ecartMoyenneGroupe;

    // ===== KPI DE LA CLASSE POUR CETTE MATIERE =====

    /**
     * Moyenne de la classe pour cette matière.
     * Null si toutes les notes ne sont pas encore saisies.
     */
    private Double moyenneClasse;

    /**
     * Note minimale dans la classe pour cette matière.
     * Null si toutes les notes ne sont pas encore saisies.
     */
    private Double minClasse;

    /**
     * Note maximale dans la classe pour cette matière.
     * Null si toutes les notes ne sont pas encore saisies.
     */
    private Double maxClasse;

    /**
     * Écart-type des notes de la classe pour cette matière.
     * Null si toutes les notes ne sont pas encore saisies.
     */
    private Double ecartTypeClasse;

    /**
     * Médiane des notes de la classe pour cette matière.
     * Null si toutes les notes ne sont pas encore saisies.
     */
    private Double medianeClasse;

    /**
     * Remarques de l'enseignant sur l'étudiant pour cette matière.
     * Triées par date décroissante.
     */
    private java.util.List<RemarqueDTO> remarques;
}