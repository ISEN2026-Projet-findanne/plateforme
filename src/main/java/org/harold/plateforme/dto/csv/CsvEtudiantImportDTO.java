package org.harold.plateforme.dto.csv;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO représentant une ligne du fichier CSV d'import d'étudiants.
 *
 * <p>Chaque instance correspond à une ligne du fichier CSV.
 * La promotion et l'année académique sont passées dans l'URL
 * et non dans le CSV.</p>
 *
 * <p>Format attendu du CSV :</p>
 * <pre>
 * nom,prenom,numeroEtudiant,email,dateNaissance,niveau
 * Dupont,Jean,ETU2024001,jean.dupont@ecole.fr,2003-05-15,N1
 * </pre>
 *
 * <p>email et dateNaissance sont optionnels et peuvent être vides.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class CsvEtudiantImportDTO {

    /**
     * Nom de l'étudiant.
     * Obligatoire dans le CSV.
     */
    private String nom;

    /**
     * Prénom de l'étudiant.
     * Obligatoire dans le CSV.
     */
    private String prenom;

    /**
     * Numéro étudiant unique.
     * Obligatoire dans le CSV.
     * Utilisé pour vérifier si l'étudiant existe déjà en base.
     */
    private String numeroEtudiant;

    /**
     * Email de l'étudiant.
     * Optionnel — peut être vide dans le CSV.
     */
    private String email;

    /**
     * Date de naissance de l'étudiant.
     * Optionnelle — peut être vide dans le CSV.
     * Format attendu : yyyy-MM-dd (ex: 2003-05-15).
     */
    private LocalDate dateNaissance;

    /**
     * Niveau de l'étudiant dans la promotion (ex: N1, N2, N3...).
     * Obligatoire dans le CSV.
     * Utilisé pour créer l'inscription dans la promotion.
     */
    private String niveau;

    /**
     * Numéro de ligne dans le fichier CSV.
     * Utilisé pour identifier les erreurs dans le rapport d'import.
     * Rempli automatiquement par le CsvService lors du parsing.
     */
    private Integer numeroLigne;

    /**
     * Indique si cette ligne est valide après parsing et validation.
     * Faux si un champ obligatoire est manquant ou invalide.
     * Rempli automatiquement par le CsvService.
     */
    private boolean valide = true;

    /**
     * Message d'erreur si la ligne est invalide.
     * Null si la ligne est valide.
     * Inclus dans le rapport d'import retourné à React.
     */
    private String messageErreur;
}