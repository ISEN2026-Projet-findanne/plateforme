package org.harold.plateforme.mapper;

import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.matiere.GroupeMatieresDTO;
import org.harold.plateforme.entity.GroupeMatieres;
import org.springframework.stereotype.Component;

/**
 * Mapper pour la conversion entre l'entité GroupeMatieres
 * et ses DTOs.
 *
 * @author Harold
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class GroupeMatieresMapper {

    /**
     * Convertit une entité GroupeMatieres en GroupeMatieresDTO.
     *
     * <p>Extrait les informations du semestre et de la classe
     * depuis les relations JPA.</p>
     *
     * @param groupeMatieres    l'entité à convertir
     * @return                  le DTO correspondant
     */
    public GroupeMatieresDTO toDTO(GroupeMatieres groupeMatieres) {
        GroupeMatieresDTO dto = new GroupeMatieresDTO();
        dto.setId(groupeMatieres.getId());
        dto.setNom(groupeMatieres.getNom());
        dto.setCoefficient(groupeMatieres.getCoefficient());

        if (groupeMatieres.getSemestre() != null) {
            dto.setSemestreId(groupeMatieres.getSemestre().getId());
            dto.setNumeroSemestre(
                    groupeMatieres.getSemestre().getNumero());

            if (groupeMatieres.getSemestre().getClasse() != null) {
                dto.setClasseId(
                        groupeMatieres.getSemestre().getClasse().getId());
                dto.setClasseNom(
                        groupeMatieres.getSemestre().getClasse().getNom());
            }
        }

        return dto;
    }
}