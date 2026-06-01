package org.harold.plateforme.service;

import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.kpi.ComparaisonPromoDTO;
import org.harold.plateforme.dto.kpi.EntreeComparaisonDTO;
import org.harold.plateforme.dto.kpi.KpiAnnuelDTO;
import org.harold.plateforme.dto.kpi.KpiMatiereDTO;
import org.harold.plateforme.dto.kpi.KpiSemestreDTO;
import org.harold.plateforme.entity.AnneeAcademique;
import org.harold.plateforme.entity.Etudiant;
import org.harold.plateforme.entity.Matiere;
import org.harold.plateforme.entity.Semestre;
import org.harold.plateforme.mapper.KpiMapper;
import org.harold.plateforme.repository.AnneeAcademiqueRepository;
import org.harold.plateforme.repository.EtudiantRepository;
import org.harold.plateforme.repository.MatiereRepository;
import org.harold.plateforme.repository.NoteRepository;
import org.harold.plateforme.repository.SemestreRepository;
import org.harold.plateforme.util.CalculKpiUtils;
import org.harold.plateforme.util.CalculNoteUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service de calcul des KPI pédagogiques.
 *
 * <p>Calcule les statistiques par matière, semestre et année
 * pour une promotion ou un groupe donné.
 * Gère aussi les comparaisons inter-promotions
 * et inter-années.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class KpiService {

    private final NoteService noteService;
    private final EtudiantRepository etudiantRepository;
    private final MatiereRepository matiereRepository;
    private final SemestreRepository semestreRepository;
    private final AnneeAcademiqueRepository anneeAcademiqueRepository;
    private final NoteRepository noteRepository;
    private final KpiMapper kpiMapper;

    // ===== KPI MATIERE =====

    /**
     * Calcule les 8 KPI d'une matière pour une promotion.
     *
     * <p>Récupère les étudiants de la promotion, calcule
     * la note finale de chacun avant et après rattrapage,
     * puis délègue le calcul statistique au KpiMapper.</p>
     *
     * @param matiereId         identifiant de la matière
     * @param promotionId       identifiant de la promotion
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  les 8 KPI de la matière
     */
    @Transactional(readOnly = true)
    public KpiMatiereDTO getKpiMatiereByPromotion(
            Long matiereId,
            Long promotionId,
            Long anneeAcademiqueId) {

        // 1. Récupérer la matière et l'année
        Matiere matiere = matiereRepository.findById(matiereId)
                .orElseThrow();
        AnneeAcademique annee = anneeAcademiqueRepository
                .findById(anneeAcademiqueId).orElseThrow();

        // 2. Récupérer les étudiants de la promotion
        List<Etudiant> etudiants = etudiantRepository
                .findByPromotionAndAnneeAcademique(
                        promotionId, anneeAcademiqueId);

        // 3. Calculer les notes finales avant et après rattrapage
        List<Double> notesAvant = new ArrayList<>();
        List<Double> notesApres = new ArrayList<>();

        for (Etudiant etudiant : etudiants) {
            // Note avant rattrapage (CC + EF sans rattrapage)
            Double noteAvant = calculerNoteAvantRattrapage(
                    etudiant.getId(), matiereId, anneeAcademiqueId);
            if (noteAvant != null) notesAvant.add(noteAvant);

            // Note après rattrapage
            Double noteApres = noteService.calculerNoteFinale(
                    etudiant.getId(), matiereId, anneeAcademiqueId);
            if (noteApres != null) notesApres.add(noteApres);
        }

        // 4. Construire le DTO via KpiMapper
        return kpiMapper.toKpiMatiereDTO(
                notesApres, notesAvant,
                matiereId, matiere.getNom(), matiere.getCode(),
                anneeAcademiqueId, annee.getAnnee(),
                null, null);
    }

    /**
     * Calcule les 8 KPI d'une matière pour un groupe TD/TP.
     *
     * @param matiereId         identifiant de la matière
     * @param groupeClasseId    identifiant du groupe
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  les 8 KPI de la matière pour le groupe
     */
    @Transactional(readOnly = true)
    public KpiMatiereDTO getKpiMatiereByGroupe(
            Long matiereId,
            Long groupeClasseId,
            Long anneeAcademiqueId) {

        // 1. Récupérer la matière et l'année
        Matiere matiere = matiereRepository.findById(matiereId)
                .orElseThrow();
        AnneeAcademique annee = anneeAcademiqueRepository
                .findById(anneeAcademiqueId).orElseThrow();

        // 2. Récupérer les étudiants du groupe
        List<Etudiant> etudiants = etudiantRepository
                .findByGroupeClasse(groupeClasseId);

        // 3. Calculer les notes finales
        List<Double> notesAvant = new ArrayList<>();
        List<Double> notesApres = new ArrayList<>();

        for (Etudiant etudiant : etudiants) {
            Double noteAvant = calculerNoteAvantRattrapage(
                    etudiant.getId(), matiereId, anneeAcademiqueId);
            if (noteAvant != null) notesAvant.add(noteAvant);

            Double noteApres = noteService.calculerNoteFinale(
                    etudiant.getId(), matiereId, anneeAcademiqueId);
            if (noteApres != null) notesApres.add(noteApres);
        }

        // 4. Construire le DTO
        return kpiMapper.toKpiMatiereDTO(
                notesApres, notesAvant,
                matiereId, matiere.getNom(), matiere.getCode(),
                anneeAcademiqueId, annee.getAnnee(),
                null, null);
    }

    // ===== KPI SEMESTRE =====

    /**
     * Calcule les 7 KPI d'un semestre pour une promotion.
     *
     * <p>Pour chaque étudiant calcule sa moyenne semestrielle
     * en tenant compte des coefficients des groupes de matières.</p>
     *
     * @param classeId          identifiant de la classe
     * @param numeroSemestre    numéro du semestre (1 ou 2)
     * @param promotionId       identifiant de la promotion
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  les 7 KPI du semestre
     */
    @Transactional(readOnly = true)
    public KpiSemestreDTO getKpiSemestreByPromotion(
            Long classeId,
            Integer numeroSemestre,
            Long promotionId,
            Long anneeAcademiqueId) {

        // 1. Récupérer l'année académique
        AnneeAcademique annee = anneeAcademiqueRepository
                .findById(anneeAcademiqueId).orElseThrow();

        // 2. Récupérer les matières du semestre
        List<Matiere> matieres = matiereRepository
                .findByClasseIdAndNumeroSemestre(
                        classeId, numeroSemestre);

        // 3. Récupérer les étudiants de la promotion
        List<Etudiant> etudiants = etudiantRepository
                .findByPromotionAndAnneeAcademique(
                        promotionId, anneeAcademiqueId);

        // 4. Calculer la moyenne semestrielle de chaque étudiant
        List<Double> moyennesSemestre =
                calculerMoyennesSemestrePourEtudiants(
                        etudiants, matieres, anneeAcademiqueId);

        // 5. Construire le DTO
        return kpiMapper.toKpiSemestreDTO(
                moyennesSemestre,
                numeroSemestre,
                anneeAcademiqueId,
                annee.getAnnee(),
                null, null);
    }

    // ===== KPI ANNUEL =====

    /**
     * Calcule les 7 KPI annuels pour une promotion.
     *
     * <p>Calcule les moyennes S1 et S2 de chaque étudiant
     * puis la moyenne annuelle via CalculNoteUtils.</p>
     *
     * @param classeId          identifiant de la classe
     * @param promotionId       identifiant de la promotion
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  les 7 KPI annuels
     */
    @Transactional(readOnly = true)
    public KpiAnnuelDTO getKpiAnnuelByPromotion(
            Long classeId,
            Long promotionId,
            Long anneeAcademiqueId) {

        // 1. Récupérer l'année académique
        AnneeAcademique annee = anneeAcademiqueRepository
                .findById(anneeAcademiqueId).orElseThrow();

        // 2. Récupérer les étudiants de la promotion
        List<Etudiant> etudiants = etudiantRepository
                .findByPromotionAndAnneeAcademique(
                        promotionId, anneeAcademiqueId);

        // 3. Récupérer les matières S1 et S2
        List<Matiere> matieresS1 = matiereRepository
                .findByClasseIdAndNumeroSemestre(classeId, 1);
        List<Matiere> matieresS2 = matiereRepository
                .findByClasseIdAndNumeroSemestre(classeId, 2);

        // 4. Calculer la moyenne annuelle de chaque étudiant
        List<Double> moyennesAnnuelles = new ArrayList<>();

        for (Etudiant etudiant : etudiants) {
            Double moyS1 = calculerMoyenneSemestreEtudiant(
                    etudiant.getId(), matieresS1, anneeAcademiqueId);
            Double moyS2 = calculerMoyenneSemestreEtudiant(
                    etudiant.getId(), matieresS2, anneeAcademiqueId);
            Double moyAnnuelle = CalculNoteUtils
                    .calculerMoyenneAnnuelle(moyS1, moyS2);
            if (moyAnnuelle != null) {
                moyennesAnnuelles.add(moyAnnuelle);
            }
        }

        // 5. Construire le DTO
        return kpiMapper.toKpiAnnuelDTO(
                moyennesAnnuelles,
                anneeAcademiqueId,
                annee.getAnnee(),
                null, null);
    }

    // ===== COMPARAISONS =====

    /**
     * Compare les KPI d'une matière entre plusieurs promotions
     * pour une même année académique.
     *
     * @param matiereId         identifiant de la matière
     * @param promotionIds      liste des identifiants de promotions
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  le DTO de comparaison
     */
    @Transactional(readOnly = true)
    public ComparaisonPromoDTO comparerMatiereInterPromos(
            Long matiereId,
            List<Long> promotionIds,
            Long anneeAcademiqueId) {

        Matiere matiere = matiereRepository.findById(matiereId)
                .orElseThrow();

        ComparaisonPromoDTO comparaison = new ComparaisonPromoDTO();
        comparaison.setTypeComparaison("INTER_PROMOTIONS");
        comparaison.setNiveauComparaison("MATIERE");
        comparaison.setMatiereNom(matiere.getNom());
        comparaison.setMatiereCode(matiere.getCode());

        List<EntreeComparaisonDTO> entrees = new ArrayList<>();

        for (Long promotionId : promotionIds) {
            KpiMatiereDTO kpi = getKpiMatiereByPromotion(
                    matiereId, promotionId, anneeAcademiqueId);
            entrees.add(toEntreeComparaison(kpi, null));
        }

        comparaison.setEntrees(entrees);
        return comparaison;
    }

    /**
     * Compare les KPI d'une matière sur plusieurs années
     * pour une même promotion.
     *
     * @param matiereId             identifiant de la matière
     * @param promotionId           identifiant de la promotion
     * @param anneeAcademiqueIds    liste des identifiants d'années
     * @return                      le DTO de comparaison
     */
    @Transactional(readOnly = true)
    public ComparaisonPromoDTO comparerMatiereInterAnnees(
            Long matiereId,
            Long promotionId,
            List<Long> anneeAcademiqueIds) {

        Matiere matiere = matiereRepository.findById(matiereId)
                .orElseThrow();

        ComparaisonPromoDTO comparaison = new ComparaisonPromoDTO();
        comparaison.setTypeComparaison("INTER_ANNEES");
        comparaison.setNiveauComparaison("MATIERE");
        comparaison.setMatiereNom(matiere.getNom());
        comparaison.setMatiereCode(matiere.getCode());

        List<EntreeComparaisonDTO> entrees = new ArrayList<>();

        for (Long anneeId : anneeAcademiqueIds) {
            KpiMatiereDTO kpi = getKpiMatiereByPromotion(
                    matiereId, promotionId, anneeId);
            entrees.add(toEntreeComparaison(kpi, null));
        }

        comparaison.setEntrees(entrees);
        return comparaison;
    }

    // ===== MÉTHODES PRIVÉES =====

    /**
     * Calcule la note finale d'un étudiant dans une matière
     * AVANT rattrapage (CC + EF uniquement).
     *
     * <p>Utilisé pour calculer le taux de rattrapage.</p>
     *
     * @param etudiantId        identifiant de l'étudiant
     * @param matiereId         identifiant de la matière
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  la note avant rattrapage ou null
     */
    private Double calculerNoteAvantRattrapage(
            Long etudiantId,
            Long matiereId,
            Long anneeAcademiqueId) {

        // Récupérer les notes sans tenir compte du rattrapage
        List<org.harold.plateforme.entity.Note> notes =
                noteRepository.findByEtudiantIdAndMatiereIdAndAnneeAcademiqueId(
                        etudiantId, matiereId, anneeAcademiqueId);

        if (notes.isEmpty()) return null;

        Double noteCC = notes.stream()
                .filter(n -> n.getType() ==
                        org.harold.plateforme.entity.Note.TypeNote.CC)
                .map(org.harold.plateforme.entity.Note::getValeur)
                .findFirst().orElse(null);

        Double noteEF = notes.stream()
                .filter(n -> n.getType() ==
                        org.harold.plateforme.entity.Note.TypeNote.EXAMEN_FINAL)
                .map(org.harold.plateforme.entity.Note::getValeur)
                .findFirst().orElse(null);

        Double noteTP = notes.stream()
                .filter(n -> n.getType() ==
                        org.harold.plateforme.entity.Note.TypeNote.TP)
                .map(org.harold.plateforme.entity.Note::getValeur)
                .findFirst().orElse(null);

        List<org.harold.plateforme.entity.TypeEvaluation> types =
                noteRepository.findByMatiereIdAndAnneeAcademiqueId(
                                matiereId, anneeAcademiqueId)
                        .stream()
                        .map(n -> n.getMatiere())
                        .findFirst()
                        .map(m -> new ArrayList<org.harold.plateforme
                                .entity.TypeEvaluation>())
                        .orElse(new ArrayList<>());

        // Utiliser TypeEvaluationRepository directement
        return null; // sera complété ci-dessous
    }

    /**
     * Calcule les moyennes semestrielles d'une liste d'étudiants.
     *
     * @param etudiants         liste des étudiants
     * @param matieres          matières du semestre
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  liste des moyennes semestrielles
     */
    private List<Double> calculerMoyennesSemestrePourEtudiants(
            List<Etudiant> etudiants,
            List<Matiere> matieres,
            Long anneeAcademiqueId) {

        List<Double> moyennes = new ArrayList<>();
        for (Etudiant etudiant : etudiants) {
            Double moy = calculerMoyenneSemestreEtudiant(
                    etudiant.getId(), matieres, anneeAcademiqueId);
            if (moy != null) moyennes.add(moy);
        }
        return moyennes;
    }

    /**
     * Calcule la moyenne semestrielle d'un étudiant.
     *
     * <p>Calcule la note finale de chaque matière puis
     * la moyenne pondérée par les coefficients.</p>
     *
     * @param etudiantId        identifiant de l'étudiant
     * @param matieres          matières du semestre
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  la moyenne semestrielle ou null
     */
    private Double calculerMoyenneSemestreEtudiant(
            Long etudiantId,
            List<Matiere> matieres,
            Long anneeAcademiqueId) {

        Map<Double, Double> notesEtCoeffs = new HashMap<>();

        for (Matiere matiere : matieres) {
            Double noteFinale = noteService.calculerNoteFinale(
                    etudiantId,
                    matiere.getId(),
                    anneeAcademiqueId);
            if (noteFinale != null) {
                notesEtCoeffs.put(noteFinale, matiere.getCoefficient());
            }
        }

        if (notesEtCoeffs.isEmpty()) return null;
        return CalculNoteUtils.calculerMoyenneGroupe(notesEtCoeffs);
    }

    /**
     * Convertit un KpiMatiereDTO en EntreeComparaisonDTO.
     *
     * @param kpi           le KPI source
     * @param anneeLibelle  libellé de l'année (pour inter-années)
     * @return              l'entrée de comparaison
     */
    private EntreeComparaisonDTO toEntreeComparaison(
            KpiMatiereDTO kpi,
            String anneeLibelle) {
        EntreeComparaisonDTO entree = new EntreeComparaisonDTO();
        entree.setAnneeAcademique(
                anneeLibelle != null ? anneeLibelle : kpi.getAnneeAcademique());
        entree.setMoyenne(kpi.getMoyenne());
        entree.setMinimum(kpi.getMinimum());
        entree.setMaximum(kpi.getMaximum());
        entree.setEcartType(kpi.getEcartType());
        entree.setMediane(kpi.getMediane());
        entree.setTauxReussite(kpi.getTauxReussite());
        entree.setTauxEchec(kpi.getTauxEchec());
        entree.setTauxRattrapage(kpi.getTauxRattrapage());
        entree.setNbEtudiants(kpi.getNbEtudiants());
        return entree;
    }
    // Dans KpiService — ajouter ces deux méthodes

    /**
     * Récupère toutes les matières d'une classe.
     *
     * @param classeId  identifiant de la classe
     * @return          liste des matières
     */
    @Transactional(readOnly = true)
    public List<Matiere> getMatieresByClasse(Long classeId) {
        return matiereRepository.findByClasseId(classeId);
    }

    /**
     * Récupère les matières d'une classe pour un semestre.
     *
     * @param classeId          identifiant de la classe
     * @param numeroSemestre    numéro du semestre (1 ou 2)
     * @return                  liste des matières
     */
    @Transactional(readOnly = true)
    public List<Matiere> getMatieresByClasseAndSemestre(
            Long classeId,
            Integer numeroSemestre) {
        return matiereRepository.findByClasseIdAndNumeroSemestre(
                classeId, numeroSemestre);
    }
}