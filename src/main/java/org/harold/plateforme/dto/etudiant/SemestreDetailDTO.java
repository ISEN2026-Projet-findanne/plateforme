package org.harold.plateforme.dto.etudiant;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO représentant le détail d'un semestre pour un étudiant.
 *
 * <p>Contient la moyenne du semestre, son statut de validation
 * et le détail de chaque groupe de matières.</p>
 *
 * <p>Les valeurs null indiquent que les données ne sont
 * pas encore disponibles.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class SemestreDetailDTO {

    /** Numéro du semestre (1 ou 2). */
    private Integer numero;

    /**
     * Moyenne du semestre.
     * Calculée via somme(Moyenne_groupe * coeff_groupe) / somme(coeff_groupes).
     * Null si toutes les notes ne sont pas encore saisies.
     */
    private Double moyenne;

    /**
     * Statut de validation du semestre.
     * Vrai si tous les groupes de matières ont une moyenne >= 10.
     * Null si moyenne non disponible.
     */
    private Boolean valide;

    /**
     * Coefficient du semestre dans l'année (toujours 0.5).
     * S1 et S2 valent chacun 50% de la moyenne annuelle.
     */
    private Double coefficient;

    /**
     * Liste des groupes de matières du semestre avec leurs détails.
     * Chaque groupe contient ses matières et leurs notes.
     */
    private List<GroupeMatieresDetailDTO> groupesMatieres;
}