package org.harold.plateforme.service;

import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.etudiant.FicheEtudiantResponsableDTO;
import org.harold.plateforme.dto.kpi.ComparaisonPromoDTO;
import org.harold.plateforme.dto.kpi.KpiAnnuelDTO;
import org.harold.plateforme.dto.kpi.KpiMatiereDTO;
import org.harold.plateforme.dto.rapport.RapportRequestDTO;
import org.harold.plateforme.dto.kpi.SimulationRequestDTO;
import org.harold.plateforme.dto.kpi.SimulationSemestreResultDTO;
import org.harold.plateforme.exception.ValidationException;
import org.harold.plateforme.util.PdfGeneratorUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service de génération des rapports PDF.
 *
 * <p>Orchestre la génération des rapports en récupérant
 * les données depuis les services métier et en les
 * passant à PdfGeneratorUtils pour la génération PDF.
 * Tous les rapports sont en mise en forme simple V1.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class RapportService {

    private final FicheEtudiantService ficheEtudiantService;
    private final KpiService kpiService;
    private final SimulationService simulationService;
    private final ClasseService classeService;

    // ===== GÉNÉRATION =====

    /**
     * Génère un rapport PDF selon le type demandé.
     *
     * <p>Dispatcher principal qui délègue à la méthode
     * appropriée selon le typeRapport du DTO.</p>
     *
     * @param dto   les paramètres du rapport
     * @return      le PDF généré en byte[]
     * @throws ValidationException  si typeRapport invalide
     *                              ou paramètres manquants
     */
    @Transactional(readOnly = true)
    public byte[] generer(RapportRequestDTO dto) {
        return switch (dto.getTypeRapport()) {
            case "ETUDIANT" -> genererRapportEtudiant(dto);
            case "MATIERE" -> genererRapportMatiere(dto);
            case "PROMOTION" -> genererRapportPromotion(dto);
            case "COMPARAISON" -> genererRapportComparaison(dto);
            case "SIMULATION" -> genererRapportSimulation(dto);
            default -> throw new ValidationException(
                    "typeRapport",
                    "Type de rapport invalide. Valeurs : " +
                            "ETUDIANT, MATIERE, PROMOTION, " +
                            "COMPARAISON, SIMULATION");
        };
    }

    // ===== MÉTHODES PRIVÉES =====

    /**
     * Génère le rapport PDF d'un étudiant.
     *
     * @param dto   les paramètres du rapport
     * @return      le PDF généré
     * @throws ValidationException  si etudiantId manquant
     */
    private byte[] genererRapportEtudiant(RapportRequestDTO dto) {
        if (dto.getEtudiantId() == null) {
            throw new ValidationException(
                    "etudiantId",
                    "L'identifiant étudiant est obligatoire " +
                            "pour un rapport ETUDIANT");
        }
        if (dto.getPromotionId() == null) {
            throw new ValidationException(
                    "promotionId",
                    "L'identifiant promotion est obligatoire " +
                            "pour un rapport ETUDIANT");
        }

        // Récupérer la classe de la promotion
        Long classeId = getClasseId(
                dto.getPromotionId(), dto.getAnneeAcademiqueId());

        FicheEtudiantResponsableDTO fiche =
                ficheEtudiantService.getFicheResponsable(
                        dto.getEtudiantId(),
                        classeId,
                        dto.getPromotionId(),
                        dto.getAnneeAcademiqueId());

        return PdfGeneratorUtils.genererRapportEtudiant(fiche);
    }

    /**
     * Génère le rapport PDF des KPI d'une matière.
     *
     * @param dto   les paramètres du rapport
     * @return      le PDF généré
     * @throws ValidationException  si matiereId manquant
     */
    private byte[] genererRapportMatiere(RapportRequestDTO dto) {
        if (dto.getMatiereId() == null) {
            throw new ValidationException(
                    "matiereId",
                    "L'identifiant matière est obligatoire " +
                            "pour un rapport MATIERE");
        }
        if (dto.getPromotionId() == null) {
            throw new ValidationException(
                    "promotionId",
                    "L'identifiant promotion est obligatoire " +
                            "pour un rapport MATIERE");
        }

        KpiMatiereDTO kpi = kpiService.getKpiMatiereByPromotion(
                dto.getMatiereId(),
                dto.getPromotionId(),
                dto.getAnneeAcademiqueId());

        return PdfGeneratorUtils.genererRapportMatiere(kpi);
    }

    /**
     * Génère le rapport PDF des KPI annuels d'une promotion.
     *
     * @param dto   les paramètres du rapport
     * @return      le PDF généré
     * @throws ValidationException  si promotionId manquant
     */
    private byte[] genererRapportPromotion(RapportRequestDTO dto) {
        if (dto.getPromotionId() == null) {
            throw new ValidationException(
                    "promotionId",
                    "L'identifiant promotion est obligatoire " +
                            "pour un rapport PROMOTION");
        }

        Long classeId = getClasseId(
                dto.getPromotionId(), dto.getAnneeAcademiqueId());

        KpiAnnuelDTO kpi = kpiService.getKpiAnnuelByPromotion(
                classeId,
                dto.getPromotionId(),
                dto.getAnneeAcademiqueId());

        return PdfGeneratorUtils.genererRapportPromotion(kpi);
    }

    /**
     * Génère le rapport PDF d'une comparaison.
     *
     * @param dto   les paramètres du rapport
     * @return      le PDF généré
     * @throws ValidationException  si paramètres manquants
     */
    private byte[] genererRapportComparaison(
            RapportRequestDTO dto) {
        if (dto.getMatiereId() == null) {
            throw new ValidationException(
                    "matiereId",
                    "L'identifiant matière est obligatoire " +
                            "pour un rapport COMPARAISON");
        }
        if (dto.getPromotionId() == null) {
            throw new ValidationException(
                    "promotionId",
                    "L'identifiant promotion est obligatoire " +
                            "pour un rapport COMPARAISON");
        }

        // Comparaison inter-années par défaut
        ComparaisonPromoDTO comparaison =
                kpiService.comparerMatiereInterAnnees(
                        dto.getMatiereId(),
                        dto.getPromotionId(),
                        List.of(dto.getAnneeAcademiqueId()));

        return PdfGeneratorUtils.genererRapportComparaison(
                comparaison);
    }

    /**
     * Génère le rapport PDF d'une simulation pédagogique.
     *
     * @param dto   les paramètres du rapport
     * @return      le PDF généré
     * @throws ValidationException  si simulation manquante
     */
    private byte[] genererRapportSimulation(
            RapportRequestDTO dto) {
        if (dto.getSimulation() == null) {
            throw new ValidationException(
                    "simulation",
                    "Les données de simulation sont obligatoires " +
                            "pour un rapport SIMULATION");
        }

        SimulationRequestDTO simRequest = new SimulationRequestDTO();
        simRequest.setEtudiantId(
                dto.getSimulation().getEtudiantId());
        simRequest.setMatiereId(
                dto.getSimulation().getMatiereId());
        simRequest.setAnneeAcademiqueId(
                dto.getAnneeAcademiqueId());
        simRequest.setNoteSimulee(
                dto.getSimulation().getNoteSimulee());

        SimulationSemestreResultDTO resultat =
                simulationService.simulerSemestre(simRequest);

        return PdfGeneratorUtils.genererRapportSimulation(resultat);
    }

    /**
     * Récupère l'identifiant de la classe d'une promotion
     * pour une année académique.
     *
     * @param promotionId       identifiant de la promotion
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  l'identifiant de la classe
     * @throws ValidationException  si aucune classe trouvée
     */
    private Long getClasseId(
            Long promotionId,
            Long anneeAcademiqueId) {
        return classeService
                .getByPromotionAndAnnee(
                        promotionId, anneeAcademiqueId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ValidationException(
                        "classe",
                        "Aucune classe trouvée pour cette " +
                                "promotion et cette année"))
                .getClasseId();
    }
}