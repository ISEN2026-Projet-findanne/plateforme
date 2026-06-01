package org.harold.plateforme.repository;

import org.harold.plateforme.entity.Inscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InscriptionRepository extends JpaRepository<Inscription, Long> {

    Optional<Inscription> findByEtudiantIdAndActifTrue(Long etudiantId);

    List<Inscription> findByEtudiantIdOrderByDateInscriptionDesc(Long etudiantId);

    List<Inscription> findByPromotionIdAndAnneeAcademiqueIdAndActifTrue(
            Long promotionId,
            Long anneeAcademiqueId);

    boolean existsByEtudiantIdAndPromotionIdAndAnneeAcademiqueId(
            Long etudiantId,
            Long promotionId,
            Long anneeAcademiqueId);

    @Query("SELECT i FROM Inscription i " +
            "WHERE i.anneeAcademique.id = :anneeAcademiqueId " +
            "AND i.actif = true")
    List<Inscription> findActivesByAnneeAcademique(
            @Param("anneeAcademiqueId") Long anneeAcademiqueId);
    List<Inscription> findByPromotionId(Long promotionId);
}