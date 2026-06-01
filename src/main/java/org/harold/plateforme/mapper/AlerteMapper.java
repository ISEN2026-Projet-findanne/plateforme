package org.harold.plateforme.mapper;

import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.alerte.AlerteDTO;
import org.harold.plateforme.entity.Alerte;
import org.harold.plateforme.entity.Inscription;
import org.springframework.stereotype.Component;

/**
 * Mapper pour la conversion entre l'entité Alerte et AlerteDTO.
 *
 * @author Harold
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class AlerteMapper {

    /**
     * Convertit une entité Alerte en AlerteDTO.
     *
     * <p>Les informations de l'étudiant et de la promotion
     * sont extraites depuis l'entité et l'inscription active.</p>
     *
     * @param alerte        l'entité à convertir
     * @param inscription   l'inscription active de l'étudiant
     * @return              le DTO correspondant
     */
    public AlerteDTO toDTO(Alerte alerte, Inscription inscription) {
        AlerteDTO dto = new AlerteDTO();
        dto.setId(alerte.getId());
        dto.setScoreRisque(alerte.getScoreRisque());
        dto.setNiveau(alerte.getNiveau().name());
        dto.setLue(alerte.isLue());
        dto.setCreatedAt(alerte.getCreatedAt());
        dto.setDetailsJson(alerte.getDetailsJson());

        // Informations année académique
        if (alerte.getAnneeAcademique() != null) {
            dto.setAnneeAcademique(
                    alerte.getAnneeAcademique().getAnnee());
        }

        // Informations étudiant
        if (alerte.getEtudiant() != null) {
            dto.setEtudiantId(alerte.getEtudiant().getId());
            dto.setEtudiantNom(alerte.getEtudiant().getNom());
            dto.setEtudiantPrenom(alerte.getEtudiant().getPrenom());
            dto.setNumeroEtudiant(
                    alerte.getEtudiant().getNumeroEtudiant());
        }

        // Informations promotion depuis inscription
        if (inscription != null) {
            dto.setPromotionNom(inscription.getPromotion().getNom());
        }

        return dto;
    }
}