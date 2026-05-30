package org.harold.plateforme.repository;

import org.harold.plateforme.entity.Semestre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SemestreRepository extends JpaRepository<Semestre, Long> {

    List<Semestre> findByClasseId(Long classeId);

    Optional<Semestre> findByClasseIdAndNumero(Long classeId, Integer numero);

    boolean existsByClasseIdAndNumero(Long classeId, Integer numero);
}