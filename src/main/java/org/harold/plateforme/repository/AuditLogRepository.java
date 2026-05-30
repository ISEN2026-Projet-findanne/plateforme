package org.harold.plateforme.repository;

import org.harold.plateforme.entity.AuditLog;
import org.harold.plateforme.entity.AuditLog.TypeAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    // Tous les logs d'un utilisateur
    List<AuditLog> findByUtilisateurIdOrderByCreatedAtDesc(Long utilisateurId);

    // Tous les logs d'une entité précise
    List<AuditLog> findByEntiteAndEntiteIdOrderByCreatedAtDesc(
            String entite,
            Long entiteId);

    // Tous les logs par type d'action
    List<AuditLog> findByActionOrderByCreatedAtDesc(TypeAction action);

    // Tous les logs d'un utilisateur pour une action précise
    List<AuditLog> findByUtilisateurIdAndActionOrderByCreatedAtDesc(
            Long utilisateurId,
            TypeAction action);

    // Tous les logs entre deux dates
    @Query("SELECT a FROM AuditLog a " +
            "WHERE a.createdAt BETWEEN :dateDebut AND :dateFin " +
            "ORDER BY a.createdAt DESC")
    List<AuditLog> findByPeriode(
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin);

    // Tous les logs d'un utilisateur entre deux dates
    @Query("SELECT a FROM AuditLog a " +
            "WHERE a.utilisateur.id = :utilisateurId " +
            "AND a.createdAt BETWEEN :dateDebut AND :dateFin " +
            "ORDER BY a.createdAt DESC")
    List<AuditLog> findByUtilisateurIdAndPeriode(
            @Param("utilisateurId") Long utilisateurId,
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin);

    // Tous les logs d'une entité par type d'action
    List<AuditLog> findByEntiteAndActionOrderByCreatedAtDesc(
            String entite,
            TypeAction action);
}