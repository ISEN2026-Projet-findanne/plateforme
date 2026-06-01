package org.harold.plateforme.controller;

import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.alerte.AlerteDTO;
import org.harold.plateforme.dto.alerte.NotificationDTO;
import org.harold.plateforme.service.AlerteService;
import org.springframework.http.ResponseEntity;
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
 * Gère la consultation et la mise à jour des notifications.</p>
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
     * Récupère toutes les notifications d'un utilisateur.
     *
     * <p>Appelé par le polling REST toutes les 30 secondes
     * depuis React pour mettre à jour la cloche.</p>
     *
     * @param utilisateurId identifiant de l'utilisateur connecté
     * @return              HTTP 200 avec la liste des notifications
     */
    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationDTO>> getNotifications(
            @RequestParam Long utilisateurId) {
        return ResponseEntity.ok(
                alerteService.getNotifications(utilisateurId));
    }

    /**
     * Récupère les notifications non lues d'un utilisateur.
     *
     * @param utilisateurId identifiant de l'utilisateur connecté
     * @return              HTTP 200 avec les notifications non lues
     */
    @GetMapping("/notifications/non-lues")
    public ResponseEntity<List<NotificationDTO>> getNotificationsNonLues(
            @RequestParam Long utilisateurId) {
        return ResponseEntity.ok(
                alerteService.getNotificationsNonLues(
                        utilisateurId));
    }

    /**
     * Récupère le nombre de notifications non lues.
     *
     * <p>Utilisé pour afficher le badge sur la cloche
     * du header React.</p>
     *
     * @param utilisateurId identifiant de l'utilisateur connecté
     * @return              HTTP 200 avec le nombre de notifications
     */
    @GetMapping("/notifications/nb-non-lues")
    public ResponseEntity<Long> getNbNotificationsNonLues(
            @RequestParam Long utilisateurId) {
        return ResponseEntity.ok(
                alerteService.getNbNotificationsNonLues(
                        utilisateurId));
    }

    /**
     * Marque une notification comme lue.
     *
     * @param id            identifiant de la notification
     * @param utilisateurId identifiant de l'utilisateur connecté
     * @return              HTTP 200 si mise à jour réussie
     */
    @PutMapping("/notifications/{id}/lue")
    public ResponseEntity<Void> marquerCommeLue(
            @PathVariable Long id,
            @RequestParam Long utilisateurId) {
        alerteService.marquerCommeLue(id, utilisateurId);
        return ResponseEntity.ok().build();
    }

    /**
     * Marque toutes les notifications d'un utilisateur comme lues.
     *
     * @param utilisateurId identifiant de l'utilisateur connecté
     * @return              HTTP 200 si mise à jour réussie
     */
    @PutMapping("/notifications/toutes-lues")
    public ResponseEntity<Void> marquerToutesCommeLues(
            @RequestParam Long utilisateurId) {
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
    public ResponseEntity<Long> getNbAlertesNonLues(
            @PathVariable Long promotionId,
            @RequestParam Long anneeAcademiqueId) {
        return ResponseEntity.ok(
                alerteService.getNbAlertesNonLues(
                        promotionId, anneeAcademiqueId));
    }
}