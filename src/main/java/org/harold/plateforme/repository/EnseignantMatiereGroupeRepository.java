package org.harold.plateforme.repository;

import org.harold.plateforme.entity.EnseignantMatiereGroupe;
import org.harold.plateforme.entity.EnseignantMatiereGroupe.TypeEnseignement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EnseignantMatiereGroupeRepository
        extends JpaRepository<EnseignantMatiereGroupe, Long> {

    // Toutes les assignations d'un enseignant
    List<EnseignantMatiereGroupe> findByUtilisateurId(Long utilisateurId);

    // Toutes les assignations d'une matière
    List<EnseignantMatiereGroupe> findByMatiereId(Long matiereId);

    // Toutes les assignations d'un groupe classe
    List<EnseignantMatiereGroupe> findByGroupeClasseId(Long groupeClasseId);

    // Assignation précise enseignant + matière + groupe
    Optional<EnseignantMatiereGroupe> findByUtilisateurIdAndMatiereIdAndGroupeClasseId(
            Long utilisateurId,
            Long matiereId,
            Long groupeClasseId);

    // Vérifier si une assignation existe déjà
    boolean existsByUtilisateurIdAndMatiereIdAndGroupeClasseId(
            Long utilisateurId,
            Long matiereId,
            Long groupeClasseId);

    // Tous les enseignants d'une matière par type d'enseignement
    @Query("SELECT emg FROM EnseignantMatiereGroupe emg " +
            "WHERE emg.matiere.id = :matiereId " +
            "AND emg.typeEnseignement = :typeEnseignement")
    List<EnseignantMatiereGroupe> findByMatiereIdAndTypeEnseignement(
            @Param("matiereId") Long matiereId,
            @Param("typeEnseignement") TypeEnseignement typeEnseignement);

    // Toutes les matières et groupes d'un enseignant pour une année
    // LEFT JOIN FETCH pour éviter le problème N+1 de lazy loading
    @Query("SELECT emg FROM EnseignantMatiereGroupe emg " +
            "LEFT JOIN FETCH emg.matiere " +
            "LEFT JOIN FETCH emg.groupeClasse gc " +
            "LEFT JOIN FETCH gc.anneeAcademique " +
            "WHERE emg.utilisateur.id = :enseignantId " +
            "AND gc.anneeAcademique.id = :anneeAcademiqueId")
    List<EnseignantMatiereGroupe> findByEnseignantIdAndAnneeAcademiqueId(
            @Param("enseignantId") Long enseignantId,
            @Param("anneeAcademiqueId") Long anneeAcademiqueId);
}