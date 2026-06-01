package org.harold.plateforme.service;

import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.etudiant.FicheEtudiantEnseignantDTO;
import org.harold.plateforme.dto.etudiant.FicheEtudiantResponsableDTO;
import org.harold.plateforme.dto.etudiant.GroupeMatieresDetailDTO;
import org.harold.plateforme.dto.etudiant.NoteEtudiantDTO;
import org.harold.plateforme.dto.etudiant.RemarqueDTO;
import org.harold.plateforme.dto.etudiant.SemestreDetailDTO;
import org.harold.plateforme.dto.kpi.KpiMatiereDTO;
import org.harold.plateforme.entity.Etudiant;
import org.harold.plateforme.entity.GroupeMatieres;
import org.harold.plateforme.entity.Inscription;
import org.harold.plateforme.entity.Matiere;
import org.harold.plateforme.entity.Note;
import org.harold.plateforme.entity.Remarque;
import org.harold.plateforme.entity.ScoreRisqueHistorique;
import org.harold.plateforme.entity.Semestre;
import org.harold.plateforme.repository.EtudiantGroupeRepository;
import org.harold.plateforme.repository.GroupeMatieresRepository;
import org.harold.plateforme.repository.InscriptionRepository;
import org.harold.plateforme.repository.NoteRepository;
import org.harold.plateforme.repository.RemarqueRepository;
import org.harold.plateforme.repository.SemestreRepository;
import org.harold.plateforme.util.CalculKpiUtils;
import org.harold.plateforme.util.CalculNoteUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service de construction des fiches étudiants.
 *
 * <p>Construit les fiches complètes des étudiants
 * pour le responsable pédagogique et pour l'enseignant.
 * Agrège les données de plusieurs services pour
 * produire une vue complète de l'étudiant.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class FicheEtudiantService {

    private final EtudiantService etudiantService;
    private final NoteService noteService;
    private final KpiService kpiService;
    private final ScoreRisqueService scoreRisqueService;
    private final MatiereService matiereService;
    private final NoteRepository noteRepository;
    private final RemarqueRepository remarqueRepository;
    private final InscriptionRepository inscriptionRepository;
    private final SemestreRepository semestreRepository;
    private final GroupeMatieresRepository groupeMatieresRepository;
    private final EtudiantGroupeRepository etudiantGroupeRepository;

    // ===== FICHE RESPONSABLE =====

    /**
     * Construit la fiche complète d'un étudiant
     * pour le responsable pédagogique.
     *
     * <p>Contient toutes les informations : notes par semestre,
     * moyennes, statuts de validation, rangs, écarts,
     * score de risque et remarques.</p>
     *
     * @param etudiantId        identifiant de l'étudiant
     * @param classeId          identifiant de la classe
     * @param promotionId       identifiant de la promotion
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  la fiche complète
     */
    @Transactional(readOnly = true)
    public FicheEtudiantResponsableDTO getFicheResponsable(
            Long etudiantId,
            Long classeId,
            Long promotionId,
            Long anneeAcademiqueId) {

        // 1. Récupérer étudiant et inscription
        Etudiant etudiant = etudiantService.getEntityById(etudiantId);
        Inscription inscription = inscriptionRepository
                .findByEtudiantIdAndActifTrue(etudiantId)
                .orElse(null);

        FicheEtudiantResponsableDTO fiche =
                new FicheEtudiantResponsableDTO();

        // 2. Informations de base
        fiche.setId(etudiant.getId());
        fiche.setNom(etudiant.getNom());
        fiche.setPrenom(etudiant.getPrenom());
        fiche.setNumeroEtudiant(etudiant.getNumeroEtudiant());
        fiche.setEmail(etudiant.getEmail());

        if (inscription != null) {
            fiche.setPromotionNom(
                    inscription.getPromotion().getNom());
            fiche.setNiveau(inscription.getNiveau());
            fiche.setAnneeAcademique(
                    inscription.getAnneeAcademique().getAnnee());
        }

        // 3. Construire les semestres
        List<Semestre> semestres = semestreRepository
                .findByClasseId(classeId);

        SemestreDetailDTO dtoS1 = null;
        SemestreDetailDTO dtoS2 = null;
        Double moyenneS1 = null;
        Double moyenneS2 = null;

        for (Semestre semestre : semestres) {
            SemestreDetailDTO dtoSemestre =
                    construireSemestreDetail(
                            etudiantId, semestre,
                            promotionId, anneeAcademiqueId);

            if (semestre.getNumero() == 1) {
                dtoS1 = dtoSemestre;
                moyenneS1 = dtoSemestre.getMoyenne();
            } else {
                dtoS2 = dtoSemestre;
                moyenneS2 = dtoSemestre.getMoyenne();
            }
        }

        fiche.setSemestreS1(dtoS1);
        fiche.setSemestreS2(dtoS2);

        // 4. Moyennes
        fiche.setMoyenneS1(moyenneS1);
        fiche.setMoyenneS2(moyenneS2);
        fiche.setMoyenneGenerale(
                CalculNoteUtils.calculerMoyenneAnnuelle(
                        moyenneS1, moyenneS2));

        // 5. Statuts de validation
        fiche.setS1Valide(dtoS1 != null ? dtoS1.getValide() : null);
        fiche.setS2Valide(dtoS2 != null ? dtoS2.getValide() : null);
        fiche.setAnneeValidee(CalculNoteUtils.estAnneeValidee(
                fiche.getS1Valide(), fiche.getS2Valide()));

        // 6. Progression inter-semestrielle
        fiche.setProgressionInterSemestrielle(
                CalculNoteUtils.calculerProgression(
                        moyenneS1, moyenneS2));

        // 7. Rangs et écarts
        calculerRangsEtEcarts(fiche, etudiantId,
                promotionId, anneeAcademiqueId, classeId);

        // 8. Score de risque
        Optional<ScoreRisqueHistorique> score =
                scoreRisqueService.getScoreActuel(
                        etudiantId, anneeAcademiqueId);
        score.ifPresent(s -> {
            fiche.setScoreRisque(s.getScoreGlobal());
            fiche.setNiveauRisque(
                    determinerNiveauRisque(s.getScoreGlobal()));
        });

        // 9. Remarques
        fiche.setRemarques(getRemarquesDTO(
                etudiantId, anneeAcademiqueId));

        return fiche;
    }

    // ===== FICHE ENSEIGNANT =====

    /**
     * Construit la fiche d'un étudiant pour un enseignant.
     *
     * <p>Limitée à la matière de l'enseignant :
     * notes, rang, KPI de la classe et remarques.</p>
     *
     * @param etudiantId        identifiant de l'étudiant
     * @param matiereId         identifiant de la matière
     * @param promotionId       identifiant de la promotion
     * @param anneeAcademiqueId identifiant de l'année académique
     * @param enseignantId      identifiant de l'enseignant
     * @return                  la fiche pour l'enseignant
     */
    @Transactional(readOnly = true)
    public FicheEtudiantEnseignantDTO getFicheEnseignant(
            Long etudiantId,
            Long matiereId,
            Long promotionId,
            Long anneeAcademiqueId,
            Long enseignantId) {

        Etudiant etudiant = etudiantService.getEntityById(etudiantId);
        Matiere matiere = matiereService.getEntityById(matiereId);

        FicheEtudiantEnseignantDTO fiche =
                new FicheEtudiantEnseignantDTO();

        // 1. Informations de base
        fiche.setId(etudiant.getId());
        fiche.setNom(etudiant.getNom());
        fiche.setPrenom(etudiant.getPrenom());
        fiche.setNumeroEtudiant(etudiant.getNumeroEtudiant());
        fiche.setMatiereId(matiereId);
        fiche.setMatiereNom(matiere.getNom());
        fiche.setMatiereCode(matiere.getCode());

        // 2. Récupérer les notes de la matière
        List<Note> notes = noteRepository
                .findByEtudiantIdAndMatiereIdAndAnneeAcademiqueId(
                        etudiantId, matiereId, anneeAcademiqueId);

        fiche.setNoteCC(getNoteValeur(notes, Note.TypeNote.CC));
        fiche.setNoteEF(getNoteValeur(
                notes, Note.TypeNote.EXAMEN_FINAL));
        fiche.setNoteTP(getNoteValeur(notes, Note.TypeNote.TP));
        fiche.setNoteRattrapage(getNoteValeur(
                notes, Note.TypeNote.RATTRAPAGE));

        // 3. Calculer note finale
        Double noteFinale = noteService.calculerNoteFinale(
                etudiantId, matiereId, anneeAcademiqueId);
        fiche.setNoteFinale(noteFinale);
        fiche.setValide(noteFinale != null ? noteFinale >= 10.0 : null);
        fiche.setEnRattrapage(noteFinale != null && noteFinale < 10.0);

        // 4. KPI de la classe pour cette matière
        KpiMatiereDTO kpi = kpiService.getKpiMatiereByPromotion(
                matiereId, promotionId, anneeAcademiqueId);
        fiche.setMoyenneClasse(kpi.getMoyenne());
        fiche.setMinClasse(kpi.getMinimum());
        fiche.setMaxClasse(kpi.getMaximum());
        fiche.setEcartTypeClasse(kpi.getEcartType());
        fiche.setMedianeClasse(kpi.getMediane());

        // 5. Écart par rapport à la moyenne classe
        if (noteFinale != null && kpi.getMoyenne() != null) {
            fiche.setEcartMoyenneClasse(
                    CalculKpiUtils.arrondir(
                            noteFinale - kpi.getMoyenne()));
        }

        // 6. Rangs dans la classe et le groupe
        calculerRangsMatiere(fiche, etudiantId,
                matiereId, promotionId, anneeAcademiqueId);

        // 7. Remarques de l'enseignant uniquement
        List<RemarqueDTO> remarques = remarqueRepository
                .findByEtudiantIdAndAnneeAcademiqueId(
                        etudiantId, anneeAcademiqueId)
                .stream()
                .filter(r -> r.getEnseignant().getId()
                        .equals(enseignantId))
                .map(this::toRemarqueDTO)
                .collect(Collectors.toList());
        fiche.setRemarques(remarques);

        return fiche;
    }

    // ===== MÉTHODES PRIVÉES =====

    /**
     * Construit le détail d'un semestre pour un étudiant.
     *
     * @param etudiantId        identifiant de l'étudiant
     * @param semestre          le semestre
     * @param promotionId       identifiant de la promotion
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  le détail du semestre
     */
    private SemestreDetailDTO construireSemestreDetail(
            Long etudiantId,
            Semestre semestre,
            Long promotionId,
            Long anneeAcademiqueId) {

        SemestreDetailDTO dto = new SemestreDetailDTO();
        dto.setNumero(semestre.getNumero());
        dto.setCoefficient(semestre.getCoefficient());

        // Groupes de matières du semestre
        List<GroupeMatieres> groupes = groupeMatieresRepository
                .findBySemestreId(semestre.getId());

        List<GroupeMatieresDetailDTO> groupesDTOs =
                new ArrayList<>();
        Map<Double, Double> moyennesGroupesEtCoeffs =
                new HashMap<>();
        List<Double> moyennesGroupes = new ArrayList<>();

        for (GroupeMatieres groupe : groupes) {
            GroupeMatieresDetailDTO groupeDTO =
                    construireGroupeDetail(
                            etudiantId, groupe,
                            promotionId, anneeAcademiqueId);
            groupesDTOs.add(groupeDTO);

            if (groupeDTO.getMoyenne() != null) {
                moyennesGroupesEtCoeffs.put(
                        groupeDTO.getMoyenne(),
                        groupe.getCoefficient());
                moyennesGroupes.add(groupeDTO.getMoyenne());
            }
        }

        dto.setGroupesMatieres(groupesDTOs);

        // Calculer moyenne et statut semestre
        Double moyenneSemestre = CalculNoteUtils
                .calculerMoyenneSemestre(moyennesGroupesEtCoeffs);
        dto.setMoyenne(moyenneSemestre);
        dto.setValide(CalculNoteUtils
                .estSemestreValide(moyennesGroupes));

        return dto;
    }

    /**
     * Construit le détail d'un groupe de matières
     * pour un étudiant.
     *
     * @param etudiantId        identifiant de l'étudiant
     * @param groupe            le groupe de matières
     * @param promotionId       identifiant de la promotion
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  le détail du groupe
     */
    private GroupeMatieresDetailDTO construireGroupeDetail(
            Long etudiantId,
            GroupeMatieres groupe,
            Long promotionId,
            Long anneeAcademiqueId) {

        GroupeMatieresDetailDTO dto = new GroupeMatieresDetailDTO();
        dto.setId(groupe.getId());
        dto.setNom(groupe.getNom());
        dto.setCoefficient(groupe.getCoefficient());

        // Matières du groupe
        List<Matiere> matieres = matiereService
                .getEntityById(groupe.getId()) != null
                ? new ArrayList<>()
                : new ArrayList<>();

        // Récupérer les matières du groupe directement
        List<Matiere> matieresGroupe = matiereService
                .getMatieresByGroupeId(groupe.getId());

        List<NoteEtudiantDTO> notesDTOs = new ArrayList<>();
        Map<Double, Double> notesEtCoeffs = new HashMap<>();

        for (Matiere matiere : matieresGroupe) {
            NoteEtudiantDTO noteDTO = construireNoteEtudiant(
                    etudiantId, matiere,
                    promotionId, anneeAcademiqueId);
            notesDTOs.add(noteDTO);

            if (noteDTO.getNoteFinale() != null) {
                notesEtCoeffs.put(
                        noteDTO.getNoteFinale(),
                        matiere.getCoefficient());
            }
        }

        dto.setMatieres(notesDTOs);

        // Calculer moyenne et statut groupe
        Double moyenneGroupe = CalculNoteUtils
                .calculerMoyenneGroupe(notesEtCoeffs);
        dto.setMoyenne(moyenneGroupe);
        dto.setValide(CalculNoteUtils.estGroupeValide(moyenneGroupe));

        return dto;
    }

    /**
     * Construit le détail des notes d'un étudiant
     * pour une matière.
     *
     * @param etudiantId        identifiant de l'étudiant
     * @param matiere           la matière
     * @param promotionId       identifiant de la promotion
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  le détail des notes
     */
    private NoteEtudiantDTO construireNoteEtudiant(
            Long etudiantId,
            Matiere matiere,
            Long promotionId,
            Long anneeAcademiqueId) {

        NoteEtudiantDTO dto = new NoteEtudiantDTO();
        dto.setMatiereId(matiere.getId());
        dto.setMatiereNom(matiere.getNom());
        dto.setMatiereCode(matiere.getCode());
        dto.setCoefficient(matiere.getCoefficient());

        // Notes de l'étudiant
        List<Note> notes = noteRepository
                .findByEtudiantIdAndMatiereIdAndAnneeAcademiqueId(
                        etudiantId, matiere.getId(), anneeAcademiqueId);

        dto.setNoteCC(getNoteValeur(notes, Note.TypeNote.CC));
        dto.setNoteEF(getNoteValeur(notes, Note.TypeNote.EXAMEN_FINAL));
        dto.setNoteTP(getNoteValeur(notes, Note.TypeNote.TP));
        dto.setNoteRattrapage(getNoteValeur(
                notes, Note.TypeNote.RATTRAPAGE));

        // Note finale
        Double noteFinale = noteService.calculerNoteFinale(
                etudiantId, matiere.getId(), anneeAcademiqueId);
        dto.setNoteFinale(noteFinale);
        dto.setValide(noteFinale != null ? noteFinale >= 10.0 : null);
        dto.setEnRattrapage(noteFinale != null && noteFinale < 10.0);

        // KPI classe pour cette matière
        KpiMatiereDTO kpi = kpiService.getKpiMatiereByPromotion(
                matiere.getId(), promotionId, anneeAcademiqueId);
        dto.setMoyenneClasse(kpi.getMoyenne());
        dto.setMinClasse(kpi.getMinimum());
        dto.setMaxClasse(kpi.getMaximum());
        dto.setEcartTypeClasse(kpi.getEcartType());
        dto.setMedianeClasse(kpi.getMediane());

        // Écart par rapport à la moyenne classe
        if (noteFinale != null && kpi.getMoyenne() != null) {
            dto.setEcartMoyenneClasse(CalculKpiUtils.arrondir(
                    noteFinale - kpi.getMoyenne()));
        }

        return dto;
    }

    /**
     * Calcule les rangs et écarts pour la fiche responsable.
     *
     * @param fiche             la fiche à compléter
     * @param etudiantId        identifiant de l'étudiant
     * @param promotionId       identifiant de la promotion
     * @param anneeAcademiqueId identifiant de l'année académique
     * @param classeId          identifiant de la classe
     */
    private void calculerRangsEtEcarts(
            FicheEtudiantResponsableDTO fiche,
            Long etudiantId,
            Long promotionId,
            Long anneeAcademiqueId,
            Long classeId) {

        if (fiche.getMoyenneGenerale() == null) return;

        // Récupérer toutes les moyennes de la promotion
        List<Etudiant> etudiants = etudiantGroupeRepository
                .findByMatiereIdAndAnneeAcademiqueId(
                        classeId, anneeAcademiqueId)
                .stream()
                .map(eg -> eg.getEtudiant())
                .distinct()
                .collect(Collectors.toList());

        List<Double> toutesLesMoyennes = new ArrayList<>();
        for (Etudiant e : etudiants) {
            Double moy = calculerMoyenneAnnuelleEtudiant(
                    e.getId(), classeId, anneeAcademiqueId);
            if (moy != null) toutesLesMoyennes.add(moy);
        }

        // Rang dans la classe
        long rangClasse = toutesLesMoyennes.stream()
                .filter(m -> m > fiche.getMoyenneGenerale())
                .count() + 1;
        fiche.setRangClasse((int) rangClasse);

        // Écart par rapport à la moyenne classe
        Double moyenneClasse =
                CalculKpiUtils.calculerMoyenne(toutesLesMoyennes);
        if (moyenneClasse != null) {
            fiche.setEcartMoyenneClasse(CalculKpiUtils.arrondir(
                    fiche.getMoyenneGenerale() - moyenneClasse));
        }
    }

    /**
     * Calcule les rangs dans la classe et le groupe
     * pour une matière donnée dans la fiche enseignant.
     *
     * @param fiche             la fiche à compléter
     * @param etudiantId        identifiant de l'étudiant
     * @param matiereId         identifiant de la matière
     * @param promotionId       identifiant de la promotion
     * @param anneeAcademiqueId identifiant de l'année académique
     */
    private void calculerRangsMatiere(
            FicheEtudiantEnseignantDTO fiche,
            Long etudiantId,
            Long matiereId,
            Long promotionId,
            Long anneeAcademiqueId) {

        if (fiche.getNoteFinale() == null) return;

        // Notes de tous les étudiants de la promo pour cette matière
        List<Note> notesPromo = noteRepository
                .findByMatiereIdAndAnneeAcademiqueId(
                        matiereId, anneeAcademiqueId);

        // Calculer notes finales de tous les étudiants
        Map<Long, Double> notesFinalesParEtudiant = new HashMap<>();
        for (Note note : notesPromo) {
            Long eid = note.getEtudiant().getId();
            if (!notesFinalesParEtudiant.containsKey(eid)) {
                Double noteFinale = noteService.calculerNoteFinale(
                        eid, matiereId, anneeAcademiqueId);
                if (noteFinale != null) {
                    notesFinalesParEtudiant.put(eid, noteFinale);
                }
            }
        }

        // Rang dans la classe
        long rangClasse = notesFinalesParEtudiant.values()
                .stream()
                .filter(n -> n > fiche.getNoteFinale())
                .count() + 1;
        fiche.setRangClasse((int) rangClasse);
    }

    /**
     * Calcule la moyenne annuelle d'un étudiant pour une classe.
     *
     * @param etudiantId        identifiant de l'étudiant
     * @param classeId          identifiant de la classe
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  la moyenne annuelle ou null
     */
    private Double calculerMoyenneAnnuelleEtudiant(
            Long etudiantId,
            Long classeId,
            Long anneeAcademiqueId) {

        List<Matiere> matieresS1 = matiereService
                .getMatieresByClasseAndSemestre(classeId, 1);
        List<Matiere> matieresS2 = matiereService
                .getMatieresByClasseAndSemestre(classeId, 2);

        Double moyS1 = calculerMoyenneSemestreEtudiant(
                etudiantId, matieresS1, anneeAcademiqueId);
        Double moyS2 = calculerMoyenneSemestreEtudiant(
                etudiantId, matieresS2, anneeAcademiqueId);

        return CalculNoteUtils.calculerMoyenneAnnuelle(moyS1, moyS2);
    }

    /**
     * Calcule la moyenne semestrielle d'un étudiant.
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
            Double note = noteService.calculerNoteFinale(
                    etudiantId, matiere.getId(), anneeAcademiqueId);
            if (note != null) {
                notesEtCoeffs.put(note, matiere.getCoefficient());
            }
        }
        if (notesEtCoeffs.isEmpty()) return null;
        return CalculNoteUtils.calculerMoyenneGroupe(notesEtCoeffs);
    }

    /**
     * Récupère les remarques d'un étudiant pour une année
     * et les convertit en DTOs.
     *
     * @param etudiantId        identifiant de l'étudiant
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  liste des remarques en DTOs
     */
    private List<RemarqueDTO> getRemarquesDTO(
            Long etudiantId,
            Long anneeAcademiqueId) {
        return remarqueRepository
                .findByEtudiantIdAndAnneeAcademiqueId(
                        etudiantId, anneeAcademiqueId)
                .stream()
                .map(this::toRemarqueDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convertit une entité Remarque en RemarqueDTO.
     *
     * @param remarque  l'entité à convertir
     * @return          le DTO correspondant
     */
    private RemarqueDTO toRemarqueDTO(Remarque remarque) {
        RemarqueDTO dto = new RemarqueDTO();
        dto.setId(remarque.getId());
        dto.setContenu(remarque.getContenu());
        dto.setType(remarque.getType().name());
        dto.setCreatedAt(remarque.getCreatedAt());
        if (remarque.getEnseignant() != null) {
            dto.setEnseignantNom(remarque.getEnseignant().getNom());
            dto.setEnseignantPrenom(
                    remarque.getEnseignant().getPrenom());
        }
        if (remarque.getGroupeClasse() != null) {
            dto.setGroupeNom(remarque.getGroupeClasse().getNom());
        }
        return dto;
    }

    /**
     * Extrait la valeur d'une note par type depuis une liste.
     *
     * @param notes     liste des notes
     * @param type      le type recherché
     * @return          la valeur ou null
     */
    private Double getNoteValeur(
            List<Note> notes,
            Note.TypeNote type) {
        return notes.stream()
                .filter(n -> n.getType() == type)
                .map(Note::getValeur)
                .findFirst()
                .orElse(null);
    }

    /**
     * Détermine le libellé du niveau de risque.
     *
     * @param score     le score entre 0 et 100
     * @return          le libellé du niveau
     */
    private String determinerNiveauRisque(Double score) {
        if (score <= 30) return "FAIBLE";
        if (score <= 55) return "MODERE";
        if (score <= 75) return "ELEVE";
        return "CRITIQUE";
    }
}