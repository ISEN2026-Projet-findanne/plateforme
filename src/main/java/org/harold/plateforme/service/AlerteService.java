package org.harold.plateforme.service;

import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.alerte.AlerteDTO;
import org.harold.plateforme.dto.alerte.NotificationDTO;
import org.harold.plateforme.entity.Alerte;
import org.harold.plateforme.entity.Inscription;
import org.harold.plateforme.entity.Notification;
import org.harold.plateforme.exception.ResourceNotFoundException;
import org.harold.plateforme.mapper.AlerteMapper;
import org.harold.plateforme.mapper.NotificationMapper;
import org.harold.plateforme.repository.AlerteRepository;
import org.harold.plateforme.repository.InscriptionRepository;
import org.harold.plateforme.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de gestion des alertes et notifications.
 *
 * <p>Gère la consultation et la mise à jour des alertes
 * et notifications. Utilisé par le polling REST V1
 * pour notifier les enseignants et responsables
 * des étudiants à risque.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class AlerteService {

    private final NotificationRepository notificationRepository;
    private final AlerteRepository alerteRepository;
    private final InscriptionRepository inscriptionRepository;
    private final AlerteMapper alerteMapper;
    private final NotificationMapper notificationMapper;

    // ===== NOTIFICATIONS =====

    /**
     * Récupère toutes les notifications d'un utilisateur.
     *
     * <p>Utilisé par le polling REST V1 pour afficher
     * les notifications dans la cloche du header React.</p>
     *
     * @param utilisateurId     identifiant de l'utilisateur
     * @return                  liste des notifications triées
     *                          par date décroissante
     */
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotifications(
            Long utilisateurId) {
        return notificationRepository
                .findByUtilisateurIdOrderByCreatedAtDesc(utilisateurId)
                .stream()
                .map(n -> notificationMapper.toDTO(
                        n, getInscription(n)))
                .collect(Collectors.toList());
    }

    /**
     * Récupère les notifications non lues d'un utilisateur.
     *
     * @param utilisateurId     identifiant de l'utilisateur
     * @return                  liste des notifications non lues
     */
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsNonLues(
            Long utilisateurId) {
        return notificationRepository
                .findByUtilisateurIdAndLueFalseOrderByCreatedAtDesc(
                        utilisateurId)
                .stream()
                .map(n -> notificationMapper.toDTO(
                        n, getInscription(n)))
                .collect(Collectors.toList());
    }

    /**
     * Récupère le nombre de notifications non lues.
     *
     * <p>Utilisé pour afficher le badge sur la cloche
     * du header React.</p>
     *
     * @param utilisateurId     identifiant de l'utilisateur
     * @return                  le nombre de notifications non lues
     */
    @Transactional(readOnly = true)
    public Long getNbNotificationsNonLues(Long utilisateurId) {
        return notificationRepository
                .countByUtilisateurIdAndLueFalse(utilisateurId);
    }

    /**
     * Marque une notification comme lue.
     *
     * @param notificationId    identifiant de la notification
     * @param utilisateurId     identifiant de l'utilisateur
     * @throws ResourceNotFoundException    si la notification
     *                                      n'existe pas
     */
    @Transactional
    public void marquerCommeLue(
            Long notificationId,
            Long utilisateurId) {

        Notification notification = notificationRepository
                .findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Notification", "id", notificationId));

        if (!notification.isLue()) {
            notification.setLue(true);
            notification.setLueLe(LocalDateTime.now());
            notificationRepository.save(notification);
        }
    }

    /**
     * Marque toutes les notifications d'un utilisateur comme lues.
     *
     * @param utilisateurId     identifiant de l'utilisateur
     */
    @Transactional
    public void marquerToutesCommeLues(Long utilisateurId) {
        notificationRepository
                .markAllAsReadByUtilisateurId(utilisateurId);
    }

    // ===== ALERTES =====

    /**
     * Récupère les alertes élevées et critiques d'une promotion.
     *
     * <p>Utilisé dans le dashboard du responsable pédagogique
     * pour afficher les étudiants prioritaires.</p>
     *
     * @param promotionId       identifiant de la promotion
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  liste des alertes triées
     *                          par score décroissant
     */
    @Transactional(readOnly = true)
    public List<AlerteDTO> getAlertesCritiques(
            Long promotionId,
            Long anneeAcademiqueId) {
        return alerteRepository
                .findEleveesCritiquesByPromotionIdAndAnneeAcademiqueId(
                        promotionId, anneeAcademiqueId)
                .stream()
                .map(a -> alerteMapper.toDTO(
                        a, getInscriptionParAlerte(a)))
                .collect(Collectors.toList());
    }

    /**
     * Récupère le nombre d'alertes non lues d'une promotion.
     *
     * @param promotionId       identifiant de la promotion
     * @param anneeAcademiqueId identifiant de l'année académique
     * @return                  le nombre d'alertes non lues
     */
    @Transactional(readOnly = true)
    public Long getNbAlertesNonLues(
            Long promotionId,
            Long anneeAcademiqueId) {
        return alerteRepository
                .countNonLuesByPromotionIdAndAnneeAcademiqueId(
                        promotionId, anneeAcademiqueId);
    }

    // ===== MÉTHODES PRIVÉES =====

    /**
     * Récupère l'inscription active de l'étudiant
     * lié à une notification.
     *
     * @param notification  la notification
     * @return              l'inscription active ou null
     */
    private Inscription getInscription(Notification notification) {
        if (notification.getAlerte() == null ||
                notification.getAlerte().getEtudiant() == null) {
            return null;
        }
        return inscriptionRepository
                .findByEtudiantIdAndActifTrue(
                        notification.getAlerte()
                                .getEtudiant().getId())
                .orElse(null);
    }

    /**
     * Récupère l'inscription active de l'étudiant
     * lié à une alerte.
     *
     * @param alerte    l'alerte
     * @return          l'inscription active ou null
     */
    private Inscription getInscriptionParAlerte(Alerte alerte) {
        if (alerte.getEtudiant() == null) return null;
        return inscriptionRepository
                .findByEtudiantIdAndActifTrue(
                        alerte.getEtudiant().getId())
                .orElse(null);
    }
}