package org.harold.plateforme.mapper;

import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.anneeacademique.AnneeAcademiqueCreateDTO;
import org.harold.plateforme.dto.anneeacademique.AnneeAcademiqueDTO;
import org.harold.plateforme.entity.AnneeAcademique;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Mapper pour la conversion entre l'entité AnneeAcademique
 * et ses DTOs.
 *
 * @author Harold
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class AnneeAcademiqueMapper {

    private final ModelMapper modelMapper;

    /**
     * Convertit une entité AnneeAcademique en AnneeAcademiqueDTO.
     *
     * @param annee     l'entité à convertir
     * @return          le DTO correspondant
     */
    public AnneeAcademiqueDTO toDTO(AnneeAcademique annee) {
        return modelMapper.map(annee, AnneeAcademiqueDTO.class);
    }

    /**
     * Convertit un AnneeAcademiqueCreateDTO en entité AnneeAcademique.
     *
     * <p>Le champ active est mis à false par défaut —
     * l'activation se fait via AnneeAcademiqueService.activer().</p>
     *
     * @param dto   le DTO de création
     * @return      la nouvelle entité AnneeAcademique
     */
    public AnneeAcademique toEntity(AnneeAcademiqueCreateDTO dto) {
        AnneeAcademique annee = modelMapper.map(
                dto, AnneeAcademique.class);
        annee.setActive(false);
        return annee;
    }

    /**
     * Met à jour une entité AnneeAcademique existante.
     *
     * <p>Seul le libellé est modifiable après création.
     * Le champ annee (ex: 2024/2025) n'est pas modifiable.</p>
     *
     * @param dto       le DTO de modification
     * @param annee     l'entité existante à mettre à jour
     */
    public void updateEntity(
            AnneeAcademiqueCreateDTO dto,
            AnneeAcademique annee) {
        annee.setLibelle(dto.getLibelle());
    }
}