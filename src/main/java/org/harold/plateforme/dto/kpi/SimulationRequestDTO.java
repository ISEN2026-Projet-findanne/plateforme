package org.harold.plateforme.dto.kpi;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de requête pour la simulation pédagogique.
 *
 * <p>Contient les paramètres envoyés par React pour simuler
 * l'impact d'une note de rattrapage sur la validation
 * d'un étudiant.</p>
 *
 * <p>La simulation est une prévisualisation uniquement —
 * aucune donnée n'est sauvegardée en base.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class SimulationRequestDTO {

    /** Identifiant de l'étudiant concerné. Obligatoire. */
    @NotNull(message = "L'étudiant est obligatoire")
    private Long etudiantId;

    /** Identifiant de la matière concernée. Obligatoire. */
    @NotNull(message = "La matière est obligatoire")
    private Long matiereId;

    /** Identifiant de l'année académique. Obligatoire. */
    @NotNull(message = "L'année académique est obligatoire")
    private Long anneeAcademiqueId;

    /**
     * Note simulée entre 0 et 20.
     * Remplace intégralement la note EF dans le calcul.
     * Obligatoire.
     */
    @NotNull(message = "La note simulée est obligatoire")
    @DecimalMin(value = "0.0", message = "La note simulée ne peut pas être inférieure à 0")
    @DecimalMax(value = "20.0", message = "La note simulée ne peut pas être supérieure à 20")
    private Double noteSimulee;
}