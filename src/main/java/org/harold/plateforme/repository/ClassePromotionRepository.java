package org.harold.plateforme.repository;

import org.harold.plateforme.entity.ClassePromotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassePromotionRepository extends JpaRepository<ClassePromotion, Long> {

    Optional<ClassePromotion> findByClasseIdAndPromotionIdAndAnneeAcademiqueId(
            Long classeId,
            Long promotionId,
            Long anneeAcademiqueId);

    List<ClassePromotion> findByPromotionId(Long promotionId);

    List<ClassePromotion> findByPromotionIdAndAnneeAcademiqueId(
            Long promotionId,
            Long anneeAcademiqueId);

    List<ClassePromotion> findByClasseId(Long classeId);

    boolean existsByClasseIdAndPromotionIdAndAnneeAcademiqueId(
            Long classeId,
            Long promotionId,
            Long anneeAcademiqueId);

    @Query("SELECT cp FROM ClassePromotion cp " +
            "WHERE cp.anneeAcademique.id = :anneeAcademiqueId")
    List<ClassePromotion> findByAnneeAcademique(
            @Param("anneeAcademiqueId") Long anneeAcademiqueId);
}