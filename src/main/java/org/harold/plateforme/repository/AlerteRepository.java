package org.harold.plateforme.repository;

import org.harold.plateforme.entity.Alerte;
import org.harold.plateforme.entity.Alerte.NiveauAlerte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AlerteRepository extends JpaRepository<Alerte, Long> {

    // Toutes les alertes d'un étudiant
    List<Alerte> findByEtudiantId(Long etudiantId);

    // Toutes les alertes d'un étudiant pour une année académique
    List<Alerte> findByEtudiantIdAndAnneeAcademiqueId(
            Long etudiantId,
            Long anneeAcademiqueId);

    // Dernière alerte d'un étudiant pour une année académique
    Optional<Alerte> findTopByEtudiantIdAndAnneeAcademiqueIdOrderByCreatedAtDesc(
            Long etudiantId,
            Long anneeAcademiqueId);

    // Toutes les alertes non lues d'un étudiant
    List<Alerte> findByEtudiantIdAndLueFalse(Long etudiantId);

    // Toutes les alertes par niveau pour une année académique
    List<Alerte> findByAnneeAcademiqueIdAndNiveau(
            Long anneeAcademiqueId,
            NiveauAlerte niveau);

    // Toutes les alertes critiques et élevées d'une promotion pour une année
    @Query("SELECT a FROM Alerte a " +
            "JOIN Inscription i ON i.etudiant = a.etudiant " +
            "WHERE i.promotion.id = :promotionId " +
            "AND a.anneeAcademique.id = :anneeAcademiqueId " +
            "AND i.actif = true " +
            "AND a.niveau IN ('ELEVE', 'CRITIQUE') " +
            "ORDER BY a.scoreRisque DESC")
    List<Alerte> findEleveesCritiquesByPromotionIdAndAnneeAcademiqueId(
            @Param("promotionId") Long promotionId,
            @Param("anneeAcademiqueId") Long anneeAcademiqueId);

    // Nombre d'alertes non lues d'une promotion pour une année
    @Query("SELECT COUNT(a) FROM Alerte a " +
            "JOIN Inscription i ON i.etudiant = a.etudiant " +
            "WHERE i.promotion.id = :promotionId " +
            "AND a.anneeAcademique.id = :anneeAcademiqueId " +
            "AND i.actif = true " +
            "AND a.lue = false")
    Long countNonLuesByPromotionIdAndAnneeAcademiqueId(
            @Param("promotionId") Long promotionId,
            @Param("anneeAcademiqueId") Long anneeAcademiqueId);
}