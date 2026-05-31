package org.harold.plateforme.dto.etudiant;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO représentant une remarque enseignant sur un étudiant.
 *
 * <p>Contient le contenu de la remarque, son type,
 * l'enseignant qui l'a rédigée et la date de création.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class RemarqueDTO {

    /** Identifiant de la remarque. */
    private Long id;

    /** Contenu de la remarque. */
    private String contenu;

    /**
     * Type de la remarque.
     * Valeurs possibles : POSITIVE, NEGATIVE, NEUTRE.
     */
    private String type;

    /** Nom de l'enseignant ayant rédigé la remarque. */
    private String enseignantNom;

    /** Prénom de l'enseignant ayant rédigé la remarque. */
    private String enseignantPrenom;

    /** Nom du groupe classe concerné par la remarque. */
    private String groupeNom;

    /**
     * Date et heure de création de la remarque.
     * Triées par date décroissante dans la fiche étudiant.
     */
    private LocalDateTime createdAt;
}