package org.harold.plateforme.repository;

import org.harold.plateforme.entity.ResponsablePromotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ResponsablePromotionRepository extends JpaRepository<ResponsablePromotion, Long> {

    // Toutes les promotions d'un responsable
    List<ResponsablePromotion> findByUtilisateurId(Long utilisateurId);

    // Tous les responsables d'une promotion
    List<ResponsablePromotion> findByPromotionId(Long promotionId);

    // Responsable d'une promotion pour une année académique précise
    Optional<ResponsablePromotion> findByUtilisateurIdAndPromotionIdAndAnneeAcademiqueId(
            Long utilisateurId,
            Long promotionId,
            Long anneeAcademiqueId);

    // Vérifier si un responsable est déjà assigné à une promotion pour une année
    boolean existsByUtilisateurIdAndPromotionIdAndAnneeAcademiqueId(
            Long utilisateurId,
            Long promotionId,
            Long anneeAcademiqueId);

    // Toutes les assignations d'une promotion pour une année académique
    List<ResponsablePromotion> findByPromotionIdAndAnneeAcademiqueId(
            Long promotionId,
            Long anneeAcademiqueId);

    // Toutes les promotions gérées par un responsable pour une année académique
    @Query("SELECT rp FROM ResponsablePromotion rp " +
            "WHERE rp.utilisateur.id = :utilisateurId " +
            "AND rp.anneeAcademique.id = :anneeAcademiqueId")
    List<ResponsablePromotion> findByUtilisateurIdAndAnneeAcademiqueId(
            @Param("utilisateurId") Long utilisateurId,
            @Param("anneeAcademiqueId") Long anneeAcademiqueId);
}