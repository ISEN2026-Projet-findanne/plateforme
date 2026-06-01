package org.harold.plateforme.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.csv.CsvImportResultDTO;
import org.harold.plateforme.dto.csv.CsvMappingDTO;
import org.harold.plateforme.service.CsvService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller de gestion des imports CSV/XLSX.
 *
 * <p>Gère l'import des étudiants et des notes
 * depuis les fichiers CSV et XLSX.
 * Supporte les formats THEIA (xlsx) et Aurion (csv latin-1)
 * pour les notes.</p>
 *
 * @author Harold
 * @version 1.0
 */
@RestController
@RequestMapping("/api/csv")
@RequiredArgsConstructor
public class CsvController {

    private final CsvService csvService;

    /**
     * Importe une liste d'étudiants depuis un fichier CSV.
     *
     * <p>Format attendu : nom, prenom, numeroEtudiant,
     * email, dateNaissance, niveau.
     * La promotion et l'année sont passées en paramètre.</p>
     *
     * @param fichier           le fichier CSV
     * @param promotionId       identifiant de la promotion
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  HTTP 200 avec le rapport d'import
     */
    @PostMapping("/etudiants")
    public ResponseEntity<CsvImportResultDTO> importerEtudiants(
            @RequestParam MultipartFile fichier,
            @RequestParam Long promotionId,
            @RequestParam Long anneeAcademiqueId) {
        return ResponseEntity.ok(
                csvService.importerEtudiants(
                        fichier, promotionId,
                        anneeAcademiqueId));
    }

    /**
     * Importe des notes depuis un fichier THEIA ou Aurion.
     *
     * <p>Détecte automatiquement le format du fichier :
     * THEIA (.xlsx) ou Aurion (.csv latin-1).</p>
     *
     * @param fichier           le fichier à importer
     * @param matiereId         identifiant de la matière
     * @param anneeAcademiqueId identifiant de l'année académique
     * @param enseignantId      identifiant de l'enseignant
     * @return                  HTTP 200 avec le rapport d'import
     */
    @PostMapping("/notes")
    public ResponseEntity<CsvImportResultDTO> importerNotes(
            @RequestParam MultipartFile fichier,
            @RequestParam Long matiereId,
            @RequestParam Long anneeAcademiqueId,
            @RequestParam Long enseignantId) {
        return ResponseEntity.ok(
                csvService.importerNotes(
                        fichier, matiereId,
                        anneeAcademiqueId, enseignantId));
    }

    /**
     * Sauvegarde un mapping de colonne CSV validé par l'utilisateur.
     *
     * <p>Enrichit le dictionnaire MappingCsv pour éviter
     * de redemander le même mapping à l'avenir.</p>
     *
     * @param dto   le mapping validé
     * @return      HTTP 200 si sauvegarde réussie
     */
    @PostMapping("/mapping")
    public ResponseEntity<Void> sauvegarderMapping(
            @Valid @RequestBody CsvMappingDTO dto) {
        csvService.sauvegarderMapping(dto);
        return ResponseEntity.ok().build();
    }
}