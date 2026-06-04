package org.harold.plateforme.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.rapport.RapportRequestDTO;
import org.harold.plateforme.service.RapportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller de génération des rapports PDF.
 *
 * <p>Génère des rapports PDF selon le type demandé :
 * étudiant, matière, promotion, comparaison ou simulation.
 * Le PDF est retourné directement en téléchargement.</p>
 *
 * @author Harold
 * @version 1.0
 */
@RestController
@RequestMapping("/api/rapports")
@RequiredArgsConstructor
public class RapportController {

    private final RapportService rapportService;

    /**
     * Génère un rapport PDF selon le type demandé.
     *
     * <p>Le PDF est retourné en pièce jointe pour
     * téléchargement direct depuis React.</p>
     *
     * @param dto   les paramètres du rapport
     * @return      HTTP 200 avec le PDF en byte[]
     */
    @PostMapping("/generer")
    @PreAuthorize("hasAnyRole('ENSEIGNANT','RESPONSABLE')")
    public ResponseEntity<byte[]> generer(
            @Valid @RequestBody RapportRequestDTO dto) {

        byte[] pdf = rapportService.generer(dto);

        // Nom du fichier selon le type de rapport
        String nomFichier = "rapport_"
                + dto.getTypeRapport().toLowerCase()
                + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + nomFichier + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .body(pdf);
    }
}