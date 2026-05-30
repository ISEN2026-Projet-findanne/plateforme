package org.harold.plateforme.repository;

import org.harold.plateforme.entity.ScoreRisqueHistorique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScoreRisqueHistoriqueRepository extends JpaRepository<ScoreRisqueHistorique, Long> {

    // Tout l'historique d'un étudiant pour une année académique
    List<ScoreRisqueHistorique> findByEtudiantIdAndAnneeAcademiqueIdOrderByCalculeLeDesc(
            Long etudiantId,
            Long anneeAcademiqueId);

    // Dernier score calculé d'un étudiant pour une année académique
    Optional<ScoreRisqueHistorique> findTopByEtudiantIdAndAnneeAcademiqueIdOrderByCalculeLeDesc(
            Long etudiantId,
            Long anneeAcademiqueId);

    // Tout l'historique d'un étudiant toutes années confondues
    List<ScoreRisqueHistorique> findByEtudiantIdOrderByCalculeLeDesc(
            Long etudiantId);

    // Tous les scores d'une promotion pour une année académique
    @Query("SELECT srh FROM ScoreRisqueHistorique srh " +
            "JOIN Inscription i ON i.etudiant = srh.etudiant " +
            "WHERE i.promotion.id = :promotionId " +
            "AND srh.anneeAcademique.id = :anneeAcademiqueId " +
            "AND i.actif = true " +
            "ORDER BY srh.scoreGlobal DESC")
    List<ScoreRisqueHistorique> findLatestByPromotionIdAndAnneeAcademiqueId(
            @Param("promotionId") Long promotionId,
            @Param("anneeAcademiqueId") Long anneeAcademiqueId);

    // Étudiants à risque élevé ou critique d'une promotion pour une année
    @Query("SELECT srh FROM ScoreRisqueHistorique srh " +
            "JOIN Inscription i ON i.etudiant = srh.etudiant " +
            "WHERE i.promotion.id = :promotionId " +
            "AND srh.anneeAcademique.id = :anneeAcademiqueId " +
            "AND i.actif = true " +
            "AND srh.scoreGlobal >= :seuilRisque " +
            "ORDER BY srh.scoreGlobal DESC")
    List<ScoreRisqueHistorique> findAtRiskByPromotionIdAndAnneeAcademiqueId(
            @Param("promotionId") Long promotionId,
            @Param("anneeAcademiqueId") Long anneeAcademiqueId,
            @Param("seuilRisque") Double seuilRisque);
}