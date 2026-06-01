package org.harold.plateforme.mapper;

import lombok.RequiredArgsConstructor;
import org.harold.plateforme.dto.note.NoteDTO;
import org.harold.plateforme.dto.note.NoteUpdateDTO;
import org.harold.plateforme.entity.Note;
import org.springframework.stereotype.Component;

/**
 * Mapper pour la conversion entre l'entité Note et ses DTOs.
 *
 * <p>La conversion NoteCreateDTO vers Note n'est pas faite ici
 * car elle nécessite plusieurs recherches en base pour récupérer
 * les entités liées (etudiant, matiere, anneeAcademique, groupeClasse).
 * Cette logique est gérée directement dans NoteService.</p>
 *
 * @author Harold
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class NoteMapper {

    /**
     * Convertit une entité Note en NoteDTO.
     *
     * <p>Extrait les informations des entités liées :
     * étudiant, matière, année académique, groupe et enseignant.</p>
     *
     * @param note  l'entité à convertir
     * @return      le DTO correspondant
     */
    public NoteDTO toDTO(Note note) {
        NoteDTO dto = new NoteDTO();
        dto.setId(note.getId());
        dto.setValeur(note.getValeur());
        dto.setType(note.getType().name());
        dto.setEstRattrapage(note.isEstRattrapage());
        dto.setSaisieLe(note.getSaisieLe());

        // Informations étudiant
        if (note.getEtudiant() != null) {
            dto.setEtudiantId(note.getEtudiant().getId());
            dto.setEtudiantNom(note.getEtudiant().getNom());
            dto.setEtudiantPrenom(note.getEtudiant().getPrenom());
        }

        // Informations matière
        if (note.getMatiere() != null) {
            dto.setMatiereId(note.getMatiere().getId());
            dto.setMatiereNom(note.getMatiere().getNom());
        }

        // Informations année académique
        if (note.getAnneeAcademique() != null) {
            dto.setAnneeAcademiqueId(note.getAnneeAcademique().getId());
            dto.setAnneeAcademique(note.getAnneeAcademique().getAnnee());
        }

        // Informations groupe classe
        if (note.getGroupeClasse() != null) {
            dto.setGroupeNom(note.getGroupeClasse().getNom());
        }

        // Informations enseignant qui a saisi la note
        if (note.getSaisiPar() != null) {
            dto.setSaisiParNom(note.getSaisiPar().getNom());
            dto.setSaisiParPrenom(note.getSaisiPar().getPrenom());
        }

        return dto;
    }

    /**
     * Met à jour une entité Note existante depuis un NoteUpdateDTO.
     *
     * <p>Seules la valeur et le statut rattrapage sont modifiables.
     * L'étudiant, la matière, l'année et le type ne peuvent pas
     * être modifiés après création.</p>
     *
     * @param dto   le DTO de modification
     * @param note  l'entité existante à mettre à jour
     */
    public void updateEntity(NoteUpdateDTO dto, Note note) {
        note.setValeur(dto.getValeur());
        note.setEstRattrapage(dto.getEstRattrapage());
    }
}