package org.harold.plateforme.service;

import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.matiere.GroupeMatieresCreateDTO;
import org.harold.plateforme.dto.matiere.GroupeMatieresDTO;
import org.harold.plateforme.dto.matiere.GroupeMatieresUpdateDTO;
import org.harold.plateforme.dto.matiere.MatiereCreateDTO;
import org.harold.plateforme.dto.matiere.MatiereDTO;
import org.harold.plateforme.dto.matiere.MatiereUpdateDTO;
import org.harold.plateforme.dto.matiere.TypeEvaluationCreateDTO;
import org.harold.plateforme.dto.matiere.TypeEvaluationDTO;
import org.harold.plateforme.dto.matiere.TypeEvaluationUpdateDTO;
import org.harold.plateforme.entity.AuditLog;
import org.harold.plateforme.entity.GroupeMatieres;
import org.harold.plateforme.entity.Matiere;
import org.harold.plateforme.entity.Semestre;
import org.harold.plateforme.entity.TypeEvaluation;
import org.harold.plateforme.exception.DuplicateResourceException;
import org.harold.plateforme.exception.ResourceNotFoundException;
import org.harold.plateforme.exception.ValidationException;
import org.harold.plateforme.mapper.GroupeMatieresMapper;
import org.harold.plateforme.mapper.MatiereMapper;
import org.harold.plateforme.mapper.TypeEvaluationMapper;
import org.harold.plateforme.repository.GroupeMatieresRepository;
import org.harold.plateforme.repository.MatiereRepository;
import org.harold.plateforme.repository.SemestreRepository;
import org.harold.plateforme.repository.TypeEvaluationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de gestion des matières.
 *
 * <p>Gère la création et modification des groupes de matières,
 * des matières et des types d'évaluation.
 * Les coefficients sont fixes après création de la classe.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class MatiereService {

    private final MatiereRepository matiereRepository;
    private final GroupeMatieresRepository groupeMatieresRepository;
    private final TypeEvaluationRepository typeEvaluationRepository;
    private final SemestreRepository semestreRepository;
    private final MatiereMapper matiereMapper;
    private final GroupeMatieresMapper groupeMatieresMapper;
    private final TypeEvaluationMapper typeEvaluationMapper;
    private final AuditService auditService;

    // ===== GROUPE DE MATIERES =====

    /**
     * Crée un nouveau groupe de matières.
     *
     * <p>Vérifie l'unicité du nom dans le semestre
     * et enregistre l'action dans l'audit.</p>
     *
     * @param dto               les données de création
     * @param utilisateurId     l'admin qui crée le groupe
     * @param ipAddress         adresse IP de l'admin
     * @return                  le DTO du groupe créé
     * @throws ResourceNotFoundException    si le semestre n'existe pas
     * @throws DuplicateResourceException   si le nom existe déjà
     *                                      dans ce semestre
     */
    @Transactional
    public GroupeMatieresDTO creerGroupeMatieres(
            GroupeMatieresCreateDTO dto,
            Long utilisateurId,
            String ipAddress) {

        // 1. Récupérer le semestre
        Semestre semestre = semestreRepository
                .findById(dto.getSemestreId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Semestre", "id", dto.getSemestreId()));

        // 2. Vérifier unicité du nom dans le semestre
        if (groupeMatieresRepository.existsBySemestreIdAndNom(
                dto.getSemestreId(), dto.getNom())) {
            throw new DuplicateResourceException(
                    "GroupeMatieres", "nom", dto.getNom());
        }

        // 3. Créer l'entité
        GroupeMatieres groupe = new GroupeMatieres();
        groupe.setNom(dto.getNom());
        groupe.setCoefficient(dto.getCoefficient());
        groupe.setSemestre(semestre);

        // 4. Sauvegarder
        GroupeMatieres sauvegarde =
                groupeMatieresRepository.save(groupe);

        // 5. Audit
        GroupeMatieresDTO groupeDTO =
                groupeMatieresMapper.toDTO(sauvegarde);
        auditService.enregistrer(
                utilisateurId,
                "GroupeMatieres",
                sauvegarde.getId(),
                AuditLog.TypeAction.CREATE,
                null,
                groupeDTO,
                ipAddress);

        return groupeDTO;
    }

    /**
     * Modifie un groupe de matières existant.
     *
     * <p>Le semestre n'est pas modifiable après création
     * car cela impacterait les calculs existants.</p>
     *
     * @param id                identifiant du groupe
     * @param dto               les nouvelles données
     * @param utilisateurId     l'admin qui effectue la modification
     * @param ipAddress         adresse IP de l'admin
     * @return                  le DTO du groupe modifié
     * @throws ResourceNotFoundException    si le groupe n'existe pas
     * @throws DuplicateResourceException   si le nouveau nom existe déjà
     */
    @Transactional
    public GroupeMatieresDTO modifierGroupeMatieres(
            Long id,
            GroupeMatieresUpdateDTO dto,
            Long utilisateurId,
            String ipAddress) {

        // 1. Récupérer le groupe existant
        GroupeMatieres groupe = groupeMatieresRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "GroupeMatieres", "id", id));

        // 2. Vérifier unicité du nom si changé
        if (!groupe.getNom().equals(dto.getNom()) &&
                groupeMatieresRepository.existsBySemestreIdAndNom(
                        groupe.getSemestre().getId(), dto.getNom())) {
            throw new DuplicateResourceException(
                    "GroupeMatieres", "nom", dto.getNom());
        }

        // 3. Sauvegarder état avant pour l'audit
        GroupeMatieresDTO avant = groupeMatieresMapper.toDTO(groupe);

        // 4. Mettre à jour
        groupe.setNom(dto.getNom());
        groupe.setCoefficient(dto.getCoefficient());
        GroupeMatieres sauvegarde =
                groupeMatieresRepository.save(groupe);

        // 5. Audit
        GroupeMatieresDTO apres =
                groupeMatieresMapper.toDTO(sauvegarde);
        auditService.enregistrer(
                utilisateurId,
                "GroupeMatieres",
                sauvegarde.getId(),
                AuditLog.TypeAction.UPDATE,
                avant,
                apres,
                ipAddress);

        return apres;
    }

    // ===== MATIERE =====

    /**
     * Crée une nouvelle matière.
     *
     * <p>Vérifie l'unicité du code et enregistre
     * l'action dans l'audit.</p>
     *
     * @param dto               les données de création
     * @param utilisateurId     l'admin qui crée la matière
     * @param ipAddress         adresse IP de l'admin
     * @return                  le DTO de la matière créée
     * @throws ResourceNotFoundException    si le groupe n'existe pas
     * @throws DuplicateResourceException   si le code existe déjà
     */
    @Transactional
    public MatiereDTO creerMatiere(
            MatiereCreateDTO dto,
            Long utilisateurId,
            String ipAddress) {

        // 1. Vérifier unicité du code
        if (matiereRepository.existsByCode(dto.getCode())) {
            throw new DuplicateResourceException(
                    "Matiere", "code", dto.getCode());
        }

        // 2. Récupérer le groupe de matières
        GroupeMatieres groupe = groupeMatieresRepository
                .findById(dto.getGroupeMatieresId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "GroupeMatieres", "id",
                        dto.getGroupeMatieresId()));

        // 3. Créer l'entité via mapper
        Matiere matiere = matiereMapper.toEntity(dto);
        matiere.setGroupeMatieres(groupe);

        // 4. Sauvegarder
        Matiere sauvegarde = matiereRepository.save(matiere);

        // 5. Audit
        MatiereDTO matiereDTO = matiereMapper.toDTO(sauvegarde);
        auditService.enregistrer(
                utilisateurId,
                "Matiere",
                sauvegarde.getId(),
                AuditLog.TypeAction.CREATE,
                null,
                matiereDTO,
                ipAddress);

        return matiereDTO;
    }

    /**
     * Modifie une matière existante.
     *
     * <p>Le code et le groupe de matières ne sont pas
     * modifiables après création.</p>
     *
     * @param id                identifiant de la matière
     * @param dto               les nouvelles données
     * @param utilisateurId     l'admin qui effectue la modification
     * @param ipAddress         adresse IP de l'admin
     * @return                  le DTO de la matière modifiée
     * @throws ResourceNotFoundException    si la matière n'existe pas
     */
    @Transactional
    public MatiereDTO modifierMatiere(
            Long id,
            MatiereUpdateDTO dto,
            Long utilisateurId,
            String ipAddress) {

        // 1. Récupérer la matière existante
        Matiere matiere = matiereRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Matiere", "id", id));

        // 2. Sauvegarder état avant pour l'audit
        MatiereDTO avant = matiereMapper.toDTO(matiere);

        // 3. Mettre à jour via mapper
        matiereMapper.updateEntity(dto, matiere);
        Matiere sauvegarde = matiereRepository.save(matiere);

        // 4. Audit
        MatiereDTO apres = matiereMapper.toDTO(sauvegarde);
        auditService.enregistrer(
                utilisateurId,
                "Matiere",
                sauvegarde.getId(),
                AuditLog.TypeAction.UPDATE,
                avant,
                apres,
                ipAddress);

        return apres;
    }

    // ===== TYPE EVALUATION =====

    /**
     * Crée un type d'évaluation pour une matière.
     *
     * <p>Vérifie que le type n'existe pas déjà pour cette matière.</p>
     *
     * @param dto               les données de création
     * @param utilisateurId     l'admin qui crée le type
     * @param ipAddress         adresse IP de l'admin
     * @return                  le DTO du type créé
     * @throws ResourceNotFoundException    si la matière n'existe pas
     * @throws ValidationException          si le type est invalide
     * @throws DuplicateResourceException   si le type existe déjà
     */
    @Transactional
    public TypeEvaluationDTO creerTypeEvaluation(
            TypeEvaluationCreateDTO dto,
            Long utilisateurId,
            String ipAddress) {

        // 1. Récupérer la matière
        Matiere matiere = matiereRepository
                .findById(dto.getMatiereId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Matiere", "id", dto.getMatiereId()));

        // 2. Valider le type
        TypeEvaluation.TypeEval typeEval;
        try {
            typeEval = TypeEvaluation.TypeEval.valueOf(dto.getType());
        } catch (IllegalArgumentException e) {
            throw new ValidationException(
                    "type",
                    "Type invalide. Valeurs acceptées : "
                            + "CC, EXAMEN_FINAL, TP, RATTRAPAGE");
        }

        // 3. Vérifier unicité du type pour cette matière
        if (typeEvaluationRepository.existsByMatiereIdAndType(
                dto.getMatiereId(), typeEval)) {
            throw new DuplicateResourceException(
                    "TypeEvaluation", "type", dto.getType());
        }

        // 4. Créer l'entité
        TypeEvaluation typeEvaluation = new TypeEvaluation();
        typeEvaluation.setMatiere(matiere);
        typeEvaluation.setType(typeEval);
        typeEvaluation.setCoefficient(dto.getCoefficient());
        typeEvaluation.setLibelle(dto.getLibelle());

        // 5. Sauvegarder
        TypeEvaluation sauvegarde =
                typeEvaluationRepository.save(typeEvaluation);

        // 6. Audit
        TypeEvaluationDTO typeDTO =
                typeEvaluationMapper.toDTO(sauvegarde);
        auditService.enregistrer(
                utilisateurId,
                "TypeEvaluation",
                sauvegarde.getId(),
                AuditLog.TypeAction.CREATE,
                null,
                typeDTO,
                ipAddress);

        return typeDTO;
    }

    /**
     * Modifie un type d'évaluation existant.
     *
     * <p>Le type (CC, EF, TP, RATTRAPAGE) et la matière
     * ne sont pas modifiables après création.</p>
     *
     * @param id                identifiant du type d'évaluation
     * @param dto               les nouvelles données
     * @param utilisateurId     l'admin qui effectue la modification
     * @param ipAddress         adresse IP de l'admin
     * @return                  le DTO du type modifié
     * @throws ResourceNotFoundException    si le type n'existe pas
     */
    @Transactional
    public TypeEvaluationDTO modifierTypeEvaluation(
            Long id,
            TypeEvaluationUpdateDTO dto,
            Long utilisateurId,
            String ipAddress) {

        // 1. Récupérer le type existant
        TypeEvaluation typeEvaluation = typeEvaluationRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "TypeEvaluation", "id", id));

        // 2. Sauvegarder état avant pour l'audit
        TypeEvaluationDTO avant =
                typeEvaluationMapper.toDTO(typeEvaluation);

        // 3. Mettre à jour coefficient et libelle uniquement
        typeEvaluation.setCoefficient(dto.getCoefficient());
        typeEvaluation.setLibelle(dto.getLibelle());
        TypeEvaluation sauvegarde =
                typeEvaluationRepository.save(typeEvaluation);

        // 4. Audit
        TypeEvaluationDTO apres =
                typeEvaluationMapper.toDTO(sauvegarde);
        auditService.enregistrer(
                utilisateurId,
                "TypeEvaluation",
                sauvegarde.getId(),
                AuditLog.TypeAction.UPDATE,
                avant,
                apres,
                ipAddress);

        return apres;
    }

    // ===== CONSULTATION =====

    /**
     * Récupère une matière par son identifiant.
     *
     * <p>Retourne l'entité pour les autres services.</p>
     *
     * @param id    identifiant de la matière
     * @return      l'entité Matiere
     * @throws ResourceNotFoundException    si la matière n'existe pas
     */
    @Transactional(readOnly = true)
    public Matiere getEntityById(Long id) {
        return matiereRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Matiere", "id", id));
    }

    /**
     * Récupère une matière par son identifiant en DTO.
     *
     * @param id    identifiant de la matière
     * @return      le DTO de la matière
     * @throws ResourceNotFoundException    si la matière n'existe pas
     */
    @Transactional(readOnly = true)
    public MatiereDTO getById(Long id) {
        return matiereMapper.toDTO(getEntityById(id));
    }

    /**
     * Récupère toutes les matières d'une classe.
     *
     * @param classeId  identifiant de la classe
     * @return          liste des matières
     */
    @Transactional(readOnly = true)
    public List<MatiereDTO> getByClasse(Long classeId) {
        return matiereRepository.findByClasseId(classeId)
                .stream()
                .map(matiereMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les matières d'une classe pour un semestre précis.
     *
     * @param classeId          identifiant de la classe
     * @param numeroSemestre    numéro du semestre (1 ou 2)
     * @return                  liste des matières du semestre
     */
    @Transactional(readOnly = true)
    public List<MatiereDTO> getByClasseAndSemestre(
            Long classeId,
            Integer numeroSemestre) {
        return matiereRepository
                .findByClasseIdAndNumeroSemestre(classeId, numeroSemestre)
                .stream()
                .map(matiereMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère toutes les matières d'un enseignant.
     *
     * @param enseignantId  identifiant de l'enseignant
     * @return              liste des matières de l'enseignant
     */
    @Transactional(readOnly = true)
    public List<MatiereDTO> getByEnseignant(Long enseignantId) {
        return matiereRepository.findByEnseignantId(enseignantId)
                .stream()
                .map(matiereMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère tous les types d'évaluation d'une matière.
     *
     * @param matiereId     identifiant de la matière
     * @return              liste des types d'évaluation
     */
    @Transactional(readOnly = true)
    public List<TypeEvaluationDTO> getTypesByMatiere(Long matiereId) {
        return typeEvaluationRepository.findByMatiereId(matiereId)
                .stream()
                .map(typeEvaluationMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère tous les groupes de matières d'une classe.
     *
     * @param classeId  identifiant de la classe
     * @return          liste des groupes de matières
     */
    @Transactional(readOnly = true)
    public List<GroupeMatieresDTO> getGroupesByClasse(Long classeId) {
        return groupeMatieresRepository.findByClasseId(classeId)
                .stream()
                .map(groupeMatieresMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère un groupe de matières par son identifiant.
     *
     * <p>Retourne l'entité pour les autres services.</p>
     *
     * @param id    identifiant du groupe
     * @return      l'entité GroupeMatieres
     * @throws ResourceNotFoundException    si le groupe n'existe pas
     */
    @Transactional(readOnly = true)
    public GroupeMatieres getGroupeEntityById(Long id) {
        return groupeMatieresRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "GroupeMatieres", "id", id));
    }

    /**
     * Récupère un type d'évaluation par son identifiant.
     *
     * <p>Retourne l'entité pour les autres services.</p>
     *
     * @param id    identifiant du type
     * @return      l'entité TypeEvaluation
     * @throws ResourceNotFoundException    si le type n'existe pas
     */
    @Transactional(readOnly = true)
    public TypeEvaluation getTypeEntityById(Long id) {
        return typeEvaluationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "TypeEvaluation", "id", id));
    }
    // Dans MatiereService — ajouter

    /**
     * Récupère les matières d'un groupe de matières.
     *
     * @param groupeMatieresId  identifiant du groupe
     * @return                  liste des matières
     */
    @Transactional(readOnly = true)
    public List<Matiere> getMatieresByGroupeId(Long groupeMatieresId) {
        return matiereRepository.findByGroupeMatieresId(groupeMatieresId);
    }

    /**
     * Récupère les matières d'une classe pour un semestre.
     *
     * @param classeId          identifiant de la classe
     * @param numeroSemestre    numéro du semestre
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