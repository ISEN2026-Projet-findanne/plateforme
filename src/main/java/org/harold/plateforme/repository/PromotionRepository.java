package org.harold.plateforme.repository;

import org.harold.plateforme.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    Optional<Promotion> findByNom(String nom);

    List<Promotion> findByFiliere(String filiere);
    List<Promotion> findByNomContainingIgnoreCase(String nom);

    boolean existsByNom(String nom);
}