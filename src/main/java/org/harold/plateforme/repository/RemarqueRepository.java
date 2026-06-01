package org.harold.plateforme.repository;

import org.harold.plateforme.entity.Remarque;
import org.harold.plateforme.entity.Remarque.TypeRemarque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RemarqueRepository extends JpaRepository<Remarque, Long> {

    // Toutes les remarques d'un étudiant
    List<Remarque> findByEtudiantId(Long etudiantId);

    // Toutes les remarques d'un étudiant par type
    List<Remarque> findByEtudiantIdAndType(
            Long etudiantId,
            TypeRemarque type);

    // Toutes les remarques d'un enseignant
    List<Remarque> findByEnseignantId(Long enseignantId);

    // Toutes les remarques d'un groupe classe
    List<Remarque> findByGroupeClasseId(Long groupeClasseId);

    // Toutes les remarques d'un étudiant dans un groupe classe
    List<Remarque> findByEtudiantIdAndGroupeClasseId(
            Long etudiantId,
            Long groupeClasseId);

    // Nombre de remarques d'un type précis pour un étudiant
    // et une année académique
    // Utilisation du paramètre TypeRemarque au lieu du String
    // pour éviter les erreurs de type en JPQL
    @Query("SELECT COUNT(r) FROM Remarque r " +
            "WHERE r.etudiant.id = :etudiantId " +
            "AND r.groupeClasse.anneeAcademique.id = :anneeAcademiqueId " +
            "AND r.type = :type")
    Long countByEtudiantIdAndAnneeAcademiqueIdAndType(
            @Param("etudiantId") Long etudiantId,
            @Param("anneeAcademiqueId") Long anneeAcademiqueId,
            @Param("type") TypeRemarque type);

    // Toutes les remarques d'un étudiant pour une année académique
    // triées par date décroissante
    @Query("SELECT r FROM Remarque r " +
            "WHERE r.etudiant.id = :etudiantId " +
            "AND r.groupeClasse.anneeAcademique.id = :anneeAcademiqueId " +
            "ORDER BY r.createdAt DESC")
    List<Remarque> findByEtudiantIdAndAnneeAcademiqueId(
            @Param("etudiantId") Long etudiantId,
            @Param("anneeAcademiqueId") Long anneeAcademiqueId);
}