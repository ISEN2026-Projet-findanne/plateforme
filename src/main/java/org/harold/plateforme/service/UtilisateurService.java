package org.harold.plateforme.service;

import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.utilisateur.UtilisateurCreateDTO;
import org.harold.plateforme.dto.utilisateur.UtilisateurDTO;
import org.harold.plateforme.dto.utilisateur.UtilisateurUpdateDTO;
import org.harold.plateforme.entity.AuditLog;
import org.harold.plateforme.entity.Utilisateur;
import org.harold.plateforme.exception.DuplicateResourceException;
import org.harold.plateforme.exception.ResourceNotFoundException;
import org.harold.plateforme.exception.ValidationException;
import org.harold.plateforme.mapper.UtilisateurMapper;
import org.harold.plateforme.repository.UtilisateurRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de gestion des utilisateurs.
 *
 * <p>Gère la création, modification, activation/désactivation
 * et consultation des comptes utilisateurs.
 * Tous les mots de passe sont hashés via BCrypt avant
 * sauvegarde en base.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final UtilisateurMapper utilisateurMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuditService auditService;

    // ===== CRÉATION =====

    /**
     * Crée un nouvel utilisateur.
     *
     * <p>Vérifie l'unicité de l'email, valide le rôle,
     * hashe le mot de passe et enregistre l'action dans l'audit.</p>
     *
     * @param dto               les données de création
     * @param utilisateurId     l'admin qui crée le compte
     * @param ipAddress         adresse IP de l'admin
     * @return                  le DTO de l'utilisateur créé
     * @throws DuplicateResourceException   si l'email existe déjà
     * @throws ValidationException          si le rôle est invalide
     */
    @Transactional
    public UtilisateurDTO creer(
            UtilisateurCreateDTO dto,
            Long utilisateurId,
            String ipAddress) {

        // 1. Vérifier unicité email
        if (utilisateurRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException(
                    "Utilisateur", "email", dto.getEmail());
        }

        // 2. Valider le rôle
        validerRole(dto.getRole());

        // 3. Créer l'entité
        Utilisateur utilisateur = utilisateurMapper.toEntity(dto);

        // 4. Hasher le mot de passe
        utilisateur.setMotDePasse(
                passwordEncoder.encode(dto.getMotDePasse()));

        // 5. Sauvegarder
        Utilisateur sauvegarde = utilisateurRepository.save(utilisateur);

        // 6. Enregistrer dans l'audit
        auditService.enregistrer(
                utilisateurId,
                "Utilisateur",
                sauvegarde.getId(),
                AuditLog.TypeAction.CREATE,
                null,
                utilisateurMapper.toDTO(sauvegarde),
                ipAddress);

        return utilisateurMapper.toDTO(sauvegarde);
    }

    // ===== MODIFICATION =====

    /**
     * Modifie un utilisateur existant.
     *
     * <p>Vérifie l'unicité du nouvel email si modifié,
     * valide le rôle et enregistre l'action dans l'audit.</p>
     *
     * @param id                identifiant de l'utilisateur à modifier
     * @param dto               les nouvelles données
     * @param utilisateurId     l'admin qui effectue la modification
     * @param ipAddress         adresse IP de l'admin
     * @return                  le DTO de l'utilisateur modifié
     * @throws ResourceNotFoundException    si l'utilisateur n'existe pas
     * @throws DuplicateResourceException   si le nouvel email existe déjà
     * @throws ValidationException          si le rôle est invalide
     */
    @Transactional
    public UtilisateurDTO modifier(
            Long id,
            UtilisateurUpdateDTO dto,
            Long utilisateurId,
            String ipAddress) {

        // 1. Récupérer l'utilisateur existant
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utilisateur", "id", id));

        // 2. Vérifier unicité email si changé
        if (!utilisateur.getEmail().equals(dto.getEmail()) &&
                utilisateurRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException(
                    "Utilisateur", "email", dto.getEmail());
        }

        // 3. Valider le rôle
        validerRole(dto.getRole());

        // 4. Sauvegarder l'état avant modification pour l'audit
        UtilisateurDTO avant = utilisateurMapper.toDTO(utilisateur);

        // 5. Mettre à jour l'entité
        utilisateurMapper.updateEntity(dto, utilisateur);
        Utilisateur sauvegarde = utilisateurRepository.save(utilisateur);

        // 6. Enregistrer dans l'audit
        auditService.enregistrer(
                utilisateurId,
                "Utilisateur",
                sauvegarde.getId(),
                AuditLog.TypeAction.UPDATE,
                avant,
                utilisateurMapper.toDTO(sauvegarde),
                ipAddress);

        return utilisateurMapper.toDTO(sauvegarde);
    }

    // ===== CONSULTATION =====

    /**
     * Récupère un utilisateur par son identifiant.
     *
     * @param id    identifiant de l'utilisateur
     * @return      le DTO de l'utilisateur
     * @throws ResourceNotFoundException    si l'utilisateur n'existe pas
     */
    @Transactional(readOnly = true)
    public UtilisateurDTO getById(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utilisateur", "id", id));
        return utilisateurMapper.toDTO(utilisateur);
    }

    /**
     * Récupère tous les utilisateurs.
     *
     * @return  liste de tous les utilisateurs
     */
    @Transactional(readOnly = true)
    public List<UtilisateurDTO> getAll() {
        return utilisateurRepository.findAll()
                .stream()
                .map(utilisateurMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère un utilisateur par son email.
     *
     * <p>Utilisé par Spring Security lors de l'authentification.</p>
     *
     * @param email     email de l'utilisateur
     * @return          l'entité Utilisateur
     * @throws ResourceNotFoundException    si l'utilisateur n'existe pas
     */
    @Transactional(readOnly = true)
    public Utilisateur getByEmail(String email) {
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utilisateur", "email", email));
    }

    // ===== MÉTHODES PRIVÉES =====

    /**
     * Valide que le rôle fourni est bien l'un des rôles autorisés.
     *
     * @param role  le rôle à valider
     * @throws ValidationException  si le rôle est invalide
     */
    private void validerRole(String role) {
        try {
            Utilisateur.Role.valueOf(role);
        } catch (IllegalArgumentException e) {
            throw new ValidationException(
                    "role",
                    "Rôle invalide. Valeurs acceptées : "
                            + "ROLE_ADMIN, ROLE_ENSEIGNANT, ROLE_RESPONSABLE");
        }
    }
}