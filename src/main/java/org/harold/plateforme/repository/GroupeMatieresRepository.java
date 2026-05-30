package org.harold.plateforme.repository;

import org.harold.plateforme.entity.GroupeMatieres;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupeMatieresRepository extends JpaRepository<GroupeMatieres, Long> {

    // Tous les groupes de matières d'un semestre
    List<GroupeMatieres> findBySemestreId(Long semestreId);

    // Un groupe de matières précis dans un semestre
    Optional<GroupeMatieres> findBySemestreIdAndNom(Long semestreId, String nom);

    // Vérifier si un groupe de matières existe déjà dans un semestre
    boolean existsBySemestreIdAndNom(Long semestreId, String nom);

    // Tous les groupes de matières d'une classe via le semestre
    @Query("SELECT gm FROM GroupeMatieres gm " +
            "WHERE gm.semestre.classe.id = :classeId")
    List<GroupeMatieres> findByClasseId(
            @Param("classeId") Long classeId);

    // Tous les groupes de matières d'une classe pour un semestre précis
    @Query("SELECT gm FROM GroupeMatieres gm " +
            "WHERE gm.semestre.classe.id = :classeId " +
            "AND gm.semestre.numero = :numeroSemestre")
    List<GroupeMatieres> findByClasseIdAndNumeroSemestre(
            @Param("classeId") Long classeId,
            @Param("numeroSemestre") Integer numeroSemestre);
}