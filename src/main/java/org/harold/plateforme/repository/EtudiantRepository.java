package org.harold.plateforme.repository;

import org.harold.plateforme.entity.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {

    // Trouver un étudiant par son numéro étudiant
    Optional<Etudiant> findByNumeroEtudiant(String numeroEtudiant);

    // Vérifier si un numéro étudiant existe déjà
    boolean existsByNumeroEtudiant(String numeroEtudiant);

    // Vérifier si un email existe déjà
    boolean existsByEmail(String email);

    // Recherche par nom ou prénom insensible à la casse
    List<Etudiant> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(
            String nom, String prenom);

    // Tous les étudiants d'une promotion pour une année académique
    // Navigation depuis Inscription → plus propre et plus fiable
    @Query("SELECT i.etudiant FROM Inscription i " +
            "WHERE i.promotion.id = :promotionId " +
            "AND i.anneeAcademique.id = :anneeAcademiqueId " +
            "AND i.actif = true")
    List<Etudiant> findByPromotionAndAnneeAcademique(
            @Param("promotionId") Long promotionId,
            @Param("anneeAcademiqueId") Long anneeAcademiqueId);

    // Tous les étudiants d'un groupe classe
    // Navigation depuis EtudiantGroupe → plus propre
    @Query("SELECT eg.etudiant FROM EtudiantGroupe eg " +
            "WHERE eg.groupeClasse.id = :groupeClasseId")
    List<Etudiant> findByGroupeClasse(
            @Param("groupeClasseId") Long groupeClasseId);
}