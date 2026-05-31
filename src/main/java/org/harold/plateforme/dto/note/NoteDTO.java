package org.harold.plateforme.dto.note;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO de réponse représentant une note.
 *
 * <p>Retourné par Spring vers React après une saisie
 * ou une modification de note.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class NoteDTO {

    /** Identifiant unique de la note. */
    private Long id;

    /** Identifiant de l'étudiant concerné. */
    private Long etudiantId;

    /** Nom de l'étudiant concerné. */
    private String etudiantNom;

    /** Prénom de l'étudiant concerné. */
    private String etudiantPrenom;

    /** Identifiant de la matière. */
    private Long matiereId;

    /** Nom de la matière. */
    private String matiereNom;

    /** Identifiant de l'année académique. */
    private Long anneeAcademiqueId;

    /** Libellé de l'année académique (ex: 2024/2025). */
    private String anneeAcademique;

    /** Nom du groupe classe (ex: TD1, TD2...). */
    private String groupeNom;

    /**
     * Type de la note.
     * Valeurs possibles : CC, EXAMEN_FINAL, TP, RATTRAPAGE.
     */
    private String type;

    /**
     * Valeur de la note entre 0 et 20.
     */
    private Double valeur;

    /**
     * Indique si cette note est une note de rattrapage.
     * Si vrai, elle remplace intégralement la note EF.
     */
    private boolean estRattrapage;

    /** Date et heure de saisie de la note. */
    private LocalDateTime saisieLe;

    /** Nom de l'enseignant ayant saisi la note. */
    private String saisiParNom;

    /** Prénom de l'enseignant ayant saisi la note. */
    private String saisiParPrenom;
}