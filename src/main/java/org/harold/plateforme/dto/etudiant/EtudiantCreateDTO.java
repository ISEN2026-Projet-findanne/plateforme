package org.harold.plateforme.dto.etudiant;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO de création d'un étudiant.
 *
 * <p>Contient les données envoyées par React lors de la création
 * d'un nouvel étudiant.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class EtudiantCreateDTO {

    /** Nom de l'étudiant. Obligatoire. */
    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    /** Prénom de l'étudiant. Obligatoire. */
    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    /** Numéro étudiant unique. Obligatoire. */
    @NotBlank(message = "Le numéro étudiant est obligatoire")
    private String numeroEtudiant;

    /** Date de naissance. Optionnelle. */
    private LocalDate dateNaissance;

    /** Email de l'étudiant. Optionnel mais doit être valide. */
    @Email(message = "L'email doit être valide")
    private String email;

    /** Identifiant de la promotion. Obligatoire. */
    @NotNull(message = "La promotion est obligatoire")
    private Long promotionId;

    /** Identifiant de l'année académique. Obligatoire. */
    @NotNull(message = "L'année académique est obligatoire")
    private Long anneeAcademiqueId;

    /** Niveau de l'étudiant (N1, N2, N3...). Obligatoire. */
    @NotBlank(message = "Le niveau est obligatoire")
    private String niveau;
}