package org.harold.plateforme.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.audit.AuditLogDTO;
import org.harold.plateforme.entity.AuditLog;
import org.harold.plateforme.entity.Utilisateur;
import org.harold.plateforme.exception.ResourceNotFoundException;
import org.harold.plateforme.repository.AuditLogRepository;
import org.harold.plateforme.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de gestion des logs d'audit.
 *
 * <p>Trace toutes les modifications effectuées dans le système.
 * La méthode enregistrer() est appelée par tous les autres services
 * lors de chaque CREATE, UPDATE ou DELETE.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final UtilisateurRepository utilisateurRepository;

    /**
     * ObjectMapper configuré pour sérialiser les entités en JSON.
     * JavaTimeModule permet de gérer les LocalDate et LocalDateTime.
     */
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    // ===== MÉTHODE PRINCIPALE =====

    /**
     * Enregistre une action dans les logs d'audit.
     *
     * <p>Appelée par tous les autres services lors de chaque
     * CREATE, UPDATE ou DELETE. Convertit les objets avant
     * et après modification en JSON pour la traçabilité.</p>
     *
     * @param utilisateurId     identifiant de l'utilisateur
     * @param entite            nom de l'entité concernée
     * @param entiteId          identifiant de l'enregistrement
     * @param action            type d'action (CREATE/UPDATE/DELETE)
     * @param objetAvant        état avant modification (null pour CREATE)
     * @param objetApres        état après modification (null pour DELETE)
     * @param ipAddress         adresse IP de l'utilisateur
     */
    @Transactional
    public void enregistrer(
            Long utilisateurId,
            String entite,
            Long entiteId,
            AuditLog.TypeAction action,
            Object objetAvant,
            Object objetApres,
            String ipAddress) {

        Utilisateur utilisateur = utilisateurRepository
                .findById(utilisateurId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utilisateur", "id", utilisateurId));

        AuditLog log = new AuditLog();
        log.setUtilisateur(utilisateur);
        log.setEntite(entite);
        log.setEntiteId(entiteId);
        log.setAction(action);
        log.setIpAddress(ipAddress);

        // Conversion des objets en JSON
        log.setAvantJson(convertirEnJson(objetAvant));
        log.setApresJson(convertirEnJson(objetApres));

        auditLogRepository.save(log);
    }

    // ===== MÉTHODES DE CONSULTATION =====

    /**
     * Récupère tous les logs d'audit d'un utilisateur.
     *
     * @param utilisateurId     identifiant de l'utilisateur
     * @return                  liste des logs triés par date décroissante
     */
    @Transactional(readOnly = true)
    public List<AuditLogDTO> getByUtilisateur(Long utilisateurId) {
        return auditLogRepository
                .findByUtilisateurIdOrderByCreatedAtDesc(utilisateurId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère tous les logs d'audit d'une entité précise.
     *
     * @param entite        nom de l'entité (ex: "Etudiant")
     * @param entiteId      identifiant de l'enregistrement
     * @return              liste des logs triés par date décroissante
     */
    @Transactional(readOnly = true)
    public List<AuditLogDTO> getByEntite(String entite, Long entiteId) {
        return auditLogRepository
                .findByEntiteAndEntiteIdOrderByCreatedAtDesc(
                        entite, entiteId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère tous les logs d'audit entre deux dates.
     *
     * @param dateDebut     date de début de la période
     * @param dateFin       date de fin de la période
     * @return              liste des logs triés par date décroissante
     */
    @Transactional(readOnly = true)
    public List<AuditLogDTO> getByPeriode(
            LocalDateTime dateDebut,
            LocalDateTime dateFin) {
        return auditLogRepository
                .findByPeriode(dateDebut, dateFin)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère tous les logs d'audit par type d'action.
     *
     * @param action    type d'action (CREATE/UPDATE/DELETE)
     * @return          liste des logs triés par date décroissante
     */
    @Transactional(readOnly = true)
    public List<AuditLogDTO> getByAction(AuditLog.TypeAction action) {
        return auditLogRepository
                .findByActionOrderByCreatedAtDesc(action)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ===== MÉTHODES PRIVÉES =====

    /**
     * Convertit un objet en JSON String.
     *
     * <p>Retourne null si l'objet est null.
     * Retourne un message d'erreur si la sérialisation échoue.</p>
     *
     * @param objet     l'objet à convertir
     * @return          la représentation JSON ou null
     */
    private String convertirEnJson(Object objet) {
        if (objet == null) return null;
        try {
            return objectMapper.writeValueAsString(objet);
        } catch (JsonProcessingException e) {
            return "{\"erreur\": \"Impossible de sérialiser l'objet\"}";
        }
    }

    /**
     * Convertit une entité AuditLog en AuditLogDTO.
     *
     * @param log   l'entité à convertir
     * @return      le DTO correspondant
     */
    private AuditLogDTO toDTO(AuditLog log) {
        AuditLogDTO dto = new AuditLogDTO();
        dto.setId(log.getId());
        dto.setEntite(log.getEntite());
        dto.setEntiteId(log.getEntiteId());
        dto.setAction(log.getAction().name());
        dto.setAvantJson(log.getAvantJson());
        dto.setApresJson(log.getApresJson());
        dto.setIpAddress(log.getIpAddress());
        dto.setCreatedAt(log.getCreatedAt());

        if (log.getUtilisateur() != null) {
            dto.setUtilisateurId(log.getUtilisateur().getId());
            dto.setUtilisateurNom(log.getUtilisateur().getNom());
            dto.setUtilisateurPrenom(log.getUtilisateur().getPrenom());
        }

        return dto;
    }
}