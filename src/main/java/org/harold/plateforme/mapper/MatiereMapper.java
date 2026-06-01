package org.harold.plateforme.mapper;

import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.matiere.MatiereCreateDTO;
import org.harold.plateforme.dto.matiere.MatiereDTO;
import org.harold.plateforme.dto.matiere.MatiereUpdateDTO;
import org.harold.plateforme.entity.Matiere;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Mapper pour la conversion entre l'entité Matiere et ses DTOs.
 *
 * @author Harold
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class MatiereMapper {

    private final ModelMapper modelMapper;

    /**
     * Convertit une entité Matiere en MatiereDTO.
     *
     * <p>Inclut le nom et l'identifiant du groupe de matières
     * auquel appartient la matière.</p>
     *
     * @param matiere   l'entité à convertir
     * @return          le DTO correspondant
     */
    public MatiereDTO toDTO(Matiere matiere) {
        MatiereDTO dto = modelMapper.map(matiere, MatiereDTO.class);
        if (matiere.getGroupeMatieres() != null) {
            dto.setGroupeMatieresId(matiere.getGroupeMatieres().getId());
            dto.setGroupeMatieresNom(matiere.getGroupeMatieres().getNom());
        }
        return dto;
    }

    /**
     * Convertit un MatiereCreateDTO en entité Matiere.
     *
     * <p>Le groupe de matières n'est pas mappé ici car il nécessite
     * une recherche en base — cette association est faite
     * dans le MatiereService.</p>
     *
     * @param dto   le DTO de création
     * @return      la nouvelle entité Matiere sans groupeMatieres
     */
    public Matiere toEntity(MatiereCreateDTO dto) {
        Matiere matiere = new Matiere();
        matiere.setNom(dto.getNom());
        matiere.setCode(dto.getCode());
        matiere.setCoefficient(dto.getCoefficient());
        matiere.setATp(dto.isATp());
        matiere.setACm(dto.isACm());
        return matiere;
    }
    /**
     * Met à jour une entité Matiere existante depuis un MatiereUpdateDTO.
     *
     * <p>Le code et le groupe de matières ne sont pas modifiables
     * après création.</p>
     *
     * @param dto       le DTO de modification
     * @param matiere   l'entité existante à mettre à jour
     */
    public void updateEntity(MatiereUpdateDTO dto, Matiere matiere) {
        matiere.setNom(dto.getNom());
        matiere.setCoefficient(dto.getCoefficient());
        matiere.setATp(dto.getATp());  // Boolean avec majuscule → getATp()
        matiere.setACm(dto.getACm());  // Boolean avec majuscule → getACm()
    }
}