
package org.harold.plateforme.repository;

import org.harold.plateforme.entity.Matiere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatiereRepository extends JpaRepository<Matiere, Long> {

    // Trouver une matière par son code unique
    Optional<Matiere> findByCode(String code);

    // Toutes les matières d'un groupe de matières
    List<Matiere> findByGroupeMatieresId(Long groupeMatieresId);

    // Vérifier si une matière existe déjà par son code
    boolean existsByCode(String code);

    // Toutes les matières d'une classe via groupe_matieres et semestre
    @Query("SELECT m FROM Matiere m " +
            "WHERE m.groupeMatieres.semestre.classe.id = :classeId")
    List<Matiere> findByClasseId(
            @Param("classeId") Long classeId);

    // Toutes les matières d'un semestre d'une classe
    @Query("SELECT m FROM Matiere m " +
            "WHERE m.groupeMatieres.semestre.classe.id = :classeId " +
            "AND m.groupeMatieres.semestre.numero = :numeroSemestre")
    List<Matiere> findByClasseIdAndNumeroSemestre(
            @Param("classeId") Long classeId,
            @Param("numeroSemestre") Integer numeroSemestre);

    // Toutes les matières enseignées par un enseignant
    @Query("SELECT DISTINCT m FROM Matiere m " +
            "JOIN EnseignantMatiereGroupe emg ON emg.matiere = m " +
            "WHERE emg.utilisateur.id = :enseignantId")
    List<Matiere> findByEnseignantId(
            @Param("enseignantId") Long enseignantId);
}