package org.harold.plateforme.dto.etudiant;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO représentant le détail des notes d'un étudiant pour une matière.
 *
 * <p>Contient toutes les notes de la matière (CC, EF, TP, Rattrapage),
 * la note finale calculée, le rang de l'étudiant et les KPI de la classe
 * pour cette matière.</p>
 *
 * <p>Les valeurs null indiquent que les notes ne sont
 * pas encore disponibles.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class NoteEtudiantDTO {

    // ===== INFORMATIONS MATIERE =====

    /** Identifiant de la matière. */
    private Long matiereId;

    /** Nom de la matière. */
    private String matiereNom;

    /** Code de la matière (ex: MATH101). */
    private String matiereCode;

    /** Coefficient de la matière dans son groupe de matières. */
    private Double coefficient;

    /** Nom du groupe TD/TP de l'étudiant pour cette matière. */
    private String groupeNom;

    // ===== NOTES =====

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
     * Remplace intégralement la note EF dans le calcul si présente.
     */
    private Double noteRattrapage;

    /**
     * Note finale calculée de la matière.
     * Calculée via (CC*coeff_CC + EF*coeff_EF + TP*coeff_TP) / somme(coeffs).
     * Si rattrapage : EF remplacé par note rattrapage.
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

    /**
     * Tendance par rapport au semestre précédent.
     * Valeurs possibles : PROGRESSION, REGRESSION, STABLE, null si premier semestre.
     */
    private String tendance;

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

    // ===== KPI CLASSE POUR CETTE MATIERE =====

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
}