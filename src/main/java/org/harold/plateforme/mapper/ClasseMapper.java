package org.harold.plateforme.mapper;

import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.classe.ClasseCreateDTO;
import org.harold.plateforme.dto.classe.ClasseDTO;
import org.harold.plateforme.entity.Classe;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Mapper pour la conversion entre l'entité Classe et ses DTOs.
 *
 * @author Harold
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class ClasseMapper {

    private final ModelMapper modelMapper;

    /**
     * Convertit une entité Classe en ClasseDTO.
     *
     * @param classe    l'entité à convertir
     * @return          le DTO correspondant
     */
    public ClasseDTO toDTO(Classe classe) {
        return modelMapper.map(classe, ClasseDTO.class);
    }

    /**
     * Convertit un ClasseCreateDTO en entité Classe.
     *
     * @param dto   le DTO de création
     * @return      la nouvelle entité Classe
     */
    public Classe toEntity(ClasseCreateDTO dto) {
        return modelMapper.map(dto, Classe.class);
    }

    /**
     * Met à jour une entité Classe existante depuis un ClasseCreateDTO.
     *
     * <p>On réutilise ClasseCreateDTO pour la modification
     * car les champs modifiables sont les mêmes que
     * ceux de la création.</p>
     *
     * @param dto       le DTO de modification
     * @param classe    l'entité existante à mettre à jour
     */
    public void updateEntity(ClasseCreateDTO dto, Classe classe) {
        classe.setNom(dto.getNom());
        classe.setNiveau(dto.getNiveau());
        classe.setDescription(dto.getDescription());
    }
}