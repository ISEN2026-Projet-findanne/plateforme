package org.harold.plateforme.repository;

import org.harold.plateforme.entity.GroupeClasse;
import org.harold.plateforme.entity.GroupeClasse.TypeGroupe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupeClasseRepository extends JpaRepository<GroupeClasse, Long> {

    // Tous les groupes d'une matière pour une année académique
    List<GroupeClasse> findByMatiereIdAndAnneeAcademiqueId(
            Long matiereId,
            Long anneeAcademiqueId);

    // Un groupe précis par nom, matière et année académique
    Optional<GroupeClasse> findByNomAndMatiereIdAndAnneeAcademiqueId(
            String nom,
            Long matiereId,
            Long anneeAcademiqueId);

    // Tous les groupes d'une matière par type (TD, TP, CM)
    List<GroupeClasse> findByMatiereIdAndAnneeAcademiqueIdAndType(
            Long matiereId,
            Long anneeAcademiqueId,
            TypeGroupe type);

    // Vérifier si un groupe existe déjà pour une matière et une année
    boolean existsByNomAndMatiereIdAndAnneeAcademiqueId(
            String nom,
            Long matiereId,
            Long anneeAcademiqueId);

    // Tous les groupes d'une année académique
    List<GroupeClasse> findByAnneeAcademiqueId(Long anneeAcademiqueId);

    // Tous les groupes d'une promotion pour une année via les matières
    @Query("SELECT gc FROM GroupeClasse gc " +
            "WHERE gc.anneeAcademique.id = :anneeAcademiqueId " +
            "AND gc.matiere.groupeMatieres.semestre.classe.id = :classeId")
    List<GroupeClasse> findByClasseIdAndAnneeAcademiqueId(
            @Param("classeId") Long classeId,
            @Param("anneeAcademiqueId") Long anneeAcademiqueId);
}