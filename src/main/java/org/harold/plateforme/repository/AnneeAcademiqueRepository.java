package org.harold.plateforme.repository;

import org.harold.plateforme.entity.AnneeAcademique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnneeAcademiqueRepository extends JpaRepository<AnneeAcademique, Long> {

    Optional<AnneeAcademique> findByAnnee(String annee);

    Optional<AnneeAcademique> findByActiveTrue();

    boolean existsByAnnee(String annee);
}