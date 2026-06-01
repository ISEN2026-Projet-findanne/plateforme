package org.harold.plateforme.repository;

import org.harold.plateforme.entity.ScoreRisqueHistorique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScoreRisqueHistoriqueRepository
        extends JpaRepository<ScoreRisqueHistorique, Long> {

    // Tout l'historique d'un étudiant pour une année trié par date
    List<ScoreRisqueHistorique> findByEtudiantIdAndAnneeAcademiqueIdOrderByCalculeLeDesc(
            Long etudiantId,
            Long anneeAcademiqueId);

    // Dernier score calculé d'un étudiant pour une année
    Optional<ScoreRisqueHistorique> findTopByEtudiantIdAndAnneeAcademiqueIdOrderByCalculeLeDesc(
            Long etudiantId,
            Long anneeAcademiqueId);

    // Tout l'historique d'un étudiant toutes années confondues
    List<ScoreRisqueHistorique> findByEtudiantIdOrderByCalculeLeDesc(
            Long etudiantId);

    // Tous les scores d'une promotion pour une année
    // Navigation depuis Inscription → évite les jointures sur objets
    @Query("SELECT srh FROM ScoreRisqueHistorique srh " +
            "WHERE srh.etudiant.id IN (" +
            "   SELECT i.etudiant.id FROM Inscription i " +
            "   WHERE i.promotion.id = :promotionId " +
            "   AND i.actif = true" +
            ") AND srh.anneeAcademique.id = :anneeAcademiqueId " +
            "ORDER BY srh.scoreGlobal DESC")
    List<ScoreRisqueHistorique> findLatestByPromotionIdAndAnneeAcademiqueId(
            @Param("promotionId") Long promotionId,
            @Param("anneeAcademiqueId") Long anneeAcademiqueId);

    // Étudiants à risque au dessus d'un seuil pour une promotion
    @Query("SELECT srh FROM ScoreRisqueHistorique srh " +
            "WHERE srh.etudiant.id IN (" +
            "   SELECT i.etudiant.id FROM Inscription i " +
            "   WHERE i.promotion.id = :promotionId " +
            "   AND i.actif = true" +
            ") AND srh.anneeAcademique.id = :anneeAcademiqueId " +
            "AND srh.scoreGlobal >= :seuilRisque " +
            "ORDER BY srh.scoreGlobal DESC")
    List<ScoreRisqueHistorique> findAtRiskByPromotionIdAndAnneeAcademiqueId(
            @Param("promotionId") Long promotionId,
            @Param("anneeAcademiqueId") Long anneeAcademiqueId,
            @Param("seuilRisque") Double seuilRisque);
}