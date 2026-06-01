package org.harold.plateforme.repository;

import org.harold.plateforme.entity.Note;
import org.harold.plateforme.entity.Note.TypeNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {

    // Toutes les notes d'un étudiant pour une année académique
    List<Note> findByEtudiantIdAndAnneeAcademiqueId(
            Long etudiantId,
            Long anneeAcademiqueId);

    // Toutes les notes d'un étudiant pour une matière et une année
    List<Note> findByEtudiantIdAndMatiereIdAndAnneeAcademiqueId(
            Long etudiantId,
            Long matiereId,
            Long anneeAcademiqueId);

    // Note précise d'un étudiant pour une matière, un type et une année
    Optional<Note> findByEtudiantIdAndMatiereIdAndAnneeAcademiqueIdAndType(
            Long etudiantId,
            Long matiereId,
            Long anneeAcademiqueId,
            TypeNote type);

    // Toutes les notes d'une matière pour une année académique
    List<Note> findByMatiereIdAndAnneeAcademiqueId(
            Long matiereId,
            Long anneeAcademiqueId);

    // Toutes les notes d'un groupe classe pour une matière et une année
    List<Note> findByGroupeClasseIdAndMatiereIdAndAnneeAcademiqueId(
            Long groupeClasseId,
            Long matiereId,
            Long anneeAcademiqueId);

    // Vérifier si une note existe déjà
    boolean existsByEtudiantIdAndMatiereIdAndAnneeAcademiqueIdAndType(
            Long etudiantId,
            Long matiereId,
            Long anneeAcademiqueId,
            TypeNote type);

    // Toutes les notes de rattrapage d'une matière pour une année
    List<Note> findByMatiereIdAndAnneeAcademiqueIdAndEstRattrapageTrue(
            Long matiereId,
            Long anneeAcademiqueId);

    // Toutes les notes d'une promotion pour une année académique
    // Navigation depuis Inscription → plus propre et plus fiable
    @Query("SELECT n FROM Note n " +
            "WHERE n.etudiant.id IN (" +
            "   SELECT i.etudiant.id FROM Inscription i " +
            "   WHERE i.promotion.id = :promotionId " +
            "   AND i.anneeAcademique.id = :anneeAcademiqueId " +
            "   AND i.actif = true" +
            ") AND n.anneeAcademique.id = :anneeAcademiqueId")
    List<Note> findByPromotionIdAndAnneeAcademiqueId(
            @Param("promotionId") Long promotionId,
            @Param("anneeAcademiqueId") Long anneeAcademiqueId);
}