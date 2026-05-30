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

    // Moyennes par matière pour une promotion et une année
    @Query("SELECT AVG(n.valeur) FROM Note n " +
            "JOIN Inscription i ON i.etudiant = n.etudiant " +
            "WHERE n.matiere.id = :matiereId " +
            "AND n.anneeAcademique.id = :anneeAcademiqueId " +
            "AND i.promotion.id = :promotionId " +
            "AND i.actif = true " +
            "AND n.type != 'RATTRAPAGE'")
    Double findMoyenneByMatiereIdAndPromotionIdAndAnneeAcademiqueId(
            @Param("matiereId") Long matiereId,
            @Param("promotionId") Long promotionId,
            @Param("anneeAcademiqueId") Long anneeAcademiqueId);

    // Toutes les notes d'une promotion pour une année académique
    @Query("SELECT n FROM Note n " +
            "JOIN Inscription i ON i.etudiant = n.etudiant " +
            "WHERE i.promotion.id = :promotionId " +
            "AND n.anneeAcademique.id = :anneeAcademiqueId " +
            "AND i.actif = true")
    List<Note> findByPromotionIdAndAnneeAcademiqueId(
            @Param("promotionId") Long promotionId,
            @Param("anneeAcademiqueId") Long anneeAcademiqueId);
}