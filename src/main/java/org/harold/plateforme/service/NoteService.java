package org.harold.plateforme.service;

import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.note.NoteCreateDTO;
import org.harold.plateforme.dto.note.NoteDTO;
import org.harold.plateforme.dto.note.NoteUpdateDTO;
import org.harold.plateforme.entity.AuditLog;
import org.harold.plateforme.entity.Note;
import org.harold.plateforme.entity.TypeEvaluation;
import org.harold.plateforme.entity.Utilisateur;
import org.harold.plateforme.exception.AccessDeniedException;
import org.harold.plateforme.exception.DuplicateResourceException;
import org.harold.plateforme.exception.ResourceNotFoundException;
import org.harold.plateforme.exception.ValidationException;
import org.harold.plateforme.mapper.NoteMapper;
import org.harold.plateforme.repository.EnseignantMatiereGroupeRepository;
import org.harold.plateforme.repository.NoteRepository;
import org.harold.plateforme.repository.TypeEvaluationRepository;
import org.harold.plateforme.repository.UtilisateurRepository;
import org.harold.plateforme.util.CalculNoteUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service de gestion des notes.
 *
 * <p>Gère la saisie, modification et consultation des notes.
 * Vérifie les droits de l'enseignant avant chaque opération
 * d'écriture. Calcule les notes finales via CalculNoteUtils
 * en tenant compte des coefficients et du rattrapage.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final TypeEvaluationRepository typeEvaluationRepository;
    private final EnseignantMatiereGroupeRepository enseignantMatiereGroupeRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final NoteMapper noteMapper;
    private final EtudiantService etudiantService;
    private final MatiereService matiereService;
    private final GroupeClasseService groupeClasseService;
    private final AnneeAcademiqueService anneeAcademiqueService;
    private final AuditService auditService;

    // ===== SAISIE =====

    /**
     * Saisit une note pour un étudiant.
     *
     * <p>Vérifie que l'enseignant a le droit de saisir
     * sur cette matière et ce groupe. Vérifie que la note
     * n'existe pas déjà. Si c'est un rattrapage vérifie
     * que la note EF existe.</p>
     *
     * @param dto               les données de la note
     * @param enseignantId      l'enseignant qui saisit la note
     * @param ipAddress         adresse IP de l'enseignant
     * @return                  le DTO de la note créée
     * @throws AccessDeniedException        si l'enseignant n'a pas
     *                                      le droit de saisir
     * @throws DuplicateResourceException   si la note existe déjà
     * @throws ValidationException          si rattrapage sans EF
     */
    @Transactional
    public NoteDTO saisir(
            NoteCreateDTO dto,
            Long enseignantId,
            String ipAddress) {

        // 1. Vérifier droits de l'enseignant
        verifierDroitsEnseignant(
                enseignantId,
                dto.getMatiereId(),
                dto.getGroupeClasseId());

        // 2. Valider le type de note
        Note.TypeNote typeNote;
        try {
            typeNote = Note.TypeNote.valueOf(dto.getType());
        } catch (IllegalArgumentException e) {
            throw new ValidationException(
                    "type",
                    "Type invalide. Valeurs : CC, EXAMEN_FINAL, TP, RATTRAPAGE");
        }

        // 3. Vérifier que la note n'existe pas déjà
        if (noteRepository.existsByEtudiantIdAndMatiereIdAndAnneeAcademiqueIdAndType(
                dto.getEtudiantId(),
                dto.getMatiereId(),
                dto.getAnneeAcademiqueId(),
                typeNote)) {
            throw new DuplicateResourceException(
                    "Note", "type", dto.getType());
        }

        // 4. Si rattrapage → vérifier que l'EF existe
        if (typeNote == Note.TypeNote.RATTRAPAGE) {
            if (!noteRepository
                    .existsByEtudiantIdAndMatiereIdAndAnneeAcademiqueIdAndType(
                            dto.getEtudiantId(),
                            dto.getMatiereId(),
                            dto.getAnneeAcademiqueId(),
                            Note.TypeNote.EXAMEN_FINAL)) {
                throw new ValidationException(
                        "Impossible de saisir un rattrapage sans " +
                                "note d'examen final");
            }
        }

        // 5. Récupérer l'enseignant
        Utilisateur enseignant = utilisateurRepository
                .findById(enseignantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utilisateur", "id", enseignantId));

        // 6. Créer la note
        Note note = new Note();
        note.setEtudiant(etudiantService.getEntityById(
                dto.getEtudiantId()));
        note.setMatiere(matiereService.getEntityById(
                dto.getMatiereId()));
        note.setAnneeAcademique(anneeAcademiqueService
                .getEntityById(dto.getAnneeAcademiqueId()));
        note.setGroupeClasse(groupeClasseService
                .getEntityById(dto.getGroupeClasseId()));
        note.setSaisiPar(enseignant);
        note.setType(typeNote);
        note.setValeur(dto.getValeur());
        note.setEstRattrapage(dto.isEstRattrapage());

        // 7. Sauvegarder
        Note sauvegarde = noteRepository.save(note);

        // 8. Audit
        NoteDTO noteDTO = noteMapper.toDTO(sauvegarde);
        auditService.enregistrer(
                enseignantId,
                "Note",
                sauvegarde.getId(),
                AuditLog.TypeAction.CREATE,
                null,
                noteDTO,
                ipAddress);

        return noteDTO;
    }

    // ===== MODIFICATION =====

    /**
     * Modifie une note existante.
     *
     * <p>Vérifie les droits de l'enseignant.
     * Seules la valeur et le statut rattrapage sont modifiables.</p>
     *
     * @param id                identifiant de la note
     * @param dto               les nouvelles données
     * @param enseignantId      l'enseignant qui modifie
     * @param ipAddress         adresse IP de l'enseignant
     * @return                  le DTO de la note modifiée
     * @throws ResourceNotFoundException    si la note n'existe pas
     * @throws AccessDeniedException        si droits insuffisants
     */
    @Transactional
    public NoteDTO modifier(
            Long id,
            NoteUpdateDTO dto,
            Long enseignantId,
            String ipAddress) {

        // 1. Récupérer la note existante
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Note", "id", id));

        // 2. Vérifier droits de l'enseignant
        verifierDroitsEnseignant(
                enseignantId,
                note.getMatiere().getId(),
                note.getGroupeClasse().getId());

        // 3. Sauvegarder état avant pour l'audit
        NoteDTO avant = noteMapper.toDTO(note);

        // 4. Mettre à jour via mapper
        noteMapper.updateEntity(dto, note);
        Note sauvegarde = noteRepository.save(note);

        // 5. Audit
        NoteDTO apres = noteMapper.toDTO(sauvegarde);
        auditService.enregistrer(
                enseignantId,
                "Note",
                sauvegarde.getId(),
                AuditLog.TypeAction.UPDATE,
                avant,
                apres,
                ipAddress);

        return apres;
    }

    // ===== CALCUL NOTE FINALE =====

    /**
     * Calcule la note finale d'un étudiant dans une matière.
     *
     * <p>Récupère les notes CC, EF/RATTRAPAGE et TP,
     * récupère les coefficients depuis TypeEvaluation
     * et calcule via CalculNoteUtils.
     * Si un rattrapage existe il remplace l'EF.</p>
     *
     * @param etudiantId        identifiant de l'étudiant
     * @param matiereId         identifiant de la matière
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  la note finale calculée ou null
     *                          si notes insuffisantes
     */
    @Transactional(readOnly = true)
    public Double calculerNoteFinale(
            Long etudiantId,
            Long matiereId,
            Long anneeAcademiqueId) {

        // 1. Récupérer toutes les notes de l'étudiant
        //    pour cette matière et cette année
        List<Note> notes = noteRepository
                .findByEtudiantIdAndMatiereIdAndAnneeAcademiqueId(
                        etudiantId, matiereId, anneeAcademiqueId);

        if (notes.isEmpty()) return null;

        // 2. Extraire chaque type de note
        Double noteCC = getNoteParType(notes, Note.TypeNote.CC);
        Double noteEF = getNoteParType(notes, Note.TypeNote.EXAMEN_FINAL);
        Double noteTP = getNoteParType(notes, Note.TypeNote.TP);
        Double noteRAT = getNoteParType(notes, Note.TypeNote.RATTRAPAGE);

        // 3. Récupérer les coefficients depuis TypeEvaluation
        List<TypeEvaluation> types = typeEvaluationRepository
                .findByMatiereId(matiereId);

        Double coeffCC = getCoeffParType(
                types, TypeEvaluation.TypeEval.CC);
        Double coeffEF = getCoeffParType(
                types, TypeEvaluation.TypeEval.EXAMEN_FINAL);
        Double coeffTP = getCoeffParType(
                types, TypeEvaluation.TypeEval.TP);

        // 4. Calculer selon si rattrapage ou non
        if (noteRAT != null) {
            return CalculNoteUtils.calculerNoteMatiereAvecRattrapage(
                    noteCC, coeffCC,
                    noteRAT, coeffEF,
                    noteTP, coeffTP);
        }

        return CalculNoteUtils.calculerNoteMatiere(
                noteCC, coeffCC,
                noteEF, coeffEF,
                noteTP, coeffTP);
    }

    // ===== CONSULTATION =====

    /**
     * Récupère toutes les notes d'un étudiant
     * pour une année académique.
     *
     * @param etudiantId        identifiant de l'étudiant
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  liste des notes
     */
    @Transactional(readOnly = true)
    public List<NoteDTO> getByEtudiantAndAnnee(
            Long etudiantId,
            Long anneeAcademiqueId) {
        return noteRepository
                .findByEtudiantIdAndAnneeAcademiqueId(
                        etudiantId, anneeAcademiqueId)
                .stream()
                .map(noteMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère toutes les notes d'une matière
     * pour une année académique.
     *
     * @param matiereId         identifiant de la matière
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  liste des notes
     */
    @Transactional(readOnly = true)
    public List<NoteDTO> getByMatiereAndAnnee(
            Long matiereId,
            Long anneeAcademiqueId) {
        return noteRepository
                .findByMatiereIdAndAnneeAcademiqueId(
                        matiereId, anneeAcademiqueId)
                .stream()
                .map(noteMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère toutes les notes d'un groupe
     * pour une matière et une année académique.
     *
     * @param groupeClasseId    identifiant du groupe
     * @param matiereId         identifiant de la matière
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  liste des notes
     */
    @Transactional(readOnly = true)
    public List<NoteDTO> getByGroupeAndMatiereAndAnnee(
            Long groupeClasseId,
            Long matiereId,
            Long anneeAcademiqueId) {
        return noteRepository
                .findByGroupeClasseIdAndMatiereIdAndAnneeAcademiqueId(
                        groupeClasseId, matiereId, anneeAcademiqueId)
                .stream()
                .map(noteMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ===== MÉTHODES PRIVÉES =====

    /**
     * Vérifie que l'enseignant a le droit de saisir
     * des notes pour cette matière et ce groupe.
     *
     * @param enseignantId      identifiant de l'enseignant
     * @param matiereId         identifiant de la matière
     * @param groupeClasseId    identifiant du groupe
     * @throws AccessDeniedException    si l'enseignant n'est pas
     *                                  assigné à cette matière/groupe
     */
    private void verifierDroitsEnseignant(
            Long enseignantId,
            Long matiereId,
            Long groupeClasseId) {
        if (!enseignantMatiereGroupeRepository
                .existsByUtilisateurIdAndMatiereIdAndGroupeClasseId(
                        enseignantId, matiereId, groupeClasseId)) {
            throw new AccessDeniedException(
                    "Vous n'êtes pas assigné à cette matière " +
                            "et ce groupe");
        }
    }

    /**
     * Extrait la valeur d'une note selon son type
     * depuis une liste de notes.
     *
     * @param notes     liste des notes
     * @param type      le type de note recherché
     * @return          la valeur de la note ou null si absente
     */
    private Double getNoteParType(
            List<Note> notes,
            Note.TypeNote type) {
        return notes.stream()
                .filter(n -> n.getType() == type)
                .map(Note::getValeur)
                .findFirst()
                .orElse(null);
    }

    /**
     * Extrait le coefficient d'un type d'évaluation
     * depuis une liste de types d'évaluation.
     *
     * @param types     liste des types d'évaluation
     * @param type      le type recherché
     * @return          le coefficient ou null si absent
     */
    private Double getCoeffParType(
            List<TypeEvaluation> types,
            TypeEvaluation.TypeEval type) {
        return types.stream()
                .filter(t -> t.getType() == type)
                .map(TypeEvaluation::getCoefficient)
                .findFirst()
                .orElse(null);
    }
    // Ajouter dans les dépendances de KpiService :


    /**
     * Calcule la note finale d'un étudiant dans une matière
     * AVANT rattrapage (CC + EF uniquement).
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
                typeEvaluationRepository.findByMatiereId(matiereId);

        Double coeffCC = types.stream()
                .filter(t -> t.getType() ==
                        org.harold.plateforme.entity.TypeEvaluation
                                .TypeEval.CC)
                .map(org.harold.plateforme.entity.TypeEvaluation::getCoefficient)
                .findFirst().orElse(null);

        Double coeffEF = types.stream()
                .filter(t -> t.getType() ==
                        org.harold.plateforme.entity.TypeEvaluation
                                .TypeEval.EXAMEN_FINAL)
                .map(org.harold.plateforme.entity.TypeEvaluation::getCoefficient)
                .findFirst().orElse(null);

        Double coeffTP = types.stream()
                .filter(t -> t.getType() ==
                        org.harold.plateforme.entity.TypeEvaluation
                                .TypeEval.TP)
                .map(org.harold.plateforme.entity.TypeEvaluation::getCoefficient)
                .findFirst().orElse(null);

        return CalculNoteUtils.calculerNoteMatiere(
                noteCC, coeffCC,
                noteEF, coeffEF,
                noteTP, coeffTP);
    }
}