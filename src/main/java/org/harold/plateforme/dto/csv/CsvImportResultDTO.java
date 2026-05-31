package org.harold.plateforme.dto.csv;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO de réponse après un import CSV.
 *
 * <p>Retourné par Spring vers React après le traitement
 * d'un fichier CSV importé. Contient le bilan complet
 * de l'import : lignes traitées, insérées, ignorées
 * et erreurs détaillées.</p>
 *
 * <p>Gère les deux formats supportés :
 * THEIA (xlsx) et Aurion (csv encodé en latin-1).</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class CsvImportResultDTO {

    /**
     * Format du fichier importé.
     * Valeurs possibles : THEIA, AURION, INCONNU.
     */
    private String formatDetecte;

    /** Nombre total de lignes lues dans le fichier. */
    private Integer nbLignesLues;

    /** Nombre de lignes insérées avec succès en base. */
    private Integer nbLignesInsereees;

    /** Nombre de lignes ignorées (doublons ou données manquantes). */
    private Integer nbLignesIgnorees;

    /** Nombre de lignes en erreur. */
    private Integer nbLignesEnErreur;

    /**
     * Indique si l'import est un succès global.
     * Vrai si aucune ligne en erreur.
     */
    private boolean succes;

    /**
     * Liste des erreurs détaillées ligne par ligne.
     * Vide si aucune erreur.
     */
    private List<String> erreurs;

    /**
     * Liste des avertissements non bloquants.
     * Ex: valeurs NC converties en null,
     * encodage latin-1 détecté automatiquement.
     */
    private List<String> avertissements;

    /**
     * Liste des colonnes non reconnues nécessitant
     * un mapping manuel par l'utilisateur.
     * Vide si toutes les colonnes ont été reconnues.
     */
    private List<String> colonnesNonReconnues;
}