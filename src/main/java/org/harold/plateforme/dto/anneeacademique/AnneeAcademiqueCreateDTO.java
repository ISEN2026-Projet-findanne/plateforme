package org.harold.plateforme.dto.anneeacademique;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de requête pour la création d'une année académique.
 *
 * <p>Contient les données envoyées par React lors de la création
 * d'une nouvelle année académique par l'administrateur.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class AnneeAcademiqueCreateDTO {

    /**
     * Libellé court de l'année académique.
     * Exemple : 2024/2025.
     * Obligatoire et unique en base.
     */
    @NotBlank(message = "L'année académique est obligatoire")
    private String annee;

    /**
     * Libellé long de l'année académique.
     * Obligatoire.
     */
    @NotBlank(message = "Le libellé est obligatoire")
    private String libelle;
}