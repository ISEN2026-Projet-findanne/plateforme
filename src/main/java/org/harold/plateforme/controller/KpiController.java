package org.harold.plateforme.controller;

import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.kpi.ComparaisonPromoDTO;
import org.harold.plateforme.dto.kpi.KpiAnnuelDTO;
import org.harold.plateforme.dto.kpi.KpiMatiereDTO;
import org.harold.plateforme.dto.kpi.KpiSemestreDTO;
import org.harold.plateforme.service.KpiService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller de calcul et consultation des KPI pédagogiques.
 *
 * <p>Expose les statistiques par matière, semestre et année
 * pour une promotion ou un groupe donné.
 * Gère aussi les comparaisons inter-promotions
 * et inter-années.</p>
 *
 * @author Harold
 * @version 1.0
 */
@RestController
@RequestMapping("/api/kpi")
@RequiredArgsConstructor
public class KpiController {

    private final KpiService kpiService;

    /**
     * Calcule les 8 KPI d'une matière pour une promotion.
     *
     * @param matiereId         identifiant de la matière
     * @param promotionId       identifiant de la promotion
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  HTTP 200 avec les 8 KPI
     */
    @GetMapping("/matiere/{matiereId}/promotion/{promotionId}")
    @PreAuthorize("hasAnyRole('ENSEIGNANT','RESPONSABLE')")
    public ResponseEntity<KpiMatiereDTO> getKpiMatiereByPromotion(
            @PathVariable Long matiereId,
            @PathVariable Long promotionId,
            @RequestParam Long anneeAcademiqueId) {
        return ResponseEntity.ok(
                kpiService.getKpiMatiereByPromotion(
                        matiereId, promotionId,
                        anneeAcademiqueId));
    }

    /**
     * Calcule les 8 KPI d'une matière pour un groupe.
     *
     * @param matiereId         identifiant de la matière
     * @param groupeId          identifiant du groupe
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  HTTP 200 avec les 8 KPI
     */
    @GetMapping("/matiere/{matiereId}/groupe/{groupeId}")
    @PreAuthorize("hasAnyRole('ENSEIGNANT','RESPONSABLE')")
    public ResponseEntity<KpiMatiereDTO> getKpiMatiereByGroupe(
            @PathVariable Long matiereId,
            @PathVariable Long groupeId,
            @RequestParam Long anneeAcademiqueId) {
        return ResponseEntity.ok(
                kpiService.getKpiMatiereByGroupe(
                        matiereId, groupeId,
                        anneeAcademiqueId));
    }

    /**
     * Calcule les 7 KPI d'un semestre pour une promotion.
     *
     * @param numeroSemestre    numéro du semestre (1 ou 2)
     * @param promotionId       identifiant de la promotion
     * @param classeId          identifiant de la classe
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  HTTP 200 avec les 7 KPI
     */
    @GetMapping("/semestre/{numeroSemestre}/promotion/{promotionId}")
    @PreAuthorize("hasRole('RESPONSABLE')")
    public ResponseEntity<KpiSemestreDTO> getKpiSemestreByPromotion(
            @PathVariable Integer numeroSemestre,
            @PathVariable Long promotionId,
            @RequestParam Long classeId,
            @RequestParam Long anneeAcademiqueId) {
        return ResponseEntity.ok(
                kpiService.getKpiSemestreByPromotion(
                        classeId, numeroSemestre,
                        promotionId, anneeAcademiqueId));
    }

    /**
     * Calcule les 7 KPI annuels pour une promotion.
     *
     * @param promotionId       identifiant de la promotion
     * @param classeId          identifiant de la classe
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  HTTP 200 avec les 7 KPI annuels
     */
    @GetMapping("/annuel/promotion/{promotionId}")
    @PreAuthorize("hasRole('RESPONSABLE')")
    public ResponseEntity<KpiAnnuelDTO> getKpiAnnuelByPromotion(
            @PathVariable Long promotionId,
            @RequestParam Long classeId,
            @RequestParam Long anneeAcademiqueId) {
        return ResponseEntity.ok(
                kpiService.getKpiAnnuelByPromotion(
                        classeId, promotionId,
                        anneeAcademiqueId));
    }

    /**
     * Compare les KPI d'une matière entre plusieurs promotions.
     *
     * @param matiereId         identifiant de la matière
     * @param promotionIds      liste des identifiants de promotions
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  HTTP 200 avec le DTO de comparaison
     */
    @GetMapping("/comparaison/inter-promos")
    @PreAuthorize("hasRole('RESPONSABLE')")
    public ResponseEntity<ComparaisonPromoDTO> comparerInterPromos(
            @RequestParam Long matiereId,
            @RequestParam List<Long> promotionIds,
            @RequestParam Long anneeAcademiqueId) {
        return ResponseEntity.ok(
                kpiService.comparerMatiereInterPromos(
                        matiereId, promotionIds,
                        anneeAcademiqueId));
    }

    /**
     * Compare les KPI d'une matière sur plusieurs années
     * pour une même promotion.
     *
     * @param matiereId             identifiant de la matière
     * @param promotionId           identifiant de la promotion
     * @param anneeAcademiqueIds    liste des identifiants d'années
     * @return                      HTTP 200 avec le DTO de comparaison
     */
    @GetMapping("/comparaison/inter-annees")
    @PreAuthorize("hasRole('RESPONSABLE')")
    public ResponseEntity<ComparaisonPromoDTO> comparerInterAnnees(
            @RequestParam Long matiereId,
            @RequestParam Long promotionId,
            @RequestParam List<Long> anneeAcademiqueIds) {
        return ResponseEntity.ok(
                kpiService.comparerMatiereInterAnnees(
                        matiereId, promotionId,
                        anneeAcademiqueIds));
    }
}