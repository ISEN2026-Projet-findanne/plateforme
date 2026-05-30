package org.harold.plateforme.repository;

import org.harold.plateforme.entity.MappingCsv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MappingCsvRepository extends JpaRepository<MappingCsv, Long> {

    // Trouver un mapping par colonne source
    Optional<MappingCsv> findByColonneSource(String colonneSource);

    // Trouver un mapping par colonne source et contexte
    Optional<MappingCsv> findByColonneSourceAndContexte(
            String colonneSource,
            String contexte);

    // Tous les mappings d'un contexte précis
    List<MappingCsv> findByContexte(String contexte);

    // Vérifier si un mapping existe déjà
    boolean existsByColonneSourceAndContexte(
            String colonneSource,
            String contexte);

    // Les mappings les plus utilisés pour un contexte
    @Query("SELECT m FROM MappingCsv m " +
            "WHERE m.contexte = :contexte " +
            "ORDER BY m.nbUtilisations DESC")
    List<MappingCsv> findMostUsedByContexte(
            @Param("contexte") String contexte);

    // Recherche par colonne source insensible à la casse
    @Query("SELECT m FROM MappingCsv m " +
            "WHERE LOWER(m.colonneSource) = LOWER(:colonneSource)")
    Optional<MappingCsv> findByColonneSourceIgnoreCase(
            @Param("colonneSource") String colonneSource);
}