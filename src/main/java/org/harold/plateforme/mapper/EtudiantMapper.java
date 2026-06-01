package org.harold.plateforme.mapper;

import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.etudiant.EtudiantCreateDTO;
import org.harold.plateforme.dto.etudiant.EtudiantDTO;
import org.harold.plateforme.dto.etudiant.EtudiantUpdateDTO;
import org.harold.plateforme.entity.Etudiant;
import org.harold.plateforme.entity.Inscription;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Mapper pour la conversion entre l'entité Etudiant et ses DTOs.
 *
 * @author Harold
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class EtudiantMapper {

    private final ModelMapper modelMapper;

    /**
     * Convertit une entité Etudiant en EtudiantDTO.
     *
     * <p>Les informations de promotion, niveau et année académique
     * sont extraites de l'inscription active de l'étudiant.</p>
     *
     * @param etudiant      l'entité à convertir
     * @param inscription   l'inscription active de l'étudiant
     * @return              le DTO correspondant
     */
    public EtudiantDTO toDTO(Etudiant etudiant, Inscription inscription) {
        EtudiantDTO dto = modelMapper.map(etudiant, EtudiantDTO.class);
        if (inscription != null) {
            dto.setPromotionNom(inscription.getPromotion().getNom());
            dto.setNiveau(inscription.getNiveau());
            dto.setAnneeAcademique(
                    inscription.getAnneeAcademique().getAnnee());
        }
        return dto;
    }

    /**
     * Convertit un EtudiantCreateDTO en entité Etudiant.
     *
     * <p>L'inscription est créée séparément dans EtudiantService
     * car elle nécessite une recherche en base pour la promotion
     * et l'année académique.</p>
     *
     * @param dto   le DTO de création
     * @return      la nouvelle entité Etudiant sans inscription
     */
    public Etudiant toEntity(EtudiantCreateDTO dto) {
        Etudiant etudiant = new Etudiant();
        etudiant.setNom(dto.getNom());
        etudiant.setPrenom(dto.getPrenom());
        etudiant.setNumeroEtudiant(dto.getNumeroEtudiant());
        etudiant.setEmail(dto.getEmail());
        etudiant.setDateNaissance(dto.getDateNaissance());
        return etudiant;
    }

    /**
     * Met à jour une entité Etudiant existante depuis un EtudiantUpdateDTO.
     *
     * <p>Le numéro étudiant n'est pas modifiable après création.
     * Le changement de promotion passe par le service d'inscription.</p>
     *
     * @param dto       le DTO de modification
     * @param etudiant  l'entité existante à mettre à jour
     */
    public void updateEntity(EtudiantUpdateDTO dto, Etudiant etudiant) {
        etudiant.setNom(dto.getNom());
        etudiant.setPrenom(dto.getPrenom());
        etudiant.setEmail(dto.getEmail());
        etudiant.setDateNaissance(dto.getDateNaissance());
    }
}