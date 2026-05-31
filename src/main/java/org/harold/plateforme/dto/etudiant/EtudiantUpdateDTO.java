package org.harold.plateforme.dto.etudiant;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO de modification d'un étudiant.
 *
 * <p>Contient les données envoyées par React lors de la modification
 * d'un étudiant existant. L'id est passé dans l'URL, pas dans le body.
 * promotionId et anneeAcademiqueId ne sont pas modifiables ici —
 * un changement de promo passe par le service d'inscription.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class EtudiantUpdateDTO {

    /** Nom de l'étudiant. Obligatoire. */
    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    /** Prénom de l'étudiant. Obligatoire. */
    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    /** Date de naissance. Optionnelle. */
    private LocalDate dateNaissance;

    /** Email de l'étudiant. Optionnel mais doit être valide. */
    @Email(message = "L'email doit être valide")
    private String email;
}