package org.harold.plateforme.controller;

import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.alerte.AlerteDTO;
import org.harold.plateforme.dto.alerte.NotificationDTO;
import org.harold.plateforme.security.SecurityUtils;
import org.harold.plateforme.service.AlerteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller de gestion des alertes et notifications.
 *
 * <p>Utilisé par le polling REST V1 pour notifier
 * les enseignants et responsables des étudiants à risque.
 * Gère la consultation et la mise à jour des notifications.
 * L'utilisateur est identifié via le token JWT : chacun ne
 * voit et ne modifie que ses propres notifications.</p>
 *
 * @author Harold
 * @version 1.0
 */
@RestController
@RequestMapping("/api/alertes")
@RequiredArgsConstructor
public class AlerteController {

    private final AlerteService alerteService;

    /**
     * Récupère toutes les notifications de l'utilisateur connecté.
     *
     * <p>Appelé par le polling REST toutes les 30 secondes
     * depuis React pour mettre à jour la cloche.</p>
     *
     * @return  HTTP 200 avec la liste des notifications
     */
    @GetMapping("/notifications")
    @PreAuthorize("hasAnyRole('ENSEIGNANT','RESPONSABLE')")
    public ResponseEntity<List<NotificationDTO>> getNotifications() {
        Long utilisateurId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(
                alerteService.getNotifications(utilisateurId));
    }

    /**
     * Récupère les notifications non lues de l'utilisateur connecté.
     *
     * @return  HTTP 200 avec les notifications non lues
     */
    @GetMapping("/notifications/non-lues")
    @PreAuthorize("hasAnyRole('ENSEIGNANT','RESPONSABLE')")
    public ResponseEntity<List<NotificationDTO>> getNotificationsNonLues() {
        Long utilisateurId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(
                alerteService.getNotificationsNonLues(
                        utilisateurId));
    }

    /**
     * Récupère le nombre de notifications non lues
     * de l'utilisateur connecté.
     *
     * <p>Utilisé pour afficher le badge sur la cloche
     * du header React.</p>
     *
     * @return  HTTP 200 avec le nombre de notifications
     */
    @GetMapping("/notifications/nb-non-lues")
    @PreAuthorize("hasAnyRole('ENSEIGNANT','RESPONSABLE')")
    public ResponseEntity<Long> getNbNotificationsNonLues() {
        Long utilisateurId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(
                alerteService.getNbNotificationsNonLues(
                        utilisateurId));
    }

    /**
     * Marque une notification comme lue.
     *
     * @param id    identifiant de la notification
     * @return      HTTP 200 si mise à jour réussie
     */
    @PutMapping("/notifications/{id}/lue")
    @PreAuthorize("hasAnyRole('ENSEIGNANT','RESPONSABLE')")
    public ResponseEntity<Void> marquerCommeLue(
            @PathVariable Long id) {
        Long utilisateurId = SecurityUtils.getCurrentUserId();
        alerteService.marquerCommeLue(id, utilisateurId);
        return ResponseEntity.ok().build();
    }

    /**
     * Marque toutes les notifications de l'utilisateur
     * connecté comme lues.
     *
     * @return  HTTP 200 si mise à jour réussie
     */
    @PutMapping("/notifications/toutes-lues")
    @PreAuthorize("hasAnyRole('ENSEIGNANT','RESPONSABLE')")
    public ResponseEntity<Void> marquerToutesCommeLues() {
        Long utilisateurId = SecurityUtils.getCurrentUserId();
        alerteService.marquerToutesCommeLues(utilisateurId);
        return ResponseEntity.ok().build();
    }

    /**
     * Récupère les alertes élevées et critiques d'une promotion.
     *
     * <p>Utilisé dans le dashboard du responsable pédagogique
     * pour afficher les étudiants prioritaires.</p>
     *
     * @param promotionId       identifiant de la promotion
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  HTTP 200 avec la liste des alertes
     */
    @GetMapping("/critiques/promotion/{promotionId}")
    @PreAuthorize("hasRole('RESPONSABLE')")
    public ResponseEntity<List<AlerteDTO>> getAlertesCritiques(
            @PathVariable Long promotionId,
            @RequestParam Long anneeAcademiqueId) {
        return ResponseEntity.ok(
                alerteService.getAlertesCritiques(
                        promotionId, anneeAcademiqueId));
    }

    /**
     * Récupère le nombre d'alertes non lues d'une promotion.
     *
     * @param promotionId       identifiant de la promotion
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  HTTP 200 avec le nombre d'alertes
     */
    @GetMapping("/nb-non-lues/promotion/{promotionId}")
    @PreAuthorize("hasRole('RESPONSABLE')")
    public ResponseEntity<Long> getNbAlertesNonLues(
            @PathVariable Long promotionId,
            @RequestParam Long anneeAcademiqueId) {
        return ResponseEntity.ok(
                alerteService.getNbAlertesNonLues(
                        promotionId, anneeAcademiqueId));
    }
}