package org.harold.plateforme.dto.note;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de requête pour la saisie d'une note.
 *
 * <p>Contient les données envoyées par React lors de la saisie
 * d'une nouvelle note par un enseignant.</p>
 *
 * <p>L'enseignant connecté est récupéré automatiquement
 * depuis le token JWT, il n'est pas envoyé dans le body.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class NoteCreateDTO {

    /**
     * Identifiant de l'étudiant concerné.
     * Obligatoire.
     */
    @NotNull(message = "L'étudiant est obligatoire")
    private Long etudiantId;

    /**
     * Identifiant de la matière concernée.
     * Obligatoire.
     */
    @NotNull(message = "La matière est obligatoire")
    private Long matiereId;

    /**
     * Identifiant de l'année académique concernée.
     * Obligatoire.
     */
    @NotNull(message = "L'année académique est obligatoire")
    private Long anneeAcademiqueId;

    /**
     * Identifiant du groupe classe de l'étudiant.
     * Obligatoire.
     */
    @NotNull(message = "Le groupe classe est obligatoire")
    private Long groupeClasseId;

    /**
     * Type de la note.
     * Valeurs possibles : CC, EXAMEN_FINAL, TP, RATTRAPAGE.
     * Obligatoire.
     */
    @NotNull(message = "Le type de note est obligatoire")
    private String type;

    /**
     * Valeur de la note entre 0 et 20.
     * Obligatoire.
     */
    @NotNull(message = "La valeur de la note est obligatoire")
    @DecimalMin(value = "0.0", message = "La note ne peut pas être inférieure à 0")
    @DecimalMax(value = "20.0", message = "La note ne peut pas être supérieure à 20")
    private Double valeur;

    /**
     * Indique si cette note est une note de rattrapage.
     * Si vrai, elle remplace intégralement la note EF dans le calcul.
     * Par défaut à false.
     */
    private boolean estRattrapage = false;
}