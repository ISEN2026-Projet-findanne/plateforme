package org.harold.plateforme.mapper;

import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.alerte.NotificationDTO;
import org.harold.plateforme.entity.Inscription;
import org.harold.plateforme.entity.Notification;
import org.springframework.stereotype.Component;

/**
 * Mapper pour la conversion entre l'entité Notification
 * et NotificationDTO.
 *
 * @author Harold
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class NotificationMapper {

    /**
     * Convertit une entité Notification en NotificationDTO.
     *
     * <p>Les informations de l'étudiant et de la promotion
     * sont extraites depuis l'alerte associée et l'inscription
     * active de l'étudiant.</p>
     *
     * @param notification  l'entité à convertir
     * @param inscription   l'inscription active de l'étudiant
     * @return              le DTO correspondant
     */
    public NotificationDTO toDTO(Notification notification,
                                 Inscription inscription) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setLue(notification.isLue());
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setLueLe(notification.getLueLe());

        // Informations depuis l'alerte associée
        if (notification.getAlerte() != null) {
            dto.setAlerteId(notification.getAlerte().getId());
            dto.setScoreRisque(notification.getAlerte().getScoreRisque());
            dto.setNiveauRisque(
                    notification.getAlerte().getNiveau().name());

            // Informations étudiant depuis l'alerte
            if (notification.getAlerte().getEtudiant() != null) {
                dto.setEtudiantNom(
                        notification.getAlerte().getEtudiant().getNom());
                dto.setEtudiantPrenom(
                        notification.getAlerte().getEtudiant().getPrenom());
                dto.setNumeroEtudiant(
                        notification.getAlerte().getEtudiant()
                                .getNumeroEtudiant());
            }
        }

        // Informations promotion depuis inscription
        if (inscription != null) {
            dto.setPromotionNom(inscription.getPromotion().getNom());
        }

        return dto;
    }
}