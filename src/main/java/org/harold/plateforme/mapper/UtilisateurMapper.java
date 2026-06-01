package org.harold.plateforme.mapper;

import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.utilisateur.UtilisateurCreateDTO;
import org.harold.plateforme.dto.utilisateur.UtilisateurDTO;
import org.harold.plateforme.dto.utilisateur.UtilisateurUpdateDTO;
import org.harold.plateforme.entity.Utilisateur;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Mapper pour la conversion entre l'entité Utilisateur et ses DTOs.
 *
 * @author Harold
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class UtilisateurMapper {

    private final ModelMapper modelMapper;

    /**
     * Convertit une entité Utilisateur en UtilisateurDTO.
     *
     * <p>Le mot de passe n'est jamais inclus dans le DTO
     * retourné à React.</p>
     *
     * @param utilisateur   l'entité à convertir
     * @return              le DTO correspondant sans mot de passe
     */
    public UtilisateurDTO toDTO(Utilisateur utilisateur) {
        UtilisateurDTO dto = new UtilisateurDTO();
        dto.setId(utilisateur.getId());
        dto.setNom(utilisateur.getNom());
        dto.setPrenom(utilisateur.getPrenom());
        dto.setEmail(utilisateur.getEmail());
        dto.setRole(utilisateur.getRole().name());
        dto.setActif(utilisateur.isActif());
        dto.setCreatedAt(utilisateur.getCreatedAt());
        return dto;
    }

    /**
     * Convertit un UtilisateurCreateDTO en entité Utilisateur.
     *
     * <p>Le mot de passe n'est pas hashé ici — le hashage
     * est fait dans UtilisateurService via BCryptPasswordEncoder
     * avant la sauvegarde en base.</p>
     *
     * @param dto   le DTO de création
     * @return      la nouvelle entité Utilisateur sans mot de passe hashé
     */
    public Utilisateur toEntity(UtilisateurCreateDTO dto) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(dto.getNom());
        utilisateur.setPrenom(dto.getPrenom());
        utilisateur.setEmail(dto.getEmail());
        utilisateur.setMotDePasse(dto.getMotDePasse());
        utilisateur.setRole(Utilisateur.Role.valueOf(dto.getRole()));
        utilisateur.setActif(true);
        return utilisateur;
    }

    /**
     * Met à jour une entité Utilisateur existante depuis un UtilisateurUpdateDTO.
     *
     * <p>Le mot de passe n'est pas modifiable ici.</p>
     *
     * @param dto           le DTO de modification
     * @param utilisateur   l'entité existante à mettre à jour
     */
    public void updateEntity(UtilisateurUpdateDTO dto, Utilisateur utilisateur) {
        utilisateur.setNom(dto.getNom());
        utilisateur.setPrenom(dto.getPrenom());
        utilisateur.setEmail(dto.getEmail());
        utilisateur.setRole(Utilisateur.Role.valueOf(dto.getRole()));
        utilisateur.setActif(dto.getActif());
    }
}