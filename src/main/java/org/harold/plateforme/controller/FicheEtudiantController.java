package org.harold.plateforme.controller;

import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.etudiant.FicheEtudiantEnseignantDTO;
import org.harold.plateforme.dto.etudiant.FicheEtudiantResponsableDTO;
import org.harold.plateforme.security.SecurityUtils;
import org.harold.plateforme.service.FicheEtudiantService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller de consultation des fiches étudiants.
 *
 * <p>Expose deux types de fiches selon le rôle :
 * fiche complète pour le responsable pédagogique
 * et fiche limitée à une matière pour l'enseignant.
 * L'identité de l'enseignant est extraite du token JWT.</p>
 *
 * @author Harold
 * @version 1.0
 */
@RestController
@RequestMapping("/api/fiches")
@RequiredArgsConstructor
public class FicheEtudiantController {

    private final FicheEtudiantService ficheEtudiantService;

    /**
     * Récupère la fiche complète d'un étudiant
     * pour le responsable pédagogique.
     *
     * <p>Contient toutes les notes par semestre,
     * moyennes, statuts de validation, rangs, écarts,
     * score de risque et remarques.</p>
     *
     * @param etudiantId        identifiant de l'étudiant
     * @param classeId          identifiant de la classe
     * @param promotionId       identifiant de la promotion
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  HTTP 200 avec la fiche complète
     */
    @GetMapping("/responsable/{etudiantId}")
    @PreAuthorize("hasRole('RESPONSABLE')")
    public ResponseEntity<FicheEtudiantResponsableDTO> getFicheResponsable(
            @PathVariable Long etudiantId,
            @RequestParam Long classeId,
            @RequestParam Long promotionId,
            @RequestParam Long anneeAcademiqueId) {
        return ResponseEntity.ok(
                ficheEtudiantService.getFicheResponsable(
                        etudiantId, classeId,
                        promotionId, anneeAcademiqueId));
    }

    /**
     * Récupère la fiche d'un étudiant pour un enseignant.
     *
     * <p>Limitée à la matière de l'enseignant :
     * notes, rang, KPI de la classe et remarques.
     * L'enseignant est identifié via le token JWT.</p>
     *
     * @param etudiantId        identifiant de l'étudiant
     * @param matiereId         identifiant de la matière
     * @param promotionId       identifiant de la promotion
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  HTTP 200 avec la fiche enseignant
     */
    @GetMapping("/enseignant/{etudiantId}")
    @PreAuthorize("hasRole('ENSEIGNANT')")
    public ResponseEntity<FicheEtudiantEnseignantDTO> getFicheEnseignant(
            @PathVariable Long etudiantId,
            @RequestParam Long matiereId,
            @RequestParam Long promotionId,
            @RequestParam Long anneeAcademiqueId) {
        Long enseignantId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(
                ficheEtudiantService.getFicheEnseignant(
                        etudiantId, matiereId,
                        promotionId, anneeAcademiqueId,
                        enseignantId));
    }
}