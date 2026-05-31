package org.harold.plateforme.dto.csv;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO représentant le mapping d'une colonne CSV non reconnue.
 *
 * <p>Utilisé dans les deux sens :</p>
 * <ul>
 *   <li>Spring vers React : liste des colonnes non reconnues
 *       à mapper manuellement par l'utilisateur.</li>
 *   <li>React vers Spring : mapping manuel validé par
 *       l'utilisateur pour relancer l'import.</li>
 * </ul>
 *
 * <p>Une fois validé, le mapping est enregistré dans la table
 * MAPPING_CSV pour enrichir le dictionnaire de synonymes
 * et éviter de redemander le même mapping à l'avenir.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Getter
@Setter
public class CsvMappingDTO {

    /**
     * Nom de la colonne trouvée dans le fichier CSV.
     * Exemples THEIA : "Matricule", "Groupes", "Moyenne standard..."
     * Exemples Aurion : "id.Apprenant", "Code.Épreuve", "Note numérique"
     * Obligatoire.
     */
    @NotBlank(message = "La colonne source est obligatoire")
    private String colonneSource;

    /**
     * Champ interne correspondant dans le système.
     * Exemples : "numeroEtudiant", "note.valeur", "matiere.code"
     * Obligatoire lors du mapping manuel par l'utilisateur.
     * Null quand envoyé par Spring vers React (à remplir).
     */
    private String champInterne;

    /**
     * Contexte de l'import concerné.
     * Valeurs possibles : "etudiant", "note", "matiere", "groupe"
     * Permet d'affiner le dictionnaire de synonymes.
     */
    private String contexte;

    /**
     * Exemple de valeur trouvée dans cette colonne.
     * Affiché à l'utilisateur pour l'aider à identifier
     * le bon champ interne.
     * Exemple : "nina.abadie", "7,07", "2526_HEI_A3_456"
     */
    private String exempleValeur;

    /**
     * Indique si ce mapping a été validé par l'utilisateur.
     * Faux par défaut, passe à vrai quand l'utilisateur
     * confirme le mapping manuel.
     */
    private boolean valide = false;
}