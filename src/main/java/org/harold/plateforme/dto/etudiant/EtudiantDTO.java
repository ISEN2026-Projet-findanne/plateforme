package org.harold.plateforme.dto.etudiant;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO de base pour les informations d'un étudiant.
 *
 * <p>Utilisé pour les listes et les opérations CRUD simples.
 * Retourné par Spring vers React.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class EtudiantDTO {

    /** Identifiant unique de l'étudiant. */
    private Long id;

    /** Nom de l'étudiant. */
    private String nom;

    /** Prénom de l'étudiant. */
    private String prenom;

    /** Numéro étudiant unique (identifiant métier). */
    private String numeroEtudiant;

    /** Date de naissance de l'étudiant. */
    private LocalDate dateNaissance;

    /** Email de l'étudiant. */
    private String email;

    /** Nom de la promotion active de l'étudiant. */
    private String promotionNom;

    /** Niveau actuel de l'étudiant (N1, N2, N3...). */
    private String niveau;

    /** Année académique en cours. */
    private String anneeAcademique;
}