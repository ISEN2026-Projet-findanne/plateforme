package org.harold.plateforme.controller;

import lombok.RequiredArgsConstructor;
import org.harold.plateforme.entity.ScoreRisqueHistorique;
import org.harold.plateforme.service.ScoreRisqueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * Controller de gestion du score de risque pédagogique.
 *
 * <p>Gère le calcul et la consultation des scores de risque.
 * Le calcul génère automatiquement des alertes et notifications
 * selon le niveau de risque détecté.</p>
 *
 * @author Harold
 * @version 1.0
 */
@RestController
@RequestMapping("/api/scores-risque")
@RequiredArgsConstructor
public class ScoreRisqueController {

    private final ScoreRisqueService scoreRisqueService;

    /**
     * Calcule le score de risque d'un étudiant.
     *
     * <p>Sauvegarde dans l'historique et génère
     * automatiquement une alerte si score > 30.</p>
     *
     * @param etudiantId        identifiant de l'étudiant
     * @param classeId          identifiant de la classe
     * @param promotionId       identifiant de la promotion
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  HTTP 200 avec le score calculé
     */
    @PostMapping("/calculer")
    public ResponseEntity<Double> calculer(
            @RequestParam Long etudiantId,
            @RequestParam Long classeId,
            @RequestParam Long promotionId,
            @RequestParam Long anneeAcademiqueId) {
        return ResponseEntity.ok(
                scoreRisqueService.calculer(
                        etudiantId, classeId,
                        promotionId, anneeAcademiqueId));
    }

    /**
     * Récupère le dernier score de risque d'un étudiant.
     *
     * @param etudiantId        identifiant de l'étudiant
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  HTTP 200 avec le score actuel
     *                          ou HTTP 404 si pas encore calculé
     */
    @GetMapping("/etudiant/{etudiantId}/actuel")
    public ResponseEntity<ScoreRisqueHistorique> getScoreActuel(
            @PathVariable Long etudiantId,
            @RequestParam Long anneeAcademiqueId) {
        Optional<ScoreRisqueHistorique> score =
                scoreRisqueService.getScoreActuel(
                        etudiantId, anneeAcademiqueId);
        return score.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Récupère l'historique des scores d'un étudiant.
     *
     * @param etudiantId        identifiant de l'étudiant
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  HTTP 200 avec la liste des scores
     */
    @GetMapping("/etudiant/{etudiantId}/historique")
    public ResponseEntity<List<ScoreRisqueHistorique>> getHistorique(
            @PathVariable Long etudiantId,
            @RequestParam Long anneeAcademiqueId) {
        return ResponseEntity.ok(
                scoreRisqueService.getHistorique(
                        etudiantId, anneeAcademiqueId));
    }

    /**
     * Récupère les étudiants à risque d'une promotion.
     *
     * <p>Filtrés par seuil de score.
     * Par défaut seuil = 56.0 (niveau ELEVE).</p>
     *
     * @param promotionId       identifiant de la promotion
     * @param anneeAcademiqueId identifiant de l'année académique
     * @param seuilRisque       seuil minimum du score
     * @return                  HTTP 200 avec la liste des scores
     */
    @GetMapping("/promotion/{promotionId}/a-risque")
    public ResponseEntity<List<ScoreRisqueHistorique>> getEtudiantsARisque(
            @PathVariable Long promotionId,
            @RequestParam Long anneeAcademiqueId,
            @RequestParam(defaultValue = "56.0") Double seuilRisque) {
        return ResponseEntity.ok(
                scoreRisqueService.getEtudiantsArisque(
                        promotionId, anneeAcademiqueId,
                        seuilRisque));
    }
}