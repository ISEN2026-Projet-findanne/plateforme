package org.harold.plateforme.dto.rapport;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de requête pour la génération d'un rapport PDF.
 *
 * <p>Contient les paramètres envoyés par React pour générer
 * un rapport PDF. Le type de rapport et le périmètre
 * déterminent le contenu généré.</p>
 *
 * <p>Accessible par l'enseignant pour ses matières uniquement
 * et par le responsable pour tous les périmètres.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class RapportRequestDTO {

    /**
     * Type de rapport à générer.
     * Valeurs possibles :
     * ETUDIANT        → fiche complète d'un étudiant
     * GROUPE          → rapport d'un groupe TD/TP
     * PROMOTION       → rapport d'une promotion
     * MATIERE         → rapport d'une matière
     * COMPARAISON     → comparaison inter-promos ou inter-années
     * SIMULATION      → rapport de simulation pédagogique
     * Obligatoire.
     */
    @NotBlank(message = "Le type de rapport est obligatoire")
    private String typeRapport;

    /**
     * Identifiant de l'année académique concernée.
     * Obligatoire.
     */
    @NotNull(message = "L'année académique est obligatoire")
    private Long anneeAcademiqueId;

    /**
     * Identifiant de l'étudiant concerné.
     * Obligatoire uniquement pour typeRapport = ETUDIANT.
     * Null pour les autres types.
     */
    private Long etudiantId;

    /**
     * Identifiant de la promotion concernée.
     * Obligatoire pour typeRapport = PROMOTION ou COMPARAISON.
     * Null pour les autres types.
     */
    private Long promotionId;

    /**
     * Identifiant du groupe classe concerné.
     * Obligatoire pour typeRapport = GROUPE.
     * Null pour les autres types.
     */
    private Long groupeClasseId;

    /**
     * Identifiant de la matière concernée.
     * Obligatoire pour typeRapport = MATIERE.
     * Null pour les autres types.
     */
    private Long matiereId;

    /**
     * Numéro du semestre concerné (1 ou 2).
     * Optionnel — si null le rapport couvre toute l'année.
     */
    private Integer numeroSemestre;

    /**
     * Données de simulation à inclure dans le rapport.
     * Obligatoire uniquement pour typeRapport = SIMULATION.
     * Null pour les autres types.
     */
    private SimulationRapportDTO simulation;
}