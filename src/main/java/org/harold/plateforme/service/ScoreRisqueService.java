package org.harold.plateforme.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.harold.plateforme.entity.Alerte;
import org.harold.plateforme.entity.AnneeAcademique;
import org.harold.plateforme.entity.Etudiant;
import org.harold.plateforme.entity.Notification;
import org.harold.plateforme.entity.Remarque;
import org.harold.plateforme.entity.ScoreRisqueHistorique;
import org.harold.plateforme.entity.Utilisateur;
import org.harold.plateforme.repository.AlerteRepository;
import org.harold.plateforme.repository.NotificationRepository;
import org.harold.plateforme.repository.RemarqueRepository;
import org.harold.plateforme.repository.ResponsablePromotionRepository;
import org.harold.plateforme.repository.ScoreRisqueHistoriqueRepository;
import org.harold.plateforme.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service de calcul du score de risque pédagogique.
 *
 * <p>Calcule un score composite sur 100 basé sur 4 critères :
 * moyenne générale (40%), matières en échec (25%),
 * tendance progression (20%), remarques négatives (15%).
 * Génère automatiquement des alertes et notifications
 * si le score dépasse 30.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class ScoreRisqueService {

    private final ScoreRisqueHistoriqueRepository scoreRepository;
    private final AlerteRepository alerteRepository;
    private final NotificationRepository notificationRepository;
    private final RemarqueRepository remarqueRepository;
    private final ResponsablePromotionRepository responsableRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final NoteService noteService;
    private final KpiService kpiService;
    private final EtudiantService etudiantService;
    private final AnneeAcademiqueService anneeAcademiqueService;

    /** ObjectMapper pour sérialiser les détails en JSON. */
    private final ObjectMapper objectMapper =
            new ObjectMapper().registerModule(new JavaTimeModule());

    // ===== CALCUL SCORE =====

    /**
     * Calcule et sauvegarde le score de risque d'un étudiant.
     *
     * <p>Calcule les 4 critères, détermine le niveau de risque,
     * sauvegarde dans l'historique et génère une alerte
     * si le score dépasse 30.</p>
     *
     * @param etudiantId        identifiant de l'étudiant
     * @param classeId          identifiant de la classe
     * @param promotionId       identifiant de la promotion
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  le score global calculé
     */
    @Transactional
    public Double calculer(
            Long etudiantId,
            Long classeId,
            Long promotionId,
            Long anneeAcademiqueId) {

        Etudiant etudiant = etudiantService.getEntityById(etudiantId);
        AnneeAcademique annee = anneeAcademiqueService
                .getEntityById(anneeAcademiqueId);

        // 1. Calculer critère moyenne (40%)
        Double scoreMoyenne = calculerScoreMoyenne(
                etudiantId, classeId, anneeAcademiqueId);

        // 2. Calculer critère matières en échec (25%)
        Double scoreMatieres = calculerScoreMatieres(
                etudiantId, classeId, anneeAcademiqueId);

        // 3. Calculer critère tendance (20%)
        Double scoreTendance = calculerScoreTendance(
                etudiantId, classeId, anneeAcademiqueId);

        // 4. Calculer critère remarques négatives (15%)
        Double scoreRemarques = calculerScoreRemarques(
                etudiantId, anneeAcademiqueId);

        // 5. Calculer le score global
        double scoreGlobal = (scoreMoyenne * 0.40)
                + (scoreMatieres * 0.25)
                + (scoreTendance * 0.20)
                + (scoreRemarques * 0.15);

        scoreGlobal = Math.min(Math.round(scoreGlobal * 100.0)
                / 100.0, 100.0);

        // 6. Sauvegarder dans l'historique
        ScoreRisqueHistorique historique = new ScoreRisqueHistorique();
        historique.setEtudiant(etudiant);
        historique.setAnneeAcademique(annee);
        historique.setScoreGlobal(scoreGlobal);
        historique.setScoreMoyenne(scoreMoyenne);
        historique.setScoreMatieres(scoreMatieres);
        historique.setScoreTendance(scoreTendance);
        historique.setScoreRemarques(scoreRemarques);
        scoreRepository.save(historique);

        // 7. Générer alerte si score > 30
        if (scoreGlobal > 30) {
            genererAlerte(
                    etudiant, annee, scoreGlobal,
                    scoreMoyenne, scoreMatieres,
                    scoreTendance, scoreRemarques,
                    promotionId);
        }

        return scoreGlobal;
    }

    // ===== CONSULTATION =====

    /**
     * Récupère le dernier score de risque calculé d'un étudiant.
     *
     * @param etudiantId        identifiant de l'étudiant
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  le dernier score ou null
     */
    @Transactional(readOnly = true)
    public Optional<ScoreRisqueHistorique> getScoreActuel(
            Long etudiantId,
            Long anneeAcademiqueId) {
        return scoreRepository
                .findTopByEtudiantIdAndAnneeAcademiqueIdOrderByCalculeLeDesc(
                        etudiantId, anneeAcademiqueId);
    }

    /**
     * Récupère l'historique des scores d'un étudiant.
     *
     * @param etudiantId        identifiant de l'étudiant
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  liste des scores triés par date
     */
    @Transactional(readOnly = true)
    public List<ScoreRisqueHistorique> getHistorique(
            Long etudiantId,
            Long anneeAcademiqueId) {
        return scoreRepository
                .findByEtudiantIdAndAnneeAcademiqueIdOrderByCalculeLeDesc(
                        etudiantId, anneeAcademiqueId);
    }

    /**
     * Récupère les étudiants à risque d'une promotion.
     *
     * @param promotionId       identifiant de la promotion
     * @param anneeAcademiqueId identifiant de l'année académique
     * @param seuilRisque       seuil minimum du score (ex: 56.0)
     * @return                  liste des scores au dessus du seuil
     */
    @Transactional(readOnly = true)
    public List<ScoreRisqueHistorique> getEtudiantsArisque(
            Long promotionId,
            Long anneeAcademiqueId,
            Double seuilRisque) {
        return scoreRepository
                .findAtRiskByPromotionIdAndAnneeAcademiqueId(
                        promotionId, anneeAcademiqueId, seuilRisque);
    }

    // ===== MÉTHODES PRIVÉES — CALCUL CRITÈRES =====

    /**
     * Calcule le score du critère moyenne générale.
     *
     * <p>Formule : ((20 - moyenne) / 20) x 100</p>
     *
     * @param etudiantId        identifiant de l'étudiant
     * @param classeId          identifiant de la classe
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  le score entre 0 et 100
     */
    private Double calculerScoreMoyenne(
            Long etudiantId,
            Long classeId,
            Long anneeAcademiqueId) {

        // Récupérer toutes les matières de la classe
        List<org.harold.plateforme.entity.Matiere> matieres =
                kpiService.getMatieresByClasse(classeId);

        if (matieres.isEmpty()) return 0.0;

        // Calculer la moyenne générale pondérée
        double sommeNotesPonderees = 0.0;
        double sommeCoeffs = 0.0;

        for (org.harold.plateforme.entity.Matiere matiere : matieres) {
            Double noteFinale = noteService.calculerNoteFinale(
                    etudiantId, matiere.getId(), anneeAcademiqueId);
            if (noteFinale != null) {
                sommeNotesPonderees +=
                        noteFinale * matiere.getCoefficient();
                sommeCoeffs += matiere.getCoefficient();
            }
        }

        if (sommeCoeffs == 0.0) return 0.0;
        double moyenne = sommeNotesPonderees / sommeCoeffs;
        return Math.max(0.0, ((20.0 - moyenne) / 20.0) * 100.0);
    }

    /**
     * Calcule le score du critère matières en échec.
     *
     * <p>Formule : (nb_echec / nb_total) x 100</p>
     *
     * @param etudiantId        identifiant de l'étudiant
     * @param classeId          identifiant de la classe
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  le score entre 0 et 100
     */
    private Double calculerScoreMatieres(
            Long etudiantId,
            Long classeId,
            Long anneeAcademiqueId) {

        List<org.harold.plateforme.entity.Matiere> matieres =
                kpiService.getMatieresByClasse(classeId);

        if (matieres.isEmpty()) return 0.0;

        long nbEchec = matieres.stream()
                .filter(m -> {
                    Double note = noteService.calculerNoteFinale(
                            etudiantId, m.getId(), anneeAcademiqueId);
                    return note != null && note < 10.0;
                })
                .count();

        return (nbEchec * 100.0) / matieres.size();
    }

    /**
     * Calcule le score du critère tendance de progression.
     *
     * <p>Formule : régression S1→S2 en %.
     * Si moyenne S2 < moyenne S1 → score positif (risque).
     * Si progression → score 0.</p>
     *
     * @param etudiantId        identifiant de l'étudiant
     * @param classeId          identifiant de la classe
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  le score entre 0 et 100
     */
    private Double calculerScoreTendance(
            Long etudiantId,
            Long classeId,
            Long anneeAcademiqueId) {

        List<org.harold.plateforme.entity.Matiere> matieresS1 =
                kpiService.getMatieresByClasseAndSemestre(classeId, 1);
        List<org.harold.plateforme.entity.Matiere> matieresS2 =
                kpiService.getMatieresByClasseAndSemestre(classeId, 2);

        Double moyS1 = calculerMoyenneEtudiant(
                etudiantId, matieresS1, anneeAcademiqueId);
        Double moyS2 = calculerMoyenneEtudiant(
                etudiantId, matieresS2, anneeAcademiqueId);

        if (moyS1 == null || moyS2 == null) return 0.0;

        // Régression = risque
        if (moyS2 < moyS1) {
            double regression = ((moyS1 - moyS2) / moyS1) * 100.0;
            return Math.min(regression, 100.0);
        }

        return 0.0;
    }

    /**
     * Calcule le score du critère remarques négatives.
     *
     * <p>Formule : min((nb_neg / 5) x 100, 100)</p>
     *
     * @param etudiantId        identifiant de l'étudiant
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  le score entre 0 et 100
     */
    private Double calculerScoreRemarques(
            Long etudiantId,
            Long anneeAcademiqueId) {

        Long nbRemarquesNegatives = remarqueRepository
                .countByEtudiantIdAndAnneeAcademiqueIdAndType(
                        etudiantId,
                        anneeAcademiqueId,
                        Remarque.TypeRemarque.NEGATIVE);

        return Math.min((nbRemarquesNegatives / 5.0) * 100.0, 100.0);
    }

    /**
     * Calcule la moyenne d'un étudiant pour une liste de matières.
     *
     * @param etudiantId        identifiant de l'étudiant
     * @param matieres          liste des matières
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  la moyenne ou null
     */
    private Double calculerMoyenneEtudiant(
            Long etudiantId,
            List<org.harold.plateforme.entity.Matiere> matieres,
            Long anneeAcademiqueId) {

        if (matieres.isEmpty()) return null;

        double sommeNotesPonderees = 0.0;
        double sommeCoeffs = 0.0;

        for (org.harold.plateforme.entity.Matiere matiere : matieres) {
            Double note = noteService.calculerNoteFinale(
                    etudiantId, matiere.getId(), anneeAcademiqueId);
            if (note != null) {
                sommeNotesPonderees += note * matiere.getCoefficient();
                sommeCoeffs += matiere.getCoefficient();
            }
        }

        if (sommeCoeffs == 0.0) return null;
        return sommeNotesPonderees / sommeCoeffs;
    }

    /**
     * Détermine le niveau d'alerte selon le score global.
     *
     * @param scoreGlobal   le score entre 0 et 100
     * @return              le niveau d'alerte
     */
    private Alerte.NiveauAlerte determinerNiveau(Double scoreGlobal) {
        if (scoreGlobal <= 30) return Alerte.NiveauAlerte.FAIBLE;
        if (scoreGlobal <= 55) return Alerte.NiveauAlerte.MODERE;
        if (scoreGlobal <= 75) return Alerte.NiveauAlerte.ELEVE;
        return Alerte.NiveauAlerte.CRITIQUE;
    }

    /**
     * Génère une alerte et les notifications associées.
     *
     * <p>Crée une alerte pour l'étudiant et génère
     * des notifications pour les enseignants et responsables
     * concernés selon le niveau de risque.</p>
     *
     * @param etudiant          l'étudiant concerné
     * @param annee             l'année académique
     * @param scoreGlobal       le score global
     * @param scoreMoyenne      score critère moyenne
     * @param scoreMatieres     score critère matières
     * @param scoreTendance     score critère tendance
     * @param scoreRemarques    score critère remarques
     * @param promotionId       identifiant de la promotion
     */
    private void genererAlerte(
            Etudiant etudiant,
            AnneeAcademique annee,
            Double scoreGlobal,
            Double scoreMoyenne,
            Double scoreMatieres,
            Double scoreTendance,
            Double scoreRemarques,
            Long promotionId) {

        // 1. Déterminer le niveau
        Alerte.NiveauAlerte niveau = determinerNiveau(scoreGlobal);

        // 2. Créer les détails JSON
        Map<String, Object> details = new HashMap<>();
        details.put("scoreMoyenne", scoreMoyenne);
        details.put("scoreMatieres", scoreMatieres);
        details.put("scoreTendance", scoreTendance);
        details.put("scoreRemarques", scoreRemarques);
        details.put("niveau", niveau.name());

        String detailsJson;
        try {
            detailsJson = objectMapper.writeValueAsString(details);
        } catch (Exception e) {
            detailsJson = "{}";
        }

        // 3. Créer l'alerte
        Alerte alerte = new Alerte();
        alerte.setEtudiant(etudiant);
        alerte.setAnneeAcademique(annee);
        alerte.setScoreRisque(scoreGlobal);
        alerte.setNiveau(niveau);
        alerte.setLue(false);
        alerte.setDetailsJson(detailsJson);
        alerteRepository.save(alerte);

        // 4. Générer notifications selon le niveau
        // MODERE → notifier les enseignants
        // ELEVE + CRITIQUE → notifier enseignants + responsables
        if (niveau == Alerte.NiveauAlerte.MODERE ||
                niveau == Alerte.NiveauAlerte.ELEVE ||
                niveau == Alerte.NiveauAlerte.CRITIQUE) {
            notifierEnseignants(alerte, etudiant.getId());
        }

        if (niveau == Alerte.NiveauAlerte.ELEVE ||
                niveau == Alerte.NiveauAlerte.CRITIQUE) {
            notifierResponsables(alerte, promotionId, annee.getId());
        }
    }

    /**
     * Notifie les enseignants de l'étudiant.
     *
     * @param alerte        l'alerte générée
     * @param etudiantId    identifiant de l'étudiant
     */
    private void notifierEnseignants(
            Alerte alerte,
            Long etudiantId) {

        // Récupérer les enseignants distincts de l'étudiant
        List<Utilisateur> enseignants = utilisateurRepository
                .findAll()
                .stream()
                .filter(u -> u.getRole() ==
                        org.harold.plateforme.entity.Utilisateur
                                .Role.ROLE_ENSEIGNANT)
                .collect(Collectors.toList());

        for (Utilisateur enseignant : enseignants) {
            creerNotification(alerte, enseignant);
        }
    }

    /**
     * Notifie les responsables de la promotion.
     *
     * @param alerte            l'alerte générée
     * @param promotionId       identifiant de la promotion
     * @param anneeAcademiqueId identifiant de l'année académique
     */
    private void notifierResponsables(
            Alerte alerte,
            Long promotionId,
            Long anneeAcademiqueId) {

        responsableRepository
                .findByPromotionIdAndAnneeAcademiqueId(
                        promotionId, anneeAcademiqueId)
                .forEach(rp -> creerNotification(
                        alerte, rp.getUtilisateur()));
    }

    /**
     * Crée une notification pour un utilisateur.
     *
     * @param alerte        l'alerte source
     * @param utilisateur   l'utilisateur à notifier
     */
    private void creerNotification(
            Alerte alerte,
            Utilisateur utilisateur) {

        // Éviter les doublons
        if (!notificationRepository
                .existsByUtilisateurIdAndAlerteId(
                        utilisateur.getId(), alerte.getId())) {
            Notification notification = new Notification();
            notification.setAlerte(alerte);
            notification.setUtilisateur(utilisateur);
            notification.setLue(false);
            notificationRepository.save(notification);
        }
    }
}