package org.harold.plateforme.dto.etudiant;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO de la fiche complète d'un étudiant destinée au responsable pédagogique.
 *
 * <p>Contient le récapitulatif annuel complet : notes de toutes les matières,
 * moyennes par matière, groupe de matières, semestre et annuelle,
 * stats par rapport à la classe et score de risque.</p>
 *
 * <p>Les indicateurs non encore disponibles sont à null,
 * React gère l'affichage en conséquence.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class FicheEtudiantResponsableDTO {

    // ===== INFORMATIONS DE BASE =====

    /** Identifiant unique de l'étudiant. */
    private Long id;

    /** Nom de l'étudiant. */
    private String nom;

    /** Prénom de l'étudiant. */
    private String prenom;

    /** Numéro étudiant unique. */
    private String numeroEtudiant;

    /** Email de l'étudiant. */
    private String email;

    /** Nom de la promotion active. */
    private String promotionNom;

    /** Niveau actuel (N1, N2, N3...). */
    private String niveau;

    /** Année académique concernée. */
    private String anneeAcademique;

    // ===== MOYENNES =====

    /**
     * Moyenne générale pondérée annuelle.
     * Null si S1 ou S2 non encore disponible.
     */
    private Double moyenneGenerale;

    /**
     * Moyenne du semestre 1.
     * Null si les notes du S1 ne sont pas encore toutes saisies.
     */
    private Double moyenneS1;

    /**
     * Moyenne du semestre 2.
     * Null si les notes du S2 ne sont pas encore toutes saisies.
     */
    private Double moyenneS2;

    // ===== RANGS ET ECARTS =====

    /**
     * Rang de l'étudiant parmi tous les étudiants de la classe.
     * Null si la moyenne générale n'est pas encore disponible.
     */
    private Integer rangClasse;

    /**
     * Rang de l'étudiant dans son groupe TD/TP.
     * Null si la moyenne générale n'est pas encore disponible.
     */
    private Integer rangGroupe;

    /**
     * Écart entre la moyenne de l'étudiant et la moyenne de la classe.
     * Positif si l'étudiant est au dessus de la moyenne.
     * Null si moyenne non disponible.
     */
    private Double ecartMoyenneClasse;

    /**
     * Écart entre la moyenne de l'étudiant et la moyenne de son groupe.
     * Positif si l'étudiant est au dessus de la moyenne.
     * Null si moyenne non disponible.
     */
    private Double ecartMoyenneGroupe;

    /**
     * Progression entre S1 et S2 (Moyenne_S2 - Moyenne_S1).
     * Positive = progression, négative = régression.
     * Null si S1 ou S2 non disponible.
     */
    private Double progressionInterSemestrielle;

    // ===== STATUTS DE VALIDATION =====

    /**
     * Statut de validation du semestre 1.
     * Null si les notes du S1 ne sont pas encore toutes saisies.
     */
    private Boolean s1Valide;

    /**
     * Statut de validation du semestre 2.
     * Null si les notes du S2 ne sont pas encore toutes saisies.
     */
    private Boolean s2Valide;

    /**
     * Statut de validation annuelle (S1 ET S2 validés).
     * Null si S1 ou S2 non disponible.
     */
    private Boolean anneeValidee;

    // ===== SCORE DE RISQUE =====

    /**
     * Score de risque composite entre 0 et 100.
     * Calculé sur la promo actuelle uniquement.
     * Null si pas encore calculé.
     */
    private Double scoreRisque;

    /**
     * Niveau de risque (FAIBLE, MODERE, ELEVE, CRITIQUE).
     * Null si score non encore calculé.
     */
    private String niveauRisque;

    // ===== DETAILS PAR SEMESTRE =====

    /**
     * Détail complet du semestre 1.
     * Contient les groupes de matières et leurs notes.
     * Null si S1 non encore disponible.
     */
    private SemestreDetailDTO semestreS1;

    /**
     * Détail complet du semestre 2.
     * Contient les groupes de matières et leurs notes.
     * Null si S2 non encore disponible.
     */
    private SemestreDetailDTO semestreS2;

    // ===== REMARQUES =====

    /**
     * Liste des remarques enseignant sur l'étudiant.
     * Triées par date décroissante.
     */
    private List<RemarqueDTO> remarques;
}