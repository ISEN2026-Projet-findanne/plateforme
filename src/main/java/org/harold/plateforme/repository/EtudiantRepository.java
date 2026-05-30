package org.harold.plateforme.repository;

import org.harold.plateforme.entity.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {

    Optional<Etudiant> findByNumeroEtudiant(String numeroEtudiant);

    boolean existsByNumeroEtudiant(String numeroEtudiant);

    boolean existsByEmail(String email);

    // Recherche par nom ou prénom (insensible à la casse)
    List<Etudiant> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(
            String nom, String prenom);

    // Tous les étudiants d'une promotion pour une année académique
    @Query("SELECT e FROM Etudiant e " +
            "JOIN Inscription i ON i.etudiant = e " +
            "WHERE i.promotion.id = :promotionId " +
            "AND i.anneeAcademique.id = :anneeAcademiqueId " +
            "AND i.actif = true")
    List<Etudiant> findByPromotionAndAnneeAcademique(
            @Param("promotionId") Long promotionId,
            @Param("anneeAcademiqueId") Long anneeAcademiqueId);

    // Tous les étudiants d'un groupe classe
    @Query("SELECT e FROM Etudiant e " +
            "JOIN EtudiantGroupe eg ON eg.etudiant = e " +
            "WHERE eg.groupeClasse.id = :groupeClasseId")
    List<Etudiant> findByGroupeClasse(
            @Param("groupeClasseId") Long groupeClasseId);
}