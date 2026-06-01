package org.harold.plateforme.controller;

import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.audit.AuditLogDTO;
import org.harold.plateforme.entity.AuditLog;
import org.harold.plateforme.service.AuditService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller de consultation des logs d'audit.
 *
 * <p>Accessible uniquement par l'administrateur.
 * Permet de tracer toutes les modifications
 * effectuées dans le système.</p>
 *
 * @author Harold
 * @version 1.0
 */
@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    /**
     * Récupère tous les logs d'audit d'un utilisateur.
     *
     * @param utilisateurId identifiant de l'utilisateur
     * @return              HTTP 200 avec la liste des logs
     */
    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<List<AuditLogDTO>> getByUtilisateur(
            @PathVariable Long utilisateurId) {
        return ResponseEntity.ok(
                auditService.getByUtilisateur(utilisateurId));
    }

    /**
     * Récupère l'historique complet d'un enregistrement précis.
     *
     * @param entite    nom de l'entité (ex: Etudiant, Note...)
     * @param entiteId  identifiant de l'enregistrement
     * @return          HTTP 200 avec la liste des logs
     */
    @GetMapping("/entite/{entite}/{entiteId}")
    public ResponseEntity<List<AuditLogDTO>> getByEntite(
            @PathVariable String entite,
            @PathVariable Long entiteId) {
        return ResponseEntity.ok(
                auditService.getByEntite(entite, entiteId));
    }

    /**
     * Récupère les logs d'audit entre deux dates.
     *
     * @param dateDebut date de début de la période
     * @param dateFin   date de fin de la période
     * @return          HTTP 200 avec la liste des logs
     */
    @GetMapping("/periode")
    public ResponseEntity<List<AuditLogDTO>> getByPeriode(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime dateDebut,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime dateFin) {
        return ResponseEntity.ok(
                auditService.getByPeriode(dateDebut, dateFin));
    }

    /**
     * Récupère les logs d'audit par type d'action.
     *
     * @param action    type d'action (CREATE, UPDATE, DELETE)
     * @return          HTTP 200 avec la liste des logs
     */
    @GetMapping("/action")
    public ResponseEntity<List<AuditLogDTO>> getByAction(
            @RequestParam AuditLog.TypeAction action) {
        return ResponseEntity.ok(
                auditService.getByAction(action));
    }
}