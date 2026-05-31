package org.harold.plateforme.dto.note;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de requête pour la modification d'une note existante.
 *
 * <p>Contient uniquement la nouvelle valeur de la note.
 * L'identifiant de la note est passé dans l'URL via @PathVariable.
 * L'enseignant connecté est récupéré depuis le token JWT.</p>
 *
 * <p>On ne peut pas modifier le type ni la matière d'une note existante.
 * Pour changer le type il faut supprimer et recréer la note.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class NoteUpdateDTO {

    /**
     * Nouvelle valeur de la note entre 0 et 20.
     * Obligatoire.
     */
    @NotNull(message = "La valeur de la note est obligatoire")
    @DecimalMin(value = "0.0", message = "La note ne peut pas être inférieure à 0")
    @DecimalMax(value = "20.0", message = "La note ne peut pas être supérieure à 20")
    private Double valeur;

    /**
     * Indique si cette note est une note de rattrapage.
     * Modifiable en cas d'erreur de saisie.
     */
    @NotNull(message = "Le statut rattrapage est obligatoire")
    private Boolean estRattrapage;
}