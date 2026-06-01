package org.harold.plateforme.mapper;

import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.matiere.TypeEvaluationDTO;
import org.harold.plateforme.entity.TypeEvaluation;
import org.springframework.stereotype.Component;

/**
 * Mapper pour la conversion entre l'entité TypeEvaluation
 * et ses DTOs.
 *
 * @author Harold
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class TypeEvaluationMapper {

    /**
     * Convertit une entité TypeEvaluation en TypeEvaluationDTO.
     *
     * @param typeEvaluation    l'entité à convertir
     * @return                  le DTO correspondant
     */
    public TypeEvaluationDTO toDTO(TypeEvaluation typeEvaluation) {
        TypeEvaluationDTO dto = new TypeEvaluationDTO();
        dto.setId(typeEvaluation.getId());
        dto.setType(typeEvaluation.getType().name());
        dto.setCoefficient(typeEvaluation.getCoefficient());
        dto.setLibelle(typeEvaluation.getLibelle());

        if (typeEvaluation.getMatiere() != null) {
            dto.setMatiereId(typeEvaluation.getMatiere().getId());
            dto.setMatiereNom(typeEvaluation.getMatiere().getNom());
        }

        return dto;
    }
}