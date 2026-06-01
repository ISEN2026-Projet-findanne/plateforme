package org.harold.plateforme.service;

import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.anneeacademique.AnneeAcademiqueCreateDTO;
import org.harold.plateforme.dto.anneeacademique.AnneeAcademiqueDTO;
import org.harold.plateforme.entity.AnneeAcademique;
import org.harold.plateforme.entity.AuditLog;
import org.harold.plateforme.exception.DuplicateResourceException;
import org.harold.plateforme.exception.ResourceNotFoundException;
import org.harold.plateforme.mapper.AnneeAcademiqueMapper;
import org.harold.plateforme.repository.AnneeAcademiqueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de gestion des années académiques.
 *
 * <p>Gère la création, modification et activation
 * des années académiques. Une seule année peut être
 * active à la fois — l'activation d'une année désactive
 * automatiquement toutes les autres.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class AnneeAcademiqueService {

    private final AnneeAcademiqueRepository anneeAcademiqueRepository;
    private final AnneeAcademiqueMapper anneeAcademiqueMapper;
    private final AuditService auditService;

    // ===== CRÉATION =====

    /**
     * Crée une nouvelle année académique.
     *
     * <p>Vérifie l'unicité du libellé court (ex: 2024/2025)
     * et enregistre l'action dans l'audit.</p>
     *
     * @param dto               les données de création
     * @param utilisateurId     l'admin qui crée l'année
     * @param ipAddress         adresse IP de l'admin
     * @return                  le DTO de l'année créée
     * @throws DuplicateResourceException   si l'année existe déjà
     */
    @Transactional
    public AnneeAcademiqueDTO creer(
            AnneeAcademiqueCreateDTO dto,
            Long utilisateurId,
            String ipAddress) {

        // 1. Vérifier unicité
        if (anneeAcademiqueRepository.existsByAnnee(dto.getAnnee())) {
            throw new DuplicateResourceException(
                    "AnneeAcademique", "annee", dto.getAnnee());
        }

        // 2. Créer l'entité via mapper
        AnneeAcademique annee = anneeAcademiqueMapper.toEntity(dto);

        // 3. Sauvegarder
        AnneeAcademique sauvegarde =
                anneeAcademiqueRepository.save(annee);

        // 4. Audit
        auditService.enregistrer(
                utilisateurId,
                "AnneeAcademique",
                sauvegarde.getId(),
                AuditLog.TypeAction.CREATE,
                null,
                anneeAcademiqueMapper.toDTO(sauvegarde),
                ipAddress);

        return anneeAcademiqueMapper.toDTO(sauvegarde);
    }

    // ===== MODIFICATION =====

    /**
     * Modifie une année académique existante.
     *
     * <p>Permet de modifier le libellé long uniquement.
     * Le libellé court (ex: 2024/2025) n'est pas modifiable.</p>
     *
     * @param id                identifiant de l'année à modifier
     * @param dto               les nouvelles données
     * @param utilisateurId     l'admin qui effectue la modification
     * @param ipAddress         adresse IP de l'admin
     * @return                  le DTO de l'année modifiée
     * @throws ResourceNotFoundException    si l'année n'existe pas
     */
    @Transactional
    public AnneeAcademiqueDTO modifier(
            Long id,
            AnneeAcademiqueCreateDTO dto,
            Long utilisateurId,
            String ipAddress) {

        // 1. Récupérer l'année existante
        AnneeAcademique annee = anneeAcademiqueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "AnneeAcademique", "id", id));

        // 2. Sauvegarder état avant pour l'audit
        AnneeAcademiqueDTO avant =
                anneeAcademiqueMapper.toDTO(annee);

        // 3. Mettre à jour via mapper
        anneeAcademiqueMapper.updateEntity(dto, annee);
        AnneeAcademique sauvegarde =
                anneeAcademiqueRepository.save(annee);

        // 4. Audit
        auditService.enregistrer(
                utilisateurId,
                "AnneeAcademique",
                sauvegarde.getId(),
                AuditLog.TypeAction.UPDATE,
                avant,
                anneeAcademiqueMapper.toDTO(sauvegarde),
                ipAddress);

        return anneeAcademiqueMapper.toDTO(sauvegarde);
    }

    // ===== ACTIVATION =====

    /**
     * Active une année académique.
     *
     * <p>Désactive automatiquement toutes les autres années
     * avant d'activer celle demandée.
     * Une seule année peut être active à la fois.</p>
     *
     * @param id                identifiant de l'année à activer
     * @param utilisateurId     l'admin qui effectue l'activation
     * @param ipAddress         adresse IP de l'admin
     * @return                  le DTO de l'année activée
     * @throws ResourceNotFoundException    si l'année n'existe pas
     */
    @Transactional
    public AnneeAcademiqueDTO activer(
            Long id,
            Long utilisateurId,
            String ipAddress) {

        // 1. Récupérer l'année à activer
        AnneeAcademique annee = anneeAcademiqueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "AnneeAcademique", "id", id));

        // 2. Désactiver l'année active actuelle si elle existe
        anneeAcademiqueRepository.findByActiveTrue()
                .ifPresent(anneeActive -> {
                    anneeActive.setActive(false);
                    anneeAcademiqueRepository.save(anneeActive);
                });

        // 3. Sauvegarder état avant pour l'audit
        AnneeAcademiqueDTO avant =
                anneeAcademiqueMapper.toDTO(annee);

        // 4. Activer l'année demandée
        annee.setActive(true);
        AnneeAcademique sauvegarde =
                anneeAcademiqueRepository.save(annee);

        // 5. Audit
        auditService.enregistrer(
                utilisateurId,
                "AnneeAcademique",
                sauvegarde.getId(),
                AuditLog.TypeAction.UPDATE,
                avant,
                anneeAcademiqueMapper.toDTO(sauvegarde),
                ipAddress);

        return anneeAcademiqueMapper.toDTO(sauvegarde);
    }

    // ===== CONSULTATION =====

    /**
     * Récupère l'année académique active.
     *
     * <p>Utilisée par défaut dans tous les services
     * qui ont besoin de l'année courante.</p>
     *
     * @return                          le DTO de l'année active
     * @throws ResourceNotFoundException si aucune année n'est active
     */
    @Transactional(readOnly = true)
    public AnneeAcademiqueDTO getActive() {
        return anneeAcademiqueMapper.toDTO(
                anneeAcademiqueRepository.findByActiveTrue()
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "AnneeAcademique", "active", true)));
    }

    /**
     * Récupère une année académique par son identifiant.
     *
     * <p>Retourne l'entité pour les autres services
     * qui ont besoin de créer des relations JPA.</p>
     *
     * @param id    identifiant de l'année
     * @return      l'entité AnneeAcademique
     * @throws ResourceNotFoundException    si l'année n'existe pas
     */
    @Transactional(readOnly = true)
    public AnneeAcademique getEntityById(Long id) {
        return anneeAcademiqueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "AnneeAcademique", "id", id));
    }

    /**
     * Récupère toutes les années académiques.
     *
     * @return  liste de toutes les années académiques
     */
    @Transactional(readOnly = true)
    public List<AnneeAcademiqueDTO> getAll() {
        return anneeAcademiqueRepository.findAll()
                .stream()
                .map(anneeAcademiqueMapper::toDTO)
                .collect(Collectors.toList());
    }
}