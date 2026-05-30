package org.harold.plateforme.repository;

import org.harold.plateforme.entity.TypeEvaluation;
import org.harold.plateforme.entity.TypeEvaluation.TypeEval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TypeEvaluationRepository extends JpaRepository<TypeEvaluation, Long> {

    // Tous les types d'évaluation d'une matière
    List<TypeEvaluation> findByMatiereId(Long matiereId);

    // Un type d'évaluation précis pour une matière
    Optional<TypeEvaluation> findByMatiereIdAndType(Long matiereId, TypeEval type);

    // Vérifier si un type d'évaluation existe déjà pour une matière
    boolean existsByMatiereIdAndType(Long matiereId, TypeEval type);
}