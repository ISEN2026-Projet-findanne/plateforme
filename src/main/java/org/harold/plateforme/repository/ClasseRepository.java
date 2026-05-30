package org.harold.plateforme.repository;

import org.harold.plateforme.entity.Classe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClasseRepository extends JpaRepository<Classe, Long> {

    Optional<Classe> findByNom(String nom);

    List<Classe> findByNiveau(String niveau);

    boolean existsByNom(String nom);

    List<Classe> findByNomContainingIgnoreCase(String nom);
}