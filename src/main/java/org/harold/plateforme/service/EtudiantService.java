package org.harold.plateforme.service;

import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.etudiant.EtudiantCreateDTO;
import org.harold.plateforme.dto.etudiant.EtudiantDTO;
import org.harold.plateforme.dto.etudiant.EtudiantUpdateDTO;
import org.harold.plateforme.entity.AuditLog;
import org.harold.plateforme.entity.Etudiant;
import org.harold.plateforme.entity.Inscription;
import org.harold.plateforme.exception.DuplicateResourceException;
import org.harold.plateforme.exception.ResourceNotFoundException;
import org.harold.plateforme.exception.ValidationException;
import org.harold.plateforme.mapper.EtudiantMapper;
import org.harold.plateforme.repository.EtudiantRepository;
import org.harold.plateforme.repository.InscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de gestion des étudiants.
 *
 * <p>Gère la création, modification et consultation
 * des étudiants ainsi que leur mobilité entre promotions.
 * La création d'un étudiant crée automatiquement
 * son inscription dans la promotion.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class EtudiantService {

    private final EtudiantRepository etudiantRepository;
    private final InscriptionRepository inscriptionRepository;
    private final EtudiantMapper etudiantMapper;
    private final PromotionService promotionService;
    private final AnneeAcademiqueService anneeAcademiqueService;
    private final AuditService auditService;

    // ===== CRÉATION =====

    /**
     * Crée un nouvel étudiant et son inscription.
     *
     * <p>Vérifie l'unicité du numéro étudiant et de l'email,
     * crée l'étudiant puis crée son inscription dans
     * la promotion pour l'année académique donnée.
     * Les deux opérations sont dans la même transaction.</p>
     *
     * @param dto               les données de création
     * @param utilisateurId     l'utilisateur qui crée l'étudiant
     * @param ipAddress         adresse IP de l'utilisateur
     * @return                  le DTO de l'étudiant créé
     * @throws DuplicateResourceException   si numeroEtudiant
     *                                      ou email existe déjà
     * @throws ResourceNotFoundException    si promotion ou année inexistante
     */
    @Transactional
    public EtudiantDTO creer(
            EtudiantCreateDTO dto,
            Long utilisateurId,
            String ipAddress) {

        // 1. Vérifier unicité du numéro étudiant
        if (etudiantRepository.existsByNumeroEtudiant(
                dto.getNumeroEtudiant())) {
            throw new DuplicateResourceException(
                    "Etudiant", "numeroEtudiant",
                    dto.getNumeroEtudiant());
        }

        // 2. Vérifier unicité de l'email si renseigné
        if (dto.getEmail() != null &&
                !dto.getEmail().isEmpty() &&
                etudiantRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException(
                    "Etudiant", "email", dto.getEmail());
        }

        // 3. Créer l'entité étudiant via mapper
        Etudiant etudiant = etudiantMapper.toEntity(dto);

        // 4. Sauvegarder l'étudiant
        Etudiant sauvegarde = etudiantRepository.save(etudiant);

        // 5. Créer l'inscription
        Inscription inscription = new Inscription();
        inscription.setEtudiant(sauvegarde);
        inscription.setPromotion(
                promotionService.getEntityById(dto.getPromotionId()));
        inscription.setAnneeAcademique(
                anneeAcademiqueService.getEntityById(
                        dto.getAnneeAcademiqueId()));
        inscription.setNiveau(dto.getNiveau());
        inscription.setActif(true);
        inscriptionRepository.save(inscription);

        // 6. Audit
        EtudiantDTO etudiantDTO =
                etudiantMapper.toDTO(sauvegarde, inscription);
        auditService.enregistrer(
                utilisateurId,
                "Etudiant",
                sauvegarde.getId(),
                AuditLog.TypeAction.CREATE,
                null,
                etudiantDTO,
                ipAddress);

        return etudiantDTO;
    }

    // ===== MODIFICATION =====

    /**
     * Modifie un étudiant existant.
     *
     * <p>Le numéro étudiant n'est pas modifiable.
     * Le changement de promotion passe par changerPromotion().</p>
     *
     * @param id                identifiant de l'étudiant
     * @param dto               les nouvelles données
     * @param utilisateurId     l'utilisateur qui modifie
     * @param ipAddress         adresse IP de l'utilisateur
     * @return                  le DTO de l'étudiant modifié
     * @throws ResourceNotFoundException    si l'étudiant n'existe pas
     * @throws DuplicateResourceException   si le nouvel email existe déjà
     */
    @Transactional
    public EtudiantDTO modifier(
            Long id,
            EtudiantUpdateDTO dto,
            Long utilisateurId,
            String ipAddress) {

        // 1. Récupérer l'étudiant existant
        Etudiant etudiant = getEntityById(id);

        // 2. Vérifier unicité email si changé
        if (dto.getEmail() != null &&
                !dto.getEmail().equals(etudiant.getEmail()) &&
                etudiantRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException(
                    "Etudiant", "email", dto.getEmail());
        }

        // 3. Récupérer inscription active pour l'audit
        Inscription inscription = inscriptionRepository
                .findByEtudiantIdAndActifTrue(etudiant.getId())
                .orElse(null);

        // 4. Sauvegarder état avant pour l'audit
        EtudiantDTO avant = etudiantMapper.toDTO(etudiant, inscription);

        // 5. Mettre à jour via mapper
        etudiantMapper.updateEntity(dto, etudiant);
        Etudiant sauvegarde = etudiantRepository.save(etudiant);

        // 6. Audit
        EtudiantDTO apres =
                etudiantMapper.toDTO(sauvegarde, inscription);
        auditService.enregistrer(
                utilisateurId,
                "Etudiant",
                sauvegarde.getId(),
                AuditLog.TypeAction.UPDATE,
                avant,
                apres,
                ipAddress);

        return apres;
    }

    // ===== CHANGEMENT DE PROMOTION =====

    /**
     * Change la promotion d'un étudiant.
     *
     * <p>Désactive l'inscription active et crée une nouvelle
     * inscription dans la nouvelle promotion.
     * L'historique des inscriptions est conservé.</p>
     *
     * @param etudiantId            identifiant de l'étudiant
     * @param promotionId           identifiant de la nouvelle promotion
     * @param anneeAcademiqueId     identifiant de l'année académique
     * @param niveau                niveau dans la nouvelle promotion
     * @param motifChangement       motif du changement
     * @param utilisateurId         l'utilisateur qui effectue le changement
     * @param ipAddress             adresse IP de l'utilisateur
     * @return                      le DTO de l'étudiant mis à jour
     * @throws ResourceNotFoundException    si étudiant, promotion
     *                                      ou année inexistante
     * @throws ValidationException          si aucune inscription active
     */
    @Transactional
    public EtudiantDTO changerPromotion(
            Long etudiantId,
            Long promotionId,
            Long anneeAcademiqueId,
            String niveau,
            String motifChangement,
            Long utilisateurId,
            String ipAddress) {

        // 1. Récupérer l'étudiant
        Etudiant etudiant = getEntityById(etudiantId);

        // 2. Récupérer l'inscription active
        Inscription inscriptionActive = inscriptionRepository
                .findByEtudiantIdAndActifTrue(etudiantId)
                .orElseThrow(() -> new ValidationException(
                        "Cet étudiant n'a pas d'inscription active"));

        // 3. Désactiver l'inscription active
        inscriptionActive.setActif(false);
        inscriptionRepository.save(inscriptionActive);

        // 4. Créer la nouvelle inscription
        Inscription nouvelleInscription = new Inscription();
        nouvelleInscription.setEtudiant(etudiant);
        nouvelleInscription.setPromotion(
                promotionService.getEntityById(promotionId));
        nouvelleInscription.setAnneeAcademique(
                anneeAcademiqueService.getEntityById(anneeAcademiqueId));
        nouvelleInscription.setNiveau(niveau);
        nouvelleInscription.setActif(true);
        nouvelleInscription.setMotifChangement(motifChangement);
        inscriptionRepository.save(nouvelleInscription);

        // 5. Audit
        EtudiantDTO apres =
                etudiantMapper.toDTO(etudiant, nouvelleInscription);
        auditService.enregistrer(
                utilisateurId,
                "Inscription",
                nouvelleInscription.getId(),
                AuditLog.TypeAction.CREATE,
                etudiantMapper.toDTO(etudiant, inscriptionActive),
                apres,
                ipAddress);

        return apres;
    }

    // ===== CONSULTATION =====

    /**
     * Récupère un étudiant par son identifiant.
     *
     * <p>Retourne l'entité pour les autres services.</p>
     *
     * @param id    identifiant de l'étudiant
     * @return      l'entité Etudiant
     * @throws ResourceNotFoundException    si l'étudiant n'existe pas
     */
    @Transactional(readOnly = true)
    public Etudiant getEntityById(Long id) {
        return etudiantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Etudiant", "id", id));
    }

    /**
     * Récupère un étudiant par son identifiant en DTO.
     *
     * <p>Inclut les informations de l'inscription active.</p>
     *
     * @param id    identifiant de l'étudiant
     * @return      le DTO de l'étudiant avec inscription active
     * @throws ResourceNotFoundException    si l'étudiant n'existe pas
     */
    @Transactional(readOnly = true)
    public EtudiantDTO getById(Long id) {
        Etudiant etudiant = getEntityById(id);
        Inscription inscription = inscriptionRepository
                .findByEtudiantIdAndActifTrue(id)
                .orElse(null);
        return etudiantMapper.toDTO(etudiant, inscription);
    }

    /**
     * Récupère tous les étudiants d'une promotion
     * pour une année académique.
     *
     * @param promotionId           identifiant de la promotion
     * @param anneeAcademiqueId     identifiant de l'année académique
     * @return                      liste des étudiants
     */
    @Transactional(readOnly = true)
    public List<EtudiantDTO> getByPromotion(
            Long promotionId,
            Long anneeAcademiqueId) {
        return etudiantRepository
                .findByPromotionAndAnneeAcademique(
                        promotionId, anneeAcademiqueId)
                .stream()
                .map(e -> {
                    Inscription inscription = inscriptionRepository
                            .findByEtudiantIdAndActifTrue(e.getId())
                            .orElse(null);
                    return etudiantMapper.toDTO(e, inscription);
                })
                .collect(Collectors.toList());
    }

    /**
     * Recherche des étudiants par nom, prénom
     * ou numéro étudiant.
     *
     * @param query     le terme de recherche
     * @return          liste des étudiants correspondants
     */
    @Transactional(readOnly = true)
    public List<EtudiantDTO> rechercher(String query) {

        // Recherche par numéro étudiant d'abord
        return etudiantRepository
                .findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(
                        query, query)
                .stream()
                .map(e -> {
                    Inscription inscription = inscriptionRepository
                            .findByEtudiantIdAndActifTrue(e.getId())
                            .orElse(null);
                    return etudiantMapper.toDTO(e, inscription);
                })
                .collect(Collectors.toList());
    }

    /**
     * Récupère l'historique des inscriptions d'un étudiant.
     *
     * <p>Utilisé pour afficher les changements de promotion
     * dans la fiche étudiant.</p>
     *
     * @param etudiantId    identifiant de l'étudiant
     * @return              liste des inscriptions triées par date
     */
    @Transactional(readOnly = true)
    public List<Inscription> getHistoriqueInscriptions(
            Long etudiantId) {
        return inscriptionRepository
                .findByEtudiantIdOrderByDateInscriptionDesc(etudiantId);
    }
}