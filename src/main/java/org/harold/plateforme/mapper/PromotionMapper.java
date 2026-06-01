package org.harold.plateforme.mapper;

import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.promotion.PromotionCreateDTO;
import org.harold.plateforme.dto.promotion.PromotionDTO;
import org.harold.plateforme.dto.promotion.PromotionUpdateDTO;
import org.harold.plateforme.entity.Promotion;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Mapper pour la conversion entre l'entité Promotion et ses DTOs.
 *
 * @author Harold
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class PromotionMapper {

    private final ModelMapper modelMapper;

    /**
     * Convertit une entité Promotion en PromotionDTO.
     *
     * @param promotion     l'entité à convertir
     * @param nbEtudiants   le nombre d'étudiants actifs dans la promotion
     * @return              le DTO correspondant
     */
    public PromotionDTO toDTO(Promotion promotion, Integer nbEtudiants) {
        PromotionDTO dto = modelMapper.map(promotion, PromotionDTO.class);
        dto.setNbEtudiants(nbEtudiants);
        return dto;
    }

    /**
     * Convertit un PromotionCreateDTO en entité Promotion.
     *
     * @param dto   le DTO de création
     * @return      la nouvelle entité Promotion
     */
    public Promotion toEntity(PromotionCreateDTO dto) {
        return modelMapper.map(dto, Promotion.class);
    }

    /**
     * Met à jour une entité Promotion existante depuis un PromotionUpdateDTO.
     *
     * @param dto           le DTO de modification
     * @param promotion     l'entité existante à mettre à jour
     */
    public void updateEntity(PromotionUpdateDTO dto, Promotion promotion) {
        promotion.setNom(dto.getNom());
        promotion.setFiliere(dto.getFiliere());
        promotion.setDescription(dto.getDescription());
    }
}