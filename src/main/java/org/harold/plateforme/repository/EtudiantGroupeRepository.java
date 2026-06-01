package org.harold.plateforme.repository;

import org.harold.plateforme.entity.EtudiantGroupe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EtudiantGroupeRepository extends JpaRepository<EtudiantGroupe, Long> {

    // Tous les groupes d'un étudiant
    List<EtudiantGroupe> findByEtudiantId(Long etudiantId);

    // Tous les étudiants d'un groupe
    List<EtudiantGroupe> findByGroupeClasseId(Long groupeClasseId);

    // Vérifier si un étudiant est déjà dans un groupe
    boolean existsByEtudiantIdAndGroupeClasseId(
            Long etudiantId,
            Long groupeClasseId);

    // Trouver l'appartenance précise d'un étudiant à un groupe
    Optional<EtudiantGroupe> findByEtudiantIdAndGroupeClasseId(
            Long etudiantId,
            Long groupeClasseId);

    // Tous les groupes d'un étudiant pour une année académique
    // LEFT JOIN FETCH pour éviter le problème N+1 de lazy loading
    @Query("SELECT eg FROM EtudiantGroupe eg " +
            "LEFT JOIN FETCH eg.groupeClasse gc " +
            "LEFT JOIN FETCH gc.anneeAcademique " +
            "WHERE eg.etudiant.id = :etudiantId " +
            "AND gc.anneeAcademique.id = :anneeAcademiqueId")
    List<EtudiantGroupe> findByEtudiantIdAndAnneeAcademiqueId(
            @Param("etudiantId") Long etudiantId,
            @Param("anneeAcademiqueId") Long anneeAcademiqueId);

    // Tous les étudiants d'une matière pour une année académique
    // Navigation depuis groupeClasse → matiere et anneeAcademique
    @Query("SELECT eg FROM EtudiantGroupe eg " +
            "LEFT JOIN FETCH eg.etudiant " +
            "WHERE eg.groupeClasse.matiere.id = :matiereId " +
            "AND eg.groupeClasse.anneeAcademique.id = :anneeAcademiqueId")
    List<EtudiantGroupe> findByMatiereIdAndAnneeAcademiqueId(
            @Param("matiereId") Long matiereId,
            @Param("anneeAcademiqueId") Long anneeAcademiqueId);
}