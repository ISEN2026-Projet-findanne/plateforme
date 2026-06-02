package org.harold.plateforme.service;

import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.classe.ClasseCreateDTO;
import org.harold.plateforme.dto.classe.ClasseDTO;
import org.harold.plateforme.dto.classe.ClassePromotionDTO;
import org.harold.plateforme.entity.AuditLog;
import org.harold.plateforme.entity.Classe;
import org.harold.plateforme.entity.ClassePromotion;
import org.harold.plateforme.exception.DuplicateResourceException;
import org.harold.plateforme.exception.ResourceNotFoundException;
import org.harold.plateforme.mapper.ClasseMapper;
import org.harold.plateforme.repository.ClassePromotionRepository;
import org.harold.plateforme.repository.ClasseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.harold.plateforme.entity.Semestre;
import org.harold.plateforme.repository.SemestreRepository;
import java.time.LocalDate;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de gestion des classes.
 *
 * <p>Gère la création et modification des classes statiques
 * ainsi que leur attribution aux promotions pour
 * une année académique donnée.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class ClasseService {

    private final ClasseRepository classeRepository;
    private final ClassePromotionRepository classePromotionRepository;
    private final SemestreRepository semestreRepository;
    private final ClasseMapper classeMapper;
    private final PromotionService promotionService;
    private final AnneeAcademiqueService anneeAcademiqueService;
    private final AuditService auditService;

    // ===== CRÉATION CLASSE =====

    /**
     * Crée une nouvelle classe statique.
     *
     * <p>Vérifie l'unicité du nom et enregistre
     * l'action dans l'audit.</p>
     *
     * @param dto               les données de création
     * @param utilisateurId     l'admin qui crée la classe
     * @param ipAddress         adresse IP de l'admin
     * @return                  le DTO de la classe créée
     * @throws DuplicateResourceException   si le nom existe déjà
     */
    @Transactional
    public ClasseDTO creer(
            ClasseCreateDTO dto,
            Long utilisateurId,
            String ipAddress) {

        // 1. Vérifier unicité du nom
        if (classeRepository.existsByNom(dto.getNom())) {
            throw new DuplicateResourceException(
                    "Classe", "nom", dto.getNom());
        }

        // 2. Créer l'entité via mapper
        Classe classe = classeMapper.toEntity(dto);

        // 3. Sauvegarder
        Classe sauvegarde = classeRepository.save(classe);

        // 3bis. Créer automatiquement les 2 semestres (S1 et S2)
        creerSemestresParDefaut(sauvegarde, LocalDate.now());

        // 4. Audit
        ClasseDTO classeDTO = classeMapper.toDTO(sauvegarde);
        auditService.enregistrer(
                utilisateurId,
                "Classe",
                sauvegarde.getId(),
                AuditLog.TypeAction.CREATE,
                null,
                classeDTO,
                ipAddress);

        return classeDTO;
    }

    // ===== MODIFICATION CLASSE =====

    /**
     * Modifie une classe existante.
     *
     * <p>Vérifie l'unicité du nouveau nom si modifié
     * et enregistre l'action dans l'audit.</p>
     *
     * @param id                identifiant de la classe
     * @param dto               les nouvelles données
     * @param utilisateurId     l'admin qui effectue la modification
     * @param ipAddress         adresse IP de l'admin
     * @return                  le DTO de la classe modifiée
     * @throws ResourceNotFoundException    si la classe n'existe pas
     * @throws DuplicateResourceException   si le nouveau nom existe déjà
     */
    @Transactional
    public ClasseDTO modifier(
            Long id,
            ClasseCreateDTO dto,
            Long utilisateurId,
            String ipAddress) {

        // 1. Récupérer la classe existante
        Classe classe = classeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Classe", "id", id));

        // 2. Vérifier unicité du nom si changé
        if (!classe.getNom().equals(dto.getNom()) &&
                classeRepository.existsByNom(dto.getNom())) {
            throw new DuplicateResourceException(
                    "Classe", "nom", dto.getNom());
        }

        // 3. Sauvegarder état avant pour l'audit
        ClasseDTO avant = classeMapper.toDTO(classe);

        // 4. Mettre à jour via mapper
        classeMapper.updateEntity(dto, classe);
        Classe sauvegarde = classeRepository.save(classe);

        // 5. Audit
        ClasseDTO apres = classeMapper.toDTO(sauvegarde);
        auditService.enregistrer(
                utilisateurId,
                "Classe",
                sauvegarde.getId(),
                AuditLog.TypeAction.UPDATE,
                avant,
                apres,
                ipAddress);

        return apres;
    }

    // ===== ATTRIBUTION CLASSE-PROMOTION =====

    /**
     * Attribue une classe à une promotion pour une année académique.
     *
     * <p>Vérifie que l'attribution n'existe pas déjà
     * et enregistre l'action dans l'audit.</p>
     *
     * @param dto               les données d'attribution
     * @param utilisateurId     l'admin qui effectue l'attribution
     * @param ipAddress         adresse IP de l'admin
     * @return                  le DTO de l'attribution créée
     * @throws DuplicateResourceException   si l'attribution existe déjà
     * @throws ResourceNotFoundException    si classe, promotion
     *                                      ou année n'existe pas
     */
    @Transactional
    public ClassePromotionDTO attribuer(
            ClassePromotionDTO dto,
            Long utilisateurId,
            String ipAddress) {

        // 1. Vérifier que l'attribution n'existe pas déjà
        if (classePromotionRepository
                .existsByClasseIdAndPromotionIdAndAnneeAcademiqueId(
                        dto.getClasseId(),
                        dto.getPromotionId(),
                        dto.getAnneeAcademiqueId())) {
            throw new DuplicateResourceException(
                    "ClassePromotion",
                    "classeId+promotionId+anneeAcademiqueId",
                    dto.getClasseId() + "+"
                            + dto.getPromotionId() + "+"
                            + dto.getAnneeAcademiqueId());
        }

        // 2. Récupérer les entités liées
        Classe classe = getEntityById(dto.getClasseId());

        // 3. Créer l'attribution
        ClassePromotion classePromotion = new ClassePromotion();
        classePromotion.setClasse(classe);
        classePromotion.setPromotion(
                promotionService.getEntityById(dto.getPromotionId()));
        classePromotion.setAnneeAcademique(
                anneeAcademiqueService.getEntityById(
                        dto.getAnneeAcademiqueId()));

        // 4. Sauvegarder
        ClassePromotion sauvegarde =
                classePromotionRepository.save(classePromotion);

        // 5. Audit
        auditService.enregistrer(
                utilisateurId,
                "ClassePromotion",
                sauvegarde.getId(),
                AuditLog.TypeAction.CREATE,
                null,
                dto,
                ipAddress);

        return toClassePromotionDTO(sauvegarde);
    }

    // ===== CONSULTATION =====

    /**
     * Récupère une classe par son identifiant.
     *
     * <p>Retourne l'entité pour les autres services.</p>
     *
     * @param id    identifiant de la classe
     * @return      l'entité Classe
     * @throws ResourceNotFoundException    si la classe n'existe pas
     */
    @Transactional(readOnly = true)
    public Classe getEntityById(Long id) {
        return classeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Classe", "id", id));
    }

    /**
     * Récupère une classe par son identifiant en DTO.
     *
     * @param id    identifiant de la classe
     * @return      le DTO de la classe
     * @throws ResourceNotFoundException    si la classe n'existe pas
     */
    @Transactional(readOnly = true)
    public ClasseDTO getById(Long id) {
        return classeMapper.toDTO(getEntityById(id));
    }

    /**
     * Récupère toutes les classes.
     *
     * @return  liste de toutes les classes
     */
    @Transactional(readOnly = true)
    public List<ClasseDTO> getAll() {
        return classeRepository.findAll()
                .stream()
                .map(classeMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les attributions d'une promotion
     * pour une année académique.
     *
     * @param promotionId           identifiant de la promotion
     * @param anneeAcademiqueId     identifiant de l'année académique
     * @return                      liste des attributions
     */
    @Transactional(readOnly = true)
    public List<ClassePromotionDTO> getByPromotionAndAnnee(
            Long promotionId,
            Long anneeAcademiqueId) {
        return classePromotionRepository
                .findByPromotionIdAndAnneeAcademiqueId(
                        promotionId, anneeAcademiqueId)
                .stream()
                .map(this::toClassePromotionDTO)
                .collect(Collectors.toList());
    }

    // ===== MÉTHODES PRIVÉES =====
    /**
     * Crée automatiquement les deux semestres d'une classe.
     *
     * <p>Chaque classe possède toujours exactement deux semestres
     * (S1 et S2), avec un coefficient de 0.5 chacun.
     * La date de début est fixée à la date de création de la classe.
     * La date de fin sera gérée en V2.</p>
     *
     * @param classe        la classe à laquelle rattacher les semestres
     * @param dateCreation  date de création utilisée comme date de début
     */
    private void creerSemestresParDefaut(
            Classe classe,
            LocalDate dateCreation) {

        for (int numero = 1; numero <= 2; numero++) {
            Semestre semestre = new Semestre();
            semestre.setClasse(classe);
            semestre.setNumero(numero);
            semestre.setCoefficient(0.5);
            semestre.setDateDebut(dateCreation);
            // dateFin laissée à null — gérée en V2
            semestreRepository.save(semestre);
        }
    }
    /**
     * Convertit une entité ClassePromotion en ClassePromotionDTO.
     *
     * @param classePromotion   l'entité à convertir
     * @return                  le DTO correspondant
     */
    private ClassePromotionDTO toClassePromotionDTO(
            ClassePromotion classePromotion) {
        ClassePromotionDTO dto = new ClassePromotionDTO();
        dto.setClasseId(classePromotion.getClasse().getId());
        dto.setPromotionId(classePromotion.getPromotion().getId());
        dto.setAnneeAcademiqueId(
                classePromotion.getAnneeAcademique().getId());
        return dto;
    }
}