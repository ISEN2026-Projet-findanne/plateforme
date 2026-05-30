package org.harold.plateforme.repository;

import org.harold.plateforme.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Toutes les notifications d'un utilisateur
    List<Notification> findByUtilisateurIdOrderByCreatedAtDesc(Long utilisateurId);

    // Toutes les notifications non lues d'un utilisateur
    List<Notification> findByUtilisateurIdAndLueFalseOrderByCreatedAtDesc(
            Long utilisateurId);

    // Nombre de notifications non lues d'un utilisateur
    Long countByUtilisateurIdAndLueFalse(Long utilisateurId);

    // Vérifier si une notification existe déjà pour un utilisateur et une alerte
    boolean existsByUtilisateurIdAndAlerteId(
            Long utilisateurId,
            Long alerteId);

    // Marquer toutes les notifications d'un utilisateur comme lues
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.lue = true, n.lueLe = CURRENT_TIMESTAMP " +
            "WHERE n.utilisateur.id = :utilisateurId AND n.lue = false")
    void markAllAsReadByUtilisateurId(
            @Param("utilisateurId") Long utilisateurId);

    // Toutes les notifications d'un utilisateur liées à une alerte
    List<Notification> findByUtilisateurIdAndAlerteId(
            Long utilisateurId,
            Long alerteId);
}