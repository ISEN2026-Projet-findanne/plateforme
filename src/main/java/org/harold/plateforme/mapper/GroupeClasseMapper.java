package org.harold.plateforme.mapper;

import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.groupe.GroupeClasseDTO;
import org.harold.plateforme.entity.GroupeClasse;
import org.springframework.stereotype.Component;

/**
 * Mapper pour la conversion entre l'entité GroupeClasse
 * et ses DTOs.
 *
 * @author Harold
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class GroupeClasseMapper {

    /**
     * Convertit une entité GroupeClasse en GroupeClasseDTO.
     *
     * @param groupeClasse      l'entité à convertir
     * @param nbEtudiants       nombre d'étudiants dans le groupe
     * @return                  le DTO correspondant
     */
    public GroupeClasseDTO toDTO(
            GroupeClasse groupeClasse,
            Integer nbEtudiants) {

        GroupeClasseDTO dto = new GroupeClasseDTO();
        dto.setId(groupeClasse.getId());
        dto.setNom(groupeClasse.getNom());
        dto.setType(groupeClasse.getType().name());
        dto.setCapacite(groupeClasse.getCapacite());
        dto.setNbEtudiants(nbEtudiants);

        if (groupeClasse.getMatiere() != null) {
            dto.setMatiereId(groupeClasse.getMatiere().getId());
            dto.setMatiereNom(groupeClasse.getMatiere().getNom());
        }

        if (groupeClasse.getAnneeAcademique() != null) {
            dto.setAnneeAcademiqueId(
                    groupeClasse.getAnneeAcademique().getId());
            dto.setAnneeAcademique(
                    groupeClasse.getAnneeAcademique().getAnnee());
        }

        return dto;
    }
}