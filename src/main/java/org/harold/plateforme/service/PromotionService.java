package org.harold.plateforme.service;

import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.promotion.PromotionCreateDTO;
import org.harold.plateforme.dto.promotion.PromotionDTO;
import org.harold.plateforme.dto.promotion.PromotionUpdateDTO;
import org.harold.plateforme.entity.AuditLog;
import org.harold.plateforme.entity.Promotion;
import org.harold.plateforme.exception.DuplicateResourceException;
import org.harold.plateforme.exception.ResourceNotFoundException;
import org.harold.plateforme.exception.ValidationException;
import org.harold.plateforme.mapper.PromotionMapper;
import org.harold.plateforme.repository.InscriptionRepository;
import org.harold.plateforme.repository.PromotionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de gestion des promotions.
 *
 * <p>Gère la création, modification, suppression
 * et consultation des promotions.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final PromotionMapper promotionMapper;
    private final InscriptionRepository inscriptionRepository;
    private final AuditService auditService;

    // ===== CRÉATION =====

    /**
     * Crée une nouvelle promotion.
     *
     * <p>Vérifie l'unicité du nom et enregistre
     * l'action dans l'audit.</p>
     *
     * @param dto               les données de création
     * @param utilisateurId     l'admin qui crée la promotion
     * @param ipAddress         adresse IP de l'admin
     * @return                  le DTO de la promotion créée
     * @throws DuplicateResourceException   si le nom existe déjà
     */
    @Transactional
    public PromotionDTO creer(
            PromotionCreateDTO dto,
            Long utilisateurId,
            String ipAddress) {

        // 1. Vérifier unicité du nom
        if (promotionRepository.existsByNom(dto.getNom())) {
            throw new DuplicateResourceException(
                    "Promotion", "nom", dto.getNom());
        }

        // 2. Créer l'entité via mapper
        Promotion promotion = promotionMapper.toEntity(dto);

        // 3. Sauvegarder
        Promotion sauvegarde = promotionRepository.save(promotion);

        // 4. Calculer nbEtudiants (0 à la création)
        PromotionDTO promotionDTO = promotionMapper.toDTO(sauvegarde, 0);

        // 5. Audit
        auditService.enregistrer(
                utilisateurId,
                "Promotion",
                sauvegarde.getId(),
                AuditLog.TypeAction.CREATE,
                null,
                promotionDTO,
                ipAddress);

        return promotionDTO;
    }

    // ===== MODIFICATION =====

    /**
     * Modifie une promotion existante.
     *
     * <p>Vérifie l'unicité du nouveau nom si modifié
     * et enregistre l'action dans l'audit.</p>
     *
     * @param id                identifiant de la promotion
     * @param dto               les nouvelles données
     * @param utilisateurId     l'admin qui effectue la modification
     * @param ipAddress         adresse IP de l'admin
     * @return                  le DTO de la promotion modifiée
     * @throws ResourceNotFoundException    si la promotion n'existe pas
     * @throws DuplicateResourceException   si le nouveau nom existe déjà
     */
    @Transactional
    public PromotionDTO modifier(
            Long id,
            PromotionUpdateDTO dto,
            Long utilisateurId,
            String ipAddress) {

        // 1. Récupérer la promotion existante
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Promotion", "id", id));

        // 2. Vérifier unicité du nom si changé
        if (!promotion.getNom().equals(dto.getNom()) &&
                promotionRepository.existsByNom(dto.getNom())) {
            throw new DuplicateResourceException(
                    "Promotion", "nom", dto.getNom());
        }

        // 3. Calculer nbEtudiants avant modification pour l'audit
        int nbEtudiants = getNbEtudiantsActifs(promotion.getId());
        PromotionDTO avant = promotionMapper.toDTO(promotion, nbEtudiants);

        // 4. Mettre à jour via mapper
        promotionMapper.updateEntity(dto, promotion);
        Promotion sauvegarde = promotionRepository.save(promotion);

        // 5. Audit
        PromotionDTO apres = promotionMapper.toDTO(sauvegarde, nbEtudiants);
        auditService.enregistrer(
                utilisateurId,
                "Promotion",
                sauvegarde.getId(),
                AuditLog.TypeAction.UPDATE,
                avant,
                apres,
                ipAddress);

        return apres;
    }

    // ===== SUPPRESSION =====

    /**
     * Supprime une promotion.
     *
     * <p>Vérifie qu'aucun étudiant n'est actif dans
     * cette promotion avant la suppression.</p>
     *
     * @param id                identifiant de la promotion
     * @param utilisateurId     l'admin qui effectue la suppression
     * @param ipAddress         adresse IP de l'admin
     * @throws ResourceNotFoundException    si la promotion n'existe pas
     * @throws ValidationException          si des étudiants sont actifs
     */
    @Transactional
    public void supprimer(
            Long id,
            Long utilisateurId,
            String ipAddress) {

        // 1. Récupérer la promotion
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Promotion", "id", id));

        // 2. Vérifier qu'aucun étudiant n'est actif
        int nbEtudiants = getNbEtudiantsActifs(promotion.getId());
        if (nbEtudiants > 0) {
            throw new ValidationException(
                    "Cette promotion contient " + nbEtudiants
                            + " étudiant(s) actif(s) — suppression impossible");
        }

        // 3. Audit avant suppression
        auditService.enregistrer(
                utilisateurId,
                "Promotion",
                promotion.getId(),
                AuditLog.TypeAction.DELETE,
                promotionMapper.toDTO(promotion, 0),
                null,
                ipAddress);

        // 4. Supprimer
        promotionRepository.delete(promotion);
    }

    // ===== CONSULTATION =====

    /**
     * Récupère une promotion par son identifiant.
     *
     * <p>Retourne l'entité pour les autres services
     * qui ont besoin de créer des relations JPA.</p>
     *
     * @param id    identifiant de la promotion
     * @return      l'entité Promotion
     * @throws ResourceNotFoundException    si la promotion n'existe pas
     */
    @Transactional(readOnly = true)
    public Promotion getEntityById(Long id) {
        return promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Promotion", "id", id));
    }

    /**
     * Récupère une promotion par son identifiant en DTO.
     *
     * @param id    identifiant de la promotion
     * @return      le DTO de la promotion
     * @throws ResourceNotFoundException    si la promotion n'existe pas
     */
    @Transactional(readOnly = true)
    public PromotionDTO getById(Long id) {
        Promotion promotion = getEntityById(id);
        int nbEtudiants = getNbEtudiantsActifs(id);
        return promotionMapper.toDTO(promotion, nbEtudiants);
    }

    /**
     * Récupère toutes les promotions avec leur nombre
     * d'étudiants actifs.
     *
     * @return  liste de toutes les promotions
     */
    @Transactional(readOnly = true)
    public List<PromotionDTO> getAll() {
        return promotionRepository.findAll()
                .stream()
                .map(p -> promotionMapper.toDTO(
                        p, getNbEtudiantsActifs(p.getId())))
                .collect(Collectors.toList());
    }

    /**
     * Recherche des promotions par nom.
     *
     * @param nom   le nom à rechercher
     * @return      liste des promotions correspondantes
     */
    @Transactional(readOnly = true)
    public List<PromotionDTO> rechercherParNom(String nom) {
        return promotionRepository
                .findByNomContainingIgnoreCase(nom)
                .stream()
                .map(p -> promotionMapper.toDTO(
                        p, getNbEtudiantsActifs(p.getId())))
                .collect(Collectors.toList());
    }

    // ===== MÉTHODES PRIVÉES =====

    /**
     * Calcule le nombre d'étudiants actifs dans une promotion.
     *
     * <p>Compte toutes les inscriptions actives
     * toutes années confondues.</p>
     *
     * @param promotionId   identifiant de la promotion
     * @return              le nombre d'étudiants actifs
     */
    private int getNbEtudiantsActifs(Long promotionId) {
        return inscriptionRepository
                .findByPromotionId(promotionId)
                .stream()
                .filter(i -> i.isActif())
                .collect(Collectors.toList())
                .size();
    }
}